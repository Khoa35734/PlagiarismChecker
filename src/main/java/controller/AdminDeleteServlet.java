package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.bo.DocumentBO;

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
            DocumentBO bo = new DocumentBO();
            boolean ok = bo.deleteDocument(documentId, adminId);
            if (!ok) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Document not found or not owned by you.");
                return;
            }
            response.sendRedirect(request.getContextPath() + "/admin/dashboard");
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid document ID format.");
        }
    }

    // database interaction moved to model.bo.DocumentBO / model.dao.DocumentDAO
}
