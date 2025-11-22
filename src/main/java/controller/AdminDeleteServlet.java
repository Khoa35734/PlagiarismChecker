package controller;

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
import java.sql.SQLException;

@WebServlet("/admin/delete")
public class AdminDeleteServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        Integer adminId = session != null ? (Integer) session.getAttribute("adminId") : null;
        if (session == null || session.getAttribute("adminLoggedIn") == null || adminId == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "You must be logged in as an admin to perform this action.");
            return;
        }

        String idParam = request.getParameter("id");
        if (idParam == null || idParam.isEmpty()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing document ID.");
            return;
        }

        try {
            int documentId = Integer.parseInt(idParam);
            deleteDocument(documentId, adminId);
            response.sendRedirect(request.getContextPath() + "/admin/dashboard");
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid document ID format.");
        } catch (SQLException e) {
            throw new ServletException("Database error while deleting document", e);
        }
    }

    private void deleteDocument(int documentId, int ownerId) throws SQLException {
        String sql = "DELETE FROM Documents WHERE id = ? AND owner_id = ?";
        try (Connection connection = DatabaseUtils.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, documentId);
            statement.setInt(2, ownerId);
            statement.executeUpdate();
        }
    }
}
