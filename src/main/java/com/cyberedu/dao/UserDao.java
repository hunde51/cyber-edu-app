package com.cyberedu.dao;

import com.cyberedu.util.DbPool;

import java.sql.*;

public class UserDao {
    public boolean createUser(String username, String hashedPassword) throws SQLException {
        String sql = "INSERT INTO users (username, password_hash, created_at) VALUES (?, ?, NOW())";
        try (Connection c = DbPool.getDataSource().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, hashedPassword);
            return ps.executeUpdate() == 1;
        }
    }

    public String findPasswordHashByUsername(String username) throws SQLException {
        String sql = "SELECT password_hash FROM users WHERE username = ?";
        try (Connection c = DbPool.getDataSource().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getString("password_hash");
                return null;
            }
        }
    }
}
