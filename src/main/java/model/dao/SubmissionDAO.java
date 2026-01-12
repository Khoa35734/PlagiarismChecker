package model.dao;

import model.DatabaseUtils;
import model.bean.SubmissionBean;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;


public class SubmissionDAO {

    public int insert(SubmissionBean s) throws Exception {
        String sql = "INSERT INTO Submissions (batch_token, guest_token, user_id, filename, raw_content, cleaned_content, status, stack_order, upload_size, upload_time) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, NOW())";
        try (Connection c = DatabaseUtils.getConnection(); PreparedStatement ps = c.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, s.getBatchToken());
            ps.setString(2, s.getGuestToken());
            if (s.getUserId() != null) ps.setInt(3, s.getUserId()); else ps.setNull(3, java.sql.Types.INTEGER);
            ps.setString(4, s.getFilename());
            ps.setString(5, s.getRawContent());
            ps.setString(6, s.getCleanedContent());
            ps.setString(7, s.getStatus());
            ps.setInt(8, s.getStackOrder());
            ps.setLong(9, s.getUploadSize());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getInt(1);
            }
        }
        return -1;
    }

}
