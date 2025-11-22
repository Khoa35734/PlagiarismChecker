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
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@MultipartConfig(
    fileSizeThreshold = 1024 * 1024, // 1 MB
    maxFileSize = 1024 * 1024 * 10,      // 10 MB
    maxRequestSize = 1024 * 1024 * 25    // 25 MB
)
public class AdminUploadServlet extends HttpServlet {
    private static final String UPLOAD_DIR = "documents";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        Integer adminId = (session != null) ? (Integer) session.getAttribute("adminId") : null;
        Boolean adminLoggedIn = (session != null) ? (Boolean) session.getAttribute("adminLoggedIn") : null;
        
        if (session == null || adminId == null || adminLoggedIn == null || !adminLoggedIn) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        request.getRequestDispatcher("/jsp/adminUpload.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession(false);
        Integer adminId = (session != null) ? (Integer) session.getAttribute("adminId") : null;

        if (adminId == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"error\": \"Authentication required. Please log in again.\"}");
            return;
        }

        try {
            Collection<Part> parts = request.getParts();
            List<String> uploadedFiles = new ArrayList<>();
            
            if (parts.isEmpty() || parts.stream().allMatch(p -> p.getSize() == 0)) {
                 response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                 response.getWriter().write("{\"error\": \"Please select at least one file to upload.\"}");
                 return;
            }

            String applicationPath = request.getServletContext().getRealPath("");
            String uploadFilePath = applicationPath + File.separator + UPLOAD_DIR;
            File uploadDir = new File(uploadFilePath);
            if (!uploadDir.exists() && !uploadDir.mkdirs()) {
                throw new IOException("Unable to create upload directory at " + uploadFilePath);
            }

            for (Part part : parts) {
                if (part.getName().equals("file") && part.getSize() > 0) {
                    String fileName = Paths.get(part.getSubmittedFileName()).getFileName().toString();
                    String filePath = uploadFilePath + File.separator + fileName;
                    
                    try (InputStream fileContent = part.getInputStream()) {
                        Files.copy(fileContent, Paths.get(filePath), StandardCopyOption.REPLACE_EXISTING);
                        saveDocument(adminId, fileName, UPLOAD_DIR + "/" + fileName, part.getSize(), part.getContentType());
                        uploadedFiles.add(fileName);
                    }
                }
            }

            if (uploadedFiles.isEmpty()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("{\"error\": \"No valid files were uploaded.\"}");
            } else {
                response.setStatus(HttpServletResponse.SC_OK);
                String fileList = String.join(", ", uploadedFiles);
                response.getWriter().write("{\"message\": \"Successfully uploaded " + uploadedFiles.size() + " file(s): " + fileList + "\"}");
            }

        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\": \"An unexpected error occurred: " + e.getMessage() + "\"}");
        }
    }

    private void saveDocument(int ownerId, String filename, String filepath, long size, String mimeType) throws SQLException {
        String sql = "INSERT INTO Documents (owner_id, filename, filepath, filesize, mime_type) VALUES (?, ?, ?, ?, ?) " +
                     "ON DUPLICATE KEY UPDATE filepath=VALUES(filepath), filesize=VALUES(filesize), upload_time=NOW()";
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
