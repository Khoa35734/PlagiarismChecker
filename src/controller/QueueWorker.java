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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class QueueWorker implements ServletContextListener {
    private static final BlockingQueue<Integer> QUEUE = new LinkedBlockingQueue<>();
    private static ExecutorService executor;
    private static volatile boolean running;
    private static ServletContext servletContext;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        servletContext = sce.getServletContext();
        String jdbcUrl = servletContext.getInitParameter("jdbcUrl");
        String jdbcUser = servletContext.getInitParameter("jdbcUser");
        String jdbcPassword = servletContext.getInitParameter("jdbcPassword");
        DatabaseUtils.configure(jdbcUrl, jdbcUser, jdbcPassword);

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

    public static void enqueueSubmission(int submissionId) {
        QUEUE.offer(submissionId);
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

    private static void processSubmission(int submissionId) throws SQLException {
        List<SubmissionData> comparisonPool = new ArrayList<>();
        SubmissionData targetSubmission = null;

        try (Connection connection = DatabaseUtils.getConnection()) {
            targetSubmission = loadSubmission(connection, submissionId);
            if (targetSubmission == null) {
                log("Submission " + submissionId + " not found", null);
                return;
            }

            comparisonPool = loadOtherSubmissions(connection, submissionId);
            clearPreviousResults(connection, submissionId);
            insertResults(connection, targetSubmission, comparisonPool);
        }
    }

    private static SubmissionData loadSubmission(Connection connection, int submissionId) throws SQLException {
        String sql = "SELECT id, content FROM Submissions WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, submissionId);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return new SubmissionData(rs.getInt("id"), rs.getString("content"));
                }
            }
        }
        return null;
    }

    private static List<SubmissionData> loadOtherSubmissions(Connection connection, int submissionId) throws SQLException {
        String sql = "SELECT id, content FROM Submissions WHERE id <> ?";
        List<SubmissionData> list = new ArrayList<>();
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, submissionId);
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    list.add(new SubmissionData(rs.getInt("id"), rs.getString("content")));
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
            return;
        }
        String insertSql = "INSERT INTO Results (submission_id, compared_with, similarity) VALUES (?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(insertSql)) {
            for (SubmissionData other : others) {
                double similarity = TextSimilarity.jaccardSimilarity(target.content(), other.content());
                statement.setInt(1, target.id());
                statement.setInt(2, other.id());
                statement.setDouble(3, similarity);
                statement.addBatch();
            }
            statement.executeBatch();
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
