package controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
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

@WebServlet("/admin/dashboard")
public class AdminDashboardServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        Integer adminId = session != null ? (Integer) session.getAttribute("adminId") : null;
        if (session == null || session.getAttribute("adminLoggedIn") == null || adminId == null) {
            response.sendRedirect(request.getContextPath() + "/jsp/adminLogin.jsp");
            return;
        }

        List<DocumentRow> documents = new ArrayList<>();
        String sql = "SELECT id, filename, upload_time, filesize FROM Documents WHERE owner_id = ? ORDER BY upload_time DESC";

        try (Connection connection = DatabaseUtils.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, adminId);
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    DocumentRow row = new DocumentRow();
                    row.id = rs.getInt("id");
                    row.filename = rs.getString("filename");
                    row.uploadTime = rs.getTimestamp("upload_time");
                    row.filesize = rs.getLong("filesize");
                    documents.add(row);
                }
            }
        } catch (SQLException e) {
            throw new ServletException("Database error while fetching documents", e);
        }

        request.setAttribute("documents", documents);
        RequestDispatcher dispatcher = request.getRequestDispatcher("/jsp/adminDashboard.jsp");
        dispatcher.forward(request, response);
    }

    public static class DocumentRow {
        public int id;
        public String filename;
        public long filesize;
        public java.sql.Timestamp uploadTime;

        public int getId() {
            return id;
        }

        public String getFilename() {
            return filename;
        }

        public long getFilesize() {
            return filesize;
        }

        public java.sql.Timestamp getUploadTime() {
            return uploadTime;
        }
    }
}
