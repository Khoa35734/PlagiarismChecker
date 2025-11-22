package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

/**
 * AdminServlet - Main entry point for /admin
 * Forwards to dashboard if logged in, or redirects to login page.
 * Mapped in web.xml to /admin
 */
public class AdminServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);

        // Check if admin is logged in
        if (session != null && session.getAttribute("adminLoggedIn") != null && (Boolean) session.getAttribute("adminLoggedIn")) {
            // Forward to the admin dashboard JSP
            request.getRequestDispatcher("/jsp/adminDashboard.jsp").forward(request, response);
        } else {
            // Redirect to the correct login page
            response.sendRedirect(request.getContextPath() + "/login");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}
