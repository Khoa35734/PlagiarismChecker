package controller;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import model.DatabaseUtils;
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

    @Override
    public void contextInitialized(ServletContextEvent sce) {
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
        String sql = "SELECT id, cleaned_content FROM Submissions WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, submissionId);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return new SubmissionData(rs.getInt("id"), rs.getString("cleaned_content"));
                }
            }
        }
        return null;
    }

    private static List<SubmissionData> loadOtherSubmissions(Connection connection, int submissionId) throws SQLException {
        String sql = "SELECT id, cleaned_content FROM Submissions WHERE id <> ?";
        List<SubmissionData> list = new ArrayList<>();
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, submissionId);
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    list.add(new SubmissionData(rs.getInt("id"), rs.getString("cleaned_content")));
                }
            }
        }
        return list;
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
        String insertSql = "INSERT INTO Results (submission_id, similarity_winnowing, similarity_tfidf, matched_segments, status, source_document) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(insertSql)) {
            for (SubmissionData other : others) {
                double similarity = TextSimilarity.jaccardSimilarity(target.content(), other.content());
                double tfidf = similarity; // placeholder until TF-IDF module is implemented
                String segmentsJson = "[]";
                String status = similarity > 0.5 ? "Plagiarism Suspected" : "No Issues";
                statement.setInt(1, target.id());
                statement.setDouble(2, similarity);
                statement.setDouble(3, tfidf);
                statement.setString(4, segmentsJson);
                statement.setString(5, status);
                statement.setString(6, "Submission " + other.id());
                statement.addBatch();
            }
            statement.executeBatch();
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

    private record SubmissionData(int id, String content) {
    }
}
