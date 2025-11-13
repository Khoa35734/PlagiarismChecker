package controller;

import jakarta.servlet.RequestDispatcher;
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
        Integer userId = ensureAuthenticated(request, response);
        if (userId == null) {
            return;
        }

        try {
            List<ResultRow> results = loadResults(userId);
            request.setAttribute("results", results);
        } catch (SQLException ex) {
            request.setAttribute("error", "Unable to load results: " + ex.getMessage());
        }

        RequestDispatcher dispatcher = request.getRequestDispatcher("/jsp/result.jsp");
        dispatcher.forward(request, response);
    }

    private Integer ensureAuthenticated(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return null;
        }
        return (Integer) session.getAttribute("userId");
    }

    private List<ResultRow> loadResults(int userId) throws SQLException {
        String sql = "SELECT r.id, r.submission_id, s.filename AS submission_name, r.compared_with, s2.filename AS compared_name, r.similarity, s.upload_time "
                + "FROM Results r "
                + "JOIN Submissions s ON r.submission_id = s.id "
                + "LEFT JOIN Submissions s2 ON r.compared_with = s2.id "
                + "WHERE s.user_id = ? "
                + "ORDER BY r.similarity DESC";

        List<ResultRow> rows = new ArrayList<>();
        try (Connection connection = DatabaseUtils.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, userId);
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    ResultRow row = new ResultRow();
                    row.id = rs.getInt("id");
                    row.submissionId = rs.getInt("submission_id");
                    row.submissionName = rs.getString("submission_name");
                    row.comparedWith = rs.getInt("compared_with");
                    row.comparedName = rs.getString("compared_name");
                    row.similarity = rs.getFloat("similarity");
                    row.uploadTime = rs.getTimestamp("upload_time");
                    rows.add(row);
                }
            }
        }
        return rows;
    }

    public static class ResultRow {
        public int id;
        public int submissionId;
        public String submissionName;
        public int comparedWith;
        public String comparedName;
        public float similarity;
        public java.sql.Timestamp uploadTime;
    }
}
