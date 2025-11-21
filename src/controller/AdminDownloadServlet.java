package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.DatabaseUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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
            DocumentInfo doc = loadDocument(docId, adminId);
            if (doc == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }
            File file = new File(getServletContext().getRealPath(""), doc.filepath);
            if (!file.exists()) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }
            response.setContentType(doc.mimeType);
            response.setHeader("Content-Disposition", "attachment; filename=" + doc.filename);
            response.setContentLengthLong(file.length());
            try (OutputStream out = response.getOutputStream();
                 FileInputStream in = new FileInputStream(file)) {
                in.transferTo(out);
            }
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid ID");
        } catch (SQLException e) {
            throw new ServletException("Failed to load document", e);
        }
    }

    private DocumentInfo loadDocument(int documentId, int ownerId) throws SQLException {
        String sql = "SELECT filename, filepath, mime_type FROM Documents WHERE id = ? AND owner_id = ?";
        try (Connection connection = DatabaseUtils.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, documentId);
            statement.setInt(2, ownerId);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return new DocumentInfo(rs.getString("filename"), rs.getString("filepath"), rs.getString("mime_type"));
                }
            }
        }
        return null;
    }

    private record DocumentInfo(String filename, String filepath, String mimeType) {}
}

