package model.dao;

import model.DatabaseUtils;
import model.bean.ResultBean;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import model.bean.ResultWithSubmissionBean;

/**
 * DAO for Result entity. Performs simple CRUD operations.
 */
public class ResultDAO {

    public int insert(ResultBean r) throws Exception {
        String sql = "INSERT INTO results (submission_id, similarity_winnowing, similarity_tfidf, matched_segments_json, status, source_document, processed_at) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection c = DatabaseUtils.getConnection(); PreparedStatement ps = c.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, r.getSubmissionId());
            ps.setDouble(2, r.getSimilarityWinnowing());
            ps.setDouble(3, r.getSimilarityTfidf());
            ps.setString(4, r.getMatchedSegmentsJson());
            ps.setString(5, r.getStatus());
            ps.setString(6, r.getSourceDocument());
            ps.setTimestamp(7, r.getProcessedAt() == null ? null : Timestamp.from(r.getProcessedAt()));
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getInt(1);
            }
        }
        return -1;
    }

    public ResultBean findBySubmissionId(int submissionId) throws Exception {
        String sql = "SELECT id, submission_id, similarity_winnowing, similarity_tfidf, matched_segments_json, status, source_document, processed_at FROM results WHERE submission_id = ? ORDER BY processed_at DESC LIMIT 1";
        try (Connection c = DatabaseUtils.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, submissionId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    ResultBean r = new ResultBean();
                    r.setId(rs.getInt("id"));
                    r.setSubmissionId(rs.getInt("submission_id"));
                    r.setSimilarityWinnowing(rs.getDouble("similarity_winnowing"));
                    r.setSimilarityTfidf(rs.getDouble("similarity_tfidf"));
                    r.setMatchedSegmentsJson(rs.getString("matched_segments_json"));
                    r.setStatus(rs.getString("status"));
                    r.setSourceDocument(rs.getString("source_document"));
                    Timestamp t = rs.getTimestamp("processed_at");
                    if (t != null) r.setProcessedAt(t.toInstant());
                    return r;
                }
            }
        }
        return null;
    }

    public boolean deleteBySubmissionId(int submissionId) throws Exception {
        String sql = "DELETE FROM results WHERE submission_id = ?";
        try (Connection c = DatabaseUtils.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, submissionId);
            int updated = ps.executeUpdate();
            return updated > 0;
        }
    }

    /**
     * List results joined with submissions according to provided filters.
     */
    public List<ResultWithSubmissionBean> listResultsWithSubmission(Integer userId, String role, String guestToken, String batchToken) throws Exception {
        List<ResultWithSubmissionBean> rows = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
                "SELECT r.id, r.submission_id, s.filename, s.status AS submission_status, r.similarity_winnowing, r.similarity_tfidf, r.matched_segments, r.status, r.source_document, s.upload_time " +
                        "FROM Results r JOIN Submissions s ON r.submission_id = s.id "
        );
        java.util.List<Object> params = new java.util.ArrayList<>();
        java.util.List<String> predicates = new java.util.ArrayList<>();

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

        try (Connection c = DatabaseUtils.getConnection(); PreparedStatement ps = c.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) ps.setObject(i + 1, params.get(i));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ResultWithSubmissionBean row = new ResultWithSubmissionBean();
                    row.setResultId(rs.getInt("id"));
                    row.setSubmissionId(rs.getInt("submission_id"));
                    row.setFilename(rs.getString("filename"));
                    row.setSubmissionStatus(rs.getString("submission_status"));
                    row.setSimWinnowing(rs.getDouble("similarity_winnowing"));
                    row.setSimTfidf(rs.getDouble("similarity_tfidf"));
                    row.setMatchedSegmentsJson(rs.getString("matched_segments"));
                    row.setStatus(rs.getString("status"));
                    row.setSourceDocument(rs.getString("source_document"));
                    row.setUploadTime(rs.getTimestamp("upload_time"));
                    rows.add(row);
                }
            }
        }
        return rows;
    }
}
