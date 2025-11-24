package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.bo.DocumentBO;
import model.bean.DocumentBean;

import java.io.IOException;
import java.io.FileInputStream;
import java.io.OutputStream;

@WebServlet("/admin/download")
public class AdminDownloadServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        Integer adminId = session != null ? (Integer) session.getAttribute("adminId") : null;
        if (session == null || session.getAttribute("adminLoggedIn") == null || adminId == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        String idParam = request.getParameter("id");
        if (idParam == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing document ID");
            return;
        }

        try {
            int docId = Integer.parseInt(idParam);
            DocumentBO dbo = new DocumentBO();
            DocumentBean doc = dbo.getDocument(docId);
            if (doc == null || doc.getOwnerId() != adminId) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }
            String storedPath = doc.getStoredPath();
            java.io.File file = new java.io.File(storedPath);
            if (!file.isAbsolute()) {
                file = new java.io.File(getServletContext().getRealPath(""), storedPath);
            }
            if (!file.exists()) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }
            String mimeType = "application/octet-stream";
            String filename = doc.getOriginalName() != null ? doc.getOriginalName() : "download";
            response.setContentType(mimeType);
            response.setHeader("Content-Disposition", "attachment; filename=" + filename);
            response.setContentLengthLong(file.length());
            try (OutputStream out = response.getOutputStream(); FileInputStream in = new FileInputStream(file)) {
                in.transferTo(out);
            }
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid ID");
        }
    }
    
}

