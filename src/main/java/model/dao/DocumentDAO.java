package model.dao;

import model.DatabaseUtils;
import model.bean.DocumentBean;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class DocumentDAO {

    public boolean deleteByIdAndOwner(int id, int ownerId) throws Exception {
        String sql = "DELETE FROM Documents WHERE id = ? AND owner_id = ?";
        try (Connection c = DatabaseUtils.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.setInt(2, ownerId);
            int updated = ps.executeUpdate();
            return updated > 0;
        }
    }

    public boolean existsByOwnerAndName(int ownerId, String filename) throws Exception {
        String sql = "SELECT COUNT(1) FROM Documents WHERE owner_id = ? AND filename = ?";
        try (Connection c = DatabaseUtils.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, ownerId);
            ps.setString(2, filename);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1) > 0;
            }
        }
        return false;
    }

    public boolean saveDocument(int ownerId, String filename, String filepath, long size, String mimeType) throws Exception {
        String sql = "INSERT INTO Documents (owner_id, filename, filepath, filesize, mime_type, upload_time) VALUES (?, ?, ?, ?, ?, NOW()) " +
                     "ON DUPLICATE KEY UPDATE filepath=VALUES(filepath), filesize=VALUES(filesize), upload_time=NOW()";
        try (Connection c = DatabaseUtils.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, ownerId);
            ps.setString(2, filename);
            ps.setString(3, filepath);
            ps.setLong(4, size);
            ps.setString(5, mimeType);
            int updated = ps.executeUpdate();
            return updated > 0;
        }
    }

    public java.util.List<model.bean.DocumentBean> listDocumentsByAdminOwners() throws Exception {
        String sql = "SELECT d.id, d.owner_id, d.original_name as filename, d.stored_path as filepath, d.filesize, d.uploaded_at, u.username " +
                     "FROM Documents d JOIN Users u ON d.owner_id = u.id WHERE u.role = 'ADMIN' ORDER BY d.uploaded_at DESC";
        java.util.List<model.bean.DocumentBean> list = new java.util.ArrayList<>();
        try (Connection c = DatabaseUtils.getConnection(); PreparedStatement ps = c.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                model.bean.DocumentBean d = new model.bean.DocumentBean();
                d.setId(rs.getInt("id"));
                d.setOwnerId(rs.getInt("owner_id"));
                d.setOriginalName(rs.getString("filename"));
                d.setStoredPath(rs.getString("filepath"));
                d.setSize(rs.getLong("filesize"));
                Timestamp t = rs.getTimestamp("uploaded_at");
                if (t != null) d.setUploadedAt(t.toInstant());
                list.add(d);
            }
        }
        return list;
    }

    public DocumentBean findById(int id) throws Exception {
        String sql = "SELECT id, owner_id, original_name, stored_path, size, uploaded_at FROM Documents WHERE id = ?";
        try (Connection c = DatabaseUtils.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    DocumentBean d = new DocumentBean();
                    d.setId(rs.getInt("id"));
                    d.setOwnerId(rs.getInt("owner_id"));
                    d.setOriginalName(rs.getString("original_name"));
                    d.setStoredPath(rs.getString("stored_path"));
                    d.setSize(rs.getLong("size"));
                    Timestamp t = rs.getTimestamp("uploaded_at");
                    if (t != null) d.setUploadedAt(t.toInstant());
                    return d;
                }
            }
        }
        return null;
    }

    public List<DocumentBean> listByOwner(int ownerId) throws Exception {
        String sql = "SELECT id, owner_id, original_name, stored_path, size, uploaded_at FROM Documents WHERE owner_id = ? ORDER BY uploaded_at DESC";
        List<DocumentBean> list = new ArrayList<>();
        try (Connection c = DatabaseUtils.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, ownerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    DocumentBean d = new DocumentBean();
                    d.setId(rs.getInt("id"));
                    d.setOwnerId(rs.getInt("owner_id"));
                    d.setOriginalName(rs.getString("original_name"));
                    d.setStoredPath(rs.getString("stored_path"));
                    d.setSize(rs.getLong("size"));
                    Timestamp t = rs.getTimestamp("uploaded_at");
                    if (t != null) d.setUploadedAt(t.toInstant());
                    list.add(d);
                }
            }
        }
        return list;
    }
}
