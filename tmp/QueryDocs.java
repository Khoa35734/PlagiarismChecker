import model.DatabaseUtils;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class QueryDocs {
    public static void main(String[] args) throws Exception {
        try (Connection c = DatabaseUtils.getConnection()) {
            String sql = "SELECT id, owner_id, filename, filepath, filesize, upload_time FROM Documents ORDER BY upload_time DESC LIMIT 100";
            try (PreparedStatement ps = c.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
                boolean any = false;
                System.out.println("id\towner_id\tfilename\tfilepath\tfilesize\tupload_time");
                while (rs.next()) {
                    any = true;
                    System.out.printf("%d\t%d\t%s\t%s\t%d\t%s\n",
                        rs.getInt("id"), rs.getInt("owner_id"), rs.getString("filename"), rs.getString("filepath"), rs.getLong("filesize"), rs.getString("upload_time")
                    );
                }
                if (!any) System.out.println("<no rows>");
            }
        }
    }
}