package controller;

import com.mysql.cj.jdbc.AbandonedConnectionCleanupThread;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.DatabaseUtils;

import java.io.IOException;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.logging.Logger;

public class AdminLoginServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(AdminLoginServlet.class.getName());

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException ex) {
            throw new ExceptionInInitializerError("Missing MySQL driver");
        }
    }

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

        request.setAttribute("error", "Invalid username or password");
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

    @Override
    public void destroy() {
        shutdownCleanupThread();
        deregisterJdbcDrivers();
    }

    private void shutdownCleanupThread() {
        try {
            runCleanupShutdown();
        } catch (java.lang.InterruptedException ex) {
            Thread.currentThread().interrupt();
            LOGGER.warning("Interrupted while stopping MySQL cleanup thread");
        } catch (Exception ex) {
            LOGGER.warning("Failed to stop MySQL cleanup thread: " + ex.getMessage());
        }
    }

    private void runCleanupShutdown() throws java.lang.InterruptedException {
        AbandonedConnectionCleanupThread.checkedShutdown();
    }

    private void deregisterJdbcDrivers() {
        Enumeration<Driver> drivers = DriverManager.getDrivers();
        while (drivers.hasMoreElements()) {
            Driver driver = drivers.nextElement();
            if (driver.getClass().getClassLoader() == getClass().getClassLoader()) {
                try {
                    DriverManager.deregisterDriver(driver);
                    LOGGER.fine("Deregistered JDBC driver: " + driver);
                } catch (SQLException ex) {
                    LOGGER.warning("Unable to deregister driver " + driver + ": " + ex.getMessage());
                }
            }
        }
    }
}
