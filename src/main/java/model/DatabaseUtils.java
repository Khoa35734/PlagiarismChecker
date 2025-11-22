package model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Objects;

/**
 * Centralized JDBC helper that gets configured from web.xml context params.
 */
public final class DatabaseUtils {
    private static String jdbcUrl = "jdbc:mysql://localhost:3306/plagiarism_checker";
    private static String jdbcUser = "root";
    private static String jdbcPassword = "123456789Quoc#";
    private static volatile boolean driverLoaded;

    private DatabaseUtils() {
    }

    public static synchronized void configure(String url, String user, String password) {
        jdbcUrl = Objects.requireNonNull(url, "JDBC URL must not be null");
        jdbcUser = Objects.requireNonNull(user, "DB user must not be null");
        jdbcPassword = Objects.requireNonNull(password, "DB password must not be null");
    }

    public static Connection getConnection() throws SQLException {
        ensureConfigured();
        loadDriver();
        return DriverManager.getConnection(jdbcUrl, jdbcUser, jdbcPassword);
    }

    private static void ensureConfigured() {
        if (jdbcUrl == null || jdbcUser == null || jdbcPassword == null) {
            throw new IllegalStateException("DatabaseUtils is not configured. Check context-param settings.");
        }
    }

    private static void loadDriver() {
        if (!driverLoaded) {
            synchronized (DatabaseUtils.class) {
                if (!driverLoaded) {
                    try {
                        Class.forName("com.mysql.cj.jdbc.Driver");
                        driverLoaded = true;
                    } catch (ClassNotFoundException ex) {
                        throw new IllegalStateException("MySQL JDBC driver not found in classpath", ex);
                    }
                }
            }
        }
    }
}
