package controller;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import model.DatabaseUtils;
import utils.FileParser;
import utils.TextCleaner;
import utils.TextSimilarity;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class QueueWorker implements ServletContextListener {
    private static final BlockingQueue<Integer> QUEUE = new LinkedBlockingQueue<>();
    private static final ConcurrentHashMap<Integer, CompletableFuture<Void>> FUTURES = new ConcurrentHashMap<>();
    private static ExecutorService executor;
    private static volatile boolean running;
    private static ServletContext servletContext;
    private static boolean initialized = false;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        if (initialized) return;
        initialized = true;
        servletContext = sce.getServletContext();

        running = true;
        executor = Executors.newSingleThreadExecutor(runnable -> {
            Thread worker = new Thread(runnable, "PlagiarismQueueWorker");
            worker.setDaemon(true);
            return worker;
        });
        executor.submit(QueueWorker::processLoop);
        servletContext.log("QueueWorker initialized");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        running = false;
        if (executor != null) {
            executor.shutdownNow();
            try {
                executor.awaitTermination(5, TimeUnit.SECONDS);
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
        }
        servletContext = null;
    }

    public static CompletableFuture<Void> enqueueSubmission(int submissionId) {
        CompletableFuture<Void> future = new CompletableFuture<>();
        FUTURES.put(submissionId, future);
        QUEUE.offer(submissionId);
        return future;
    }

    private static void processLoop() {
        while (running) {
            try {
                Integer submissionId = QUEUE.take();
                processSubmission(submissionId);
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
                break;
            } catch (Exception ex) {
                log("Failed to process submission: " + ex.getMessage(), ex);
            }
        }
    }

    private static void processSubmission(int submissionId) {
        try (Connection connection = DatabaseUtils.getConnection()) {
            SubmissionData target = loadSubmission(connection, submissionId);
            if (target == null) {
                log("Submission " + submissionId + " not found", null);
                complete(submissionId);
                return;
            }
            updateStatus(connection, submissionId, "PROCESSING");
            List<SubmissionData> others = loadOtherSubmissions(connection, submissionId);
            clearPreviousResults(connection, submissionId);
            insertResults(connection, target, others);
            updateStatus(connection, submissionId, "DONE");
        } catch (SQLException ex) {
            log("Processing failed for submission " + submissionId + ": " + ex.getMessage(), ex);
        } finally {
            complete(submissionId);
        }
    }

    private static void updateStatus(Connection connection, int submissionId, String status) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement("UPDATE Submissions SET status = ? WHERE id = ?")) {
            statement.setString(1, status);
            statement.setInt(2, submissionId);
            statement.executeUpdate();
        }
    }

    private static SubmissionData loadSubmission(Connection connection, int submissionId) throws SQLException {
        String sql = "SELECT id, cleaned_content, filename FROM Submissions WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, submissionId);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return new SubmissionData(rs.getInt("id"), rs.getString("cleaned_content"), rs.getString("filename"));
                }
            }
        }
        return null;
    }

    private static List<SubmissionData> loadOtherSubmissions(Connection connection, int submissionId) throws SQLException {
        // Compare with Documents repository (admin-uploaded reference documents)
        List<SubmissionData> list = new ArrayList<>();

        // First, try to get documents from admin repository
        String docSql = "SELECT d.id, d.filename, d.filepath FROM Documents d ORDER BY d.upload_time DESC";
        try (PreparedStatement statement = connection.prepareStatement(docSql);
             ResultSet rs = statement.executeQuery()) {
            while (rs.next()) {
                int docId = rs.getInt("id");
                String filename = rs.getString("filename");
                String filepath = rs.getString("filepath");

                // Extract text from document file
                String content = extractDocumentContent(filepath, filename);
                if (content != null && !content.trim().isEmpty()) {
                    list.add(new SubmissionData(docId, content, filename));
                }
            }
        }

        // If no documents in repository, fall back to comparing with previous submissions
        if (list.isEmpty()) {
            String subSql = "SELECT id, cleaned_content, filename FROM Submissions WHERE id < ? AND status = 'DONE' AND cleaned_content IS NOT NULL";
            try (PreparedStatement statement = connection.prepareStatement(subSql)) {
                statement.setInt(1, submissionId);
                try (ResultSet rs = statement.executeQuery()) {
                    while (rs.next()) {
                        String content = rs.getString("cleaned_content");
                        if (content != null && !content.trim().isEmpty()) {
                            list.add(new SubmissionData(rs.getInt("id"), content, rs.getString("filename")));
                        }
                    }
                }
            }
        }

        return list;
    }

    private static String extractDocumentContent(String filepath, String filename) {
        try {
            // Get the actual file path from the webapp
            if (servletContext != null) {
                String realPath = servletContext.getRealPath(filepath);
                if (realPath != null) {
                    java.io.File file = new java.io.File(realPath);
                    if (file.exists()) {
                        // Use FileParser to extract text
                        try (java.io.FileInputStream fis = new java.io.FileInputStream(file)) {
                            String extractedText = FileParser.extractText(fis, filename);
                            // Clean the text
                            return TextCleaner.clean(extractedText);
                        }
                    }
                }
            }
        } catch (Exception e) {
            log("Failed to extract content from document: " + filename + " - " + e.getMessage(), e);
        }
        return null;
    }

    private static void clearPreviousResults(Connection connection, int submissionId) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement("DELETE FROM Results WHERE submission_id = ?")) {
            statement.setInt(1, submissionId);
            statement.executeUpdate();
        }
    }

    private static void insertResults(Connection connection, SubmissionData target, List<SubmissionData> others) throws SQLException {
        if (others.isEmpty()) {
            insertEmptyResult(connection, target.id());
            return;
        }

        // Calculate overall similarity across all documents
        double maxSimilarity = 0.0;
        String maxSource = null;
        StringBuilder allSegments = new StringBuilder("[");

        for (SubmissionData other : others) {
            double similarity = TextSimilarity.jaccardSimilarity(target.content(), other.content());
            if (similarity > maxSimilarity) {
                maxSimilarity = similarity;
                maxSource = other.filename() != null ? other.filename() : "Document #" + other.id();
            }

            // Calculate matched segments
            if (similarity > 0.1) { // Only include if similarity > 10%
                String docName = other.filename() != null ? other.filename() : "Document #" + other.id();
                String segments = TextSimilarity.findMatchedSegmentsWithSource(target.content(), other.content(), docName);
                if (!segments.equals("[]") && allSegments.length() > 1) {
                    allSegments.append(",");
                }
                if (!segments.equals("[]")) {
                    allSegments.append(segments, 1, segments.length() - 1); // Remove outer brackets
                }
            }
        }
        allSegments.append("]");

        String status;
        if (maxSimilarity > 0.5) {
            status = "Plagiarism Suspected";
        } else if (maxSimilarity > 0.2) {
            status = "Partial Match";
        } else {
            status = "No Issues";
        }

        String insertSql = "INSERT INTO Results (submission_id, similarity_winnowing, similarity_tfidf, matched_segments, status, source_document) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(insertSql)) {
            statement.setInt(1, target.id());
            statement.setDouble(2, maxSimilarity);
            statement.setDouble(3, maxSimilarity); // Use same value for now
            statement.setString(4, allSegments.toString());
            statement.setString(5, status);
            statement.setString(6, maxSource);
            statement.executeUpdate();
        }
    }

    private static void insertEmptyResult(Connection connection, int submissionId) throws SQLException {
        String sql = "INSERT INTO Results (submission_id, similarity_winnowing, similarity_tfidf, matched_segments, status, source_document) " +
                     "VALUES (?, 0, 0, '[]', 'No Issues', NULL)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, submissionId);
            statement.executeUpdate();
        }
    }

    private static void complete(int submissionId) {
        CompletableFuture<Void> future = FUTURES.remove(submissionId);
        if (future != null) {
            future.complete(null);
        }
    }

    private static void log(String message, Throwable throwable) {
        if (servletContext != null) {
            servletContext.log(message, throwable);
        }
    }

    private record SubmissionData(int id, String content, String filename) {
        // Constructor for submissions without filename
        SubmissionData(int id, String content) {
            this(id, content, null);
        }
    }
}
