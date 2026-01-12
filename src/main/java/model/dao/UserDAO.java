package model.dao;

import model.DatabaseUtils;
import model.bean.UserBean;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class UserDAO {

    public UserBean findByUsernameAndPassword(String username, String password) throws Exception {
        String sql = "SELECT id, username, role FROM Users WHERE username = ? AND password = ?";
        try (Connection c = DatabaseUtils.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    UserBean u = new UserBean();
                    u.setId(rs.getInt("id"));
                    u.setUsername(rs.getString("username"));
                    u.setRole(rs.getString("role"));
                    return u;
                }
            }
        }
        return null;
    }

    public Integer findAdminId(String username, String password) throws Exception {
        String sql = "SELECT id FROM Users WHERE username = ? AND password = ? AND role = 'ADMIN'";
        try (Connection c = DatabaseUtils.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt("id");
            }
        }
        return null;
    }
}
