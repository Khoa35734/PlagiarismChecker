package controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.bo.DocumentBO;
import model.bean.DocumentBean;

import java.io.File;
import java.io.IOException;
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
        DocumentBO dbo = new DocumentBO();
        List<DocumentBean> beans = dbo.listDocumentsForAdmins();
        for (DocumentBean b : beans) {
            DocumentRow row = new DocumentRow();
            row.id = b.getId();
            row.filename = b.getOriginalName();
            row.filesize = b.getSize();
            row.uploadTime = b.getUploadedAt() == null ? null : new java.sql.Timestamp(b.getUploadedAt().toEpochMilli());
            row.ownerName = "admin"; // username not available in bean; UI can call owner id lookup if needed
            documents.add(row);
        }
        request.setAttribute("documents", documents);
        // If DB has no documents (e.g., uploads saved to file system but not inserted),
        // try to populate from the persistent upload directory so admin sees files.
        if (documents.isEmpty()) {
            String appPath = request.getServletContext().getRealPath("");
            String configured = request.getServletContext().getInitParameter("persistentUploadDir");
            String uploadDirPath = null;
            if (configured != null && !configured.isBlank()) {
                File cfg = new File(configured.trim());
                if (!cfg.isAbsolute()) uploadDirPath = appPath + File.separator + configured.trim();
                else uploadDirPath = configured.trim();
            } else {
                uploadDirPath = appPath + File.separator + "documents";
            }
            File uploadDir = new File(uploadDirPath);
            if (uploadDir.exists() && uploadDir.isDirectory()) {
                File[] files = uploadDir.listFiles();
                if (files != null) {
                    for (File f : files) {
                        if (f.isFile()) {
                            DocumentRow row = new DocumentRow();
                            row.id = 0;
                            row.filename = f.getName();
                            row.filesize = f.length();
                            row.uploadTime = new java.sql.Timestamp(f.lastModified());
                            row.ownerName = "admin";
                            documents.add(row);
                        }
                    }
                }
            }
            request.setAttribute("documents", documents);
        }
        RequestDispatcher dispatcher = request.getRequestDispatcher("/jsp/adminDashboard.jsp");
        dispatcher.forward(request, response);
    }

    public static class DocumentRow {
        public int id;
        public String filename;
        public long filesize;
        public String ownerName;
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

        public String getOwnerName() {
            return ownerName;
        }
    }
}
