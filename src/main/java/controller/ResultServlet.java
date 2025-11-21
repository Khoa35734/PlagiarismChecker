package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.DatabaseUtils;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ResultServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        Integer userId = session != null ? (Integer) session.getAttribute("userId") : null;
        String role = session != null ? (String) session.getAttribute("role") : null;
        String guestToken = session != null ? (String) session.getAttribute("guestToken") : null;
        String batchToken = request.getParameter("batch");

        try {
            List<ResultWithSubmission> results = loadResults(userId, role, guestToken, batchToken);
            request.setAttribute("results", results);
        } catch (SQLException ex) {
            request.setAttribute("error", "Unable to load results: " + ex.getMessage());
        }

        request.getRequestDispatcher("/jsp/result.jsp").forward(request, response);
    }

    private List<ResultWithSubmission> loadResults(Integer userId, String role, String guestToken, String batchToken) throws SQLException {
        List<ResultWithSubmission> rows = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
                "SELECT r.id, r.submission_id, s.filename, s.status AS submission_status, r.similarity_winnowing, r.similarity_tfidf, r.matched_segments, r.status, r.source_document, s.upload_time " +
                "FROM Results r " +
                "JOIN Submissions s ON r.submission_id = s.id "
        );
        List<Object> params = new ArrayList<>();
        List<String> predicates = new ArrayList<>();

        if ("ADMIN".equals(role)) {
            if (batchToken != null && !batchToken.isBlank()) {
                predicates.add("s.batch_token = ?");
                params.add(batchToken);
            }
        } else if (userId != null) {
            predicates.add("s.user_id = ?");
            params.add(userId);
        } else if (batchToken != null && !batchToken.isBlank()) {
            predicates.add("s.batch_token = ?");
            params.add(batchToken);
        } else if (guestToken != null) {
            predicates.add("s.guest_token = ?");
            params.add(guestToken);
        } else {
            predicates.add("1 = 0");
        }

        if (!predicates.isEmpty()) {
            sql.append(" WHERE ").append(String.join(" AND ", predicates));
        }
        sql.append(" ORDER BY s.upload_time DESC");

        try (Connection connection = DatabaseUtils.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                statement.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    ResultWithSubmission row = new ResultWithSubmission();
                    row.resultId = rs.getInt("id");
                    row.submissionId = rs.getInt("submission_id");
                    row.filename = rs.getString("filename");
                    row.submissionStatus = rs.getString("submission_status");
                    row.simWinnowing = rs.getDouble("similarity_winnowing");
                    row.simTfidf = rs.getDouble("similarity_tfidf");
                    row.matchedSegmentsJson = rs.getString("matched_segments");
                    row.status = rs.getString("status");
                    row.sourceDocument = rs.getString("source_document");
                    row.uploadTime = rs.getTimestamp("upload_time");
                    rows.add(row);
                }
            }
        }
        return rows;
    }

    public static class ResultWithSubmission {
        private int resultId;
        private int submissionId;
        private String filename;
        private String submissionStatus;
        private double simWinnowing;
        private double simTfidf;
        private String matchedSegmentsJson;
        private String status;
        private String sourceDocument;
        private java.sql.Timestamp uploadTime;

        public int getResultId() { return resultId; }
        public void setResultId(int resultId) { this.resultId = resultId; }

        public int getSubmissionId() { return submissionId; }
        public void setSubmissionId(int submissionId) { this.submissionId = submissionId; }

        public String getFilename() { return filename; }
        public void setFilename(String filename) { this.filename = filename; }

        public String getSubmissionStatus() { return submissionStatus; }
        public void setSubmissionStatus(String submissionStatus) { this.submissionStatus = submissionStatus; }

        public double getSimWinnowing() { return simWinnowing; }
        public void setSimWinnowing(double simWinnowing) { this.simWinnowing = simWinnowing; }

        public double getSimTfidf() { return simTfidf; }
        public void setSimTfidf(double simTfidf) { this.simTfidf = simTfidf; }

        public String getMatchedSegmentsJson() { return matchedSegmentsJson; }
        public void setMatchedSegmentsJson(String matchedSegmentsJson) { this.matchedSegmentsJson = matchedSegmentsJson; }

        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }

        public String getSourceDocument() { return sourceDocument; }
        public void setSourceDocument(String sourceDocument) { this.sourceDocument = sourceDocument; }

        public java.sql.Timestamp getUploadTime() { return uploadTime; }
        public void setUploadTime(java.sql.Timestamp uploadTime) { this.uploadTime = uploadTime; }
    }
}
