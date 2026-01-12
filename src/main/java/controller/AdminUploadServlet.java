package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;
import model.bo.DocumentBO;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
 
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@MultipartConfig(
    // No application-level limit for admin uploads: allow the container to accept large files.
    // Use -1L for maxFileSize and maxRequestSize to indicate "unlimited" where supported.
    fileSizeThreshold = 0,
    maxFileSize = -1L,
    maxRequestSize = -1L
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
            // Prefer a persistent upload directory configured in web.xml (context-param "persistentUploadDir").
            // If not configured, fall back to the webapp's documents folder (not ideal for persistence across redeploys).
            String configured = request.getServletContext().getInitParameter("persistentUploadDir");
            String uploadFilePath;
            boolean storeAbsolute = false;
            if (configured != null && !configured.isBlank()) {
                uploadFilePath = configured.trim();
                // If configured path is relative, resolve against applicationPath
                File cfgFile = new File(uploadFilePath);
                if (!cfgFile.isAbsolute()) {
                    uploadFilePath = applicationPath + File.separator + uploadFilePath;
                } else {
                    storeAbsolute = true;
                }
            } else {
                uploadFilePath = applicationPath + File.separator + UPLOAD_DIR;
            }
            File uploadDir = new File(uploadFilePath);
            if (!uploadDir.exists() && !uploadDir.mkdirs()) {
                throw new IOException("Unable to create upload directory at " + uploadFilePath);
            }

            for (Part part : parts) {
                if (part.getName().equals("file") && part.getSize() > 0) {
                    String fileName = Paths.get(part.getSubmittedFileName()).getFileName().toString();
                    // Prevent duplicate filenames for the same admin
                    DocumentBO dbo = new DocumentBO();
                    if (dbo.documentExists(adminId, fileName)) {
                        response.setStatus(HttpServletResponse.SC_CONFLICT);
                        response.getWriter().write("{\"error\": \"A document with the name '" + fileName + "' already exists. Please rename the file before uploading.\"}");
                        return;
                    }
                    String filePath = uploadFilePath + File.separator + fileName;
                    // Also prevent uploading a file that already exists on disk
                    File existing = new File(filePath);
                    if (existing.exists()) {
                        response.setStatus(HttpServletResponse.SC_CONFLICT);
                        response.getWriter().write("{\"error\": \"A document with the name '" + fileName + "' already exists on disk. Please rename the file before uploading.\"}");
                        return;
                    }
                    
                    try (InputStream fileContent = part.getInputStream()) {
                        Files.copy(fileContent, Paths.get(filePath), StandardCopyOption.REPLACE_EXISTING);
                        // Store relative path if we used the webapp documents folder; otherwise store absolute path
                        String storedPath;
                        if (storeAbsolute) {
                            storedPath = filePath.replace("\\", "/"); // normalize for DB
                        } else {
                            // compute relative path from applicationPath if possible
                            if (filePath.startsWith(applicationPath)) {
                                String rel = filePath.substring(applicationPath.length());
                                if (rel.startsWith(File.separator) || rel.startsWith("/")) rel = rel.substring(1);
                                storedPath = rel.replace("\\", "/");
                            } else {
                                storedPath = UPLOAD_DIR + "/" + fileName;
                            }
                        }
                        dbo.saveDocument(adminId, fileName, storedPath, part.getSize(), part.getContentType());
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

    // persistence moved to model.bo.DocumentBO / model.dao.DocumentDAO
}
