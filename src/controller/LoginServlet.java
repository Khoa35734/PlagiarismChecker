package controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
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

public class LoginServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if ("true".equals(request.getParameter("logout"))) {
            HttpSession session = request.getSession(false);
            if (session != null) {
                session.invalidate();
            }
            request.setAttribute("message", "You have been logged out.");
        }
        RequestDispatcher dispatcher = request.getRequestDispatcher("/jsp/login.jsp");
        dispatcher.forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        try {
            Integer userId = authenticate(username, password);
            if (userId != null) {
                HttpSession session = request.getSession(true);
                session.setAttribute("userId", userId);
                session.setAttribute("username", username);
                response.sendRedirect(request.getContextPath() + "/upload");
            } else {
                request.setAttribute("error", "Invalid credentials");
                RequestDispatcher dispatcher = request.getRequestDispatcher("/jsp/login.jsp");
                dispatcher.forward(request, response);
            }
        } catch (SQLException ex) {
            throw new ServletException("Failed to authenticate user", ex);
        }
    }

    private Integer authenticate(String username, String password) throws SQLException {
        String sql = "SELECT id FROM Users WHERE username = ? AND password = ?";
        try (Connection connection = DatabaseUtils.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, username);
            statement.setString(2, password);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id");
                }
            }
        }
        return null;
    }
}
