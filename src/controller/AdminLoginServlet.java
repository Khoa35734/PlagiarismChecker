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

public class AdminLoginServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getRequestDispatcher("/jsp/adminLogin.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        try {
            Integer adminId = authenticate(username, password);
            if (adminId != null) {
                HttpSession session = request.getSession(true);
                session.setAttribute("adminLoggedIn", true);
                session.setAttribute("adminId", adminId);
                response.sendRedirect(request.getContextPath() + "/admin/dashboard");
                return;
            }
        } catch (SQLException ex) {
            throw new ServletException("Failed to authenticate admin", ex);
        }

        request.setAttribute("error", "Invalid credentials");
        request.getRequestDispatcher("/jsp/adminLogin.jsp").forward(request, response);
    }

    private Integer authenticate(String username, String password) throws SQLException {
        String sql = "SELECT id FROM Users WHERE username = ? AND password = ? AND role = 'ADMIN'";
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
