import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class TestJdbc {
    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/plagiarism_checker?useSSL=false&serverTimezone=UTC";
        // ensure allowPublicKeyRetrieval appended
        if (!url.toLowerCase().contains("allowpublickeyretrieval")) {
            url += (url.contains("?") ? "&" : "?") + "allowPublicKeyRetrieval=true";
        }
        String user = "root";
        String pass = "123456789Quoc#";
        System.out.println("Attempting JDBC connect to: " + url);
        try (Connection c = DriverManager.getConnection(url, user, pass)) {
            System.out.println("Connected OK. DB user: " + c.getMetaData().getUserName());
        } catch (SQLException ex) {
            ex.printStackTrace();
            System.exit(2);
        }
    }
}
