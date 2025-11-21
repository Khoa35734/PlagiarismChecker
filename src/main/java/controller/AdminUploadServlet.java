package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;
import model.DatabaseUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@MultipartConfig
public class AdminUploadServlet extends HttpServlet {
    private static final String UPLOAD_DIR = "documents";
    private static final long MAX_TOTAL_BYTES = Long.MAX_VALUE;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        Integer adminId = session != null ? (Integer) session.getAttribute("adminId") : null;
        if (session == null || session.getAttribute("adminLoggedIn") == null || adminId == null) {
            response.sendRedirect(request.getContextPath() + "/jsp/adminLogin.jsp");
            return;
        }
        request.setAttribute("documents", loadDocuments(adminId));
        request.getRequestDispatcher("/jsp/adminUpload.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        Integer adminId = session != null ? (Integer) session.getAttribute("adminId") : null;
        if (session == null || session.getAttribute("adminLoggedIn") == null || adminId == null) {
            response.sendRedirect(request.getContextPath() + "/jsp/adminLogin.jsp");
            return;
        }

        Collection<Part> parts = request.getParts();
        long totalBytes = 0L;
        List<String> uploaded = new ArrayList<>();

        for (Part part : parts) {
            if (!"file".equals(part.getName()) || part.getSize() <= 0) {
                continue;
            }
            totalBytes += part.getSize();
            String fileName = Paths.get(part.getSubmittedFileName()).getFileName().toString();
            String applicationPath = request.getServletContext().getRealPath("");
            String uploadFilePath = applicationPath + File.separator + UPLOAD_DIR;

            File uploadDir = new File(uploadFilePath);
            if (!uploadDir.exists() && !uploadDir.mkdirs()) {
                request.setAttribute("error", "Unable to create upload directory");
                request.getRequestDispatcher("/jsp/adminUpload.jsp").forward(request, response);
                return;
            }

            String filePath = uploadFilePath + File.separator + fileName;
            try (InputStream fileContent = part.getInputStream()) {
                Files.copy(fileContent, Paths.get(filePath), StandardCopyOption.REPLACE_EXISTING);
                saveDocument(adminId, fileName, UPLOAD_DIR + "/" + fileName, part.getSize(), part.getContentType());
                uploaded.add(fileName);
            } catch (SQLException e) {
                request.setAttribute("error", "Database error while saving metadata: " + e.getMessage());
                request.getRequestDispatcher("/jsp/adminUpload.jsp").forward(request, response);
                return;
            }
        }

        if (uploaded.isEmpty()) {
            request.setAttribute("error", "Please select at least one file.");
        } else {
            request.setAttribute("message", "Uploaded: " + String.join(", ", uploaded));
        }
        request.setAttribute("documents", loadDocuments(adminId));
        request.getRequestDispatcher("/jsp/adminUpload.jsp").forward(request, response);
    }

    private List<AdminDashboardServlet.DocumentRow> loadDocuments(int adminId) throws ServletException {
        String sql = "SELECT id, filename, upload_time, filesize FROM Documents WHERE owner_id = ? ORDER BY upload_time DESC";
        List<AdminDashboardServlet.DocumentRow> rows = new ArrayList<>();
        try (Connection connection = DatabaseUtils.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, adminId);
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    AdminDashboardServlet.DocumentRow row = new AdminDashboardServlet.DocumentRow();
                    row.id = rs.getInt("id");
                    row.filename = rs.getString("filename");
                    row.uploadTime = rs.getTimestamp("upload_time");
                    row.filesize = rs.getLong("filesize");
                    rows.add(row);
                }
            }
        } catch (SQLException e) {
            throw new ServletException("Unable to load document list", e);
        }
        return rows;
    }

    private void saveDocument(int ownerId, String filename, String filepath, long size, String mimeType) throws SQLException {
        String sql = "INSERT INTO Documents (owner_id, filename, filepath, filesize, mime_type) VALUES (?, ?, ?, ?, ?)";
        try (Connection connection = DatabaseUtils.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, ownerId);
            statement.setString(2, filename);
            statement.setString(3, filepath);
            statement.setLong(4, size);
            statement.setString(5, mimeType);
            statement.executeUpdate();
        }
    }
}
