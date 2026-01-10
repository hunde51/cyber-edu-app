package com.cyberedu.dao;

import com.cyberedu.util.DbPool;

import java.sql.*;

public class ScanHistoryDao {
    public static class ScanRecord {
        private String type;
        private String inputValue;
        private String riskLevel;
        private String explanation;
        private Timestamp createdAt;

        public String getType() { return type; }
        public void setType(String type) { this.type = type; }

        public String getInputValue() { return inputValue; }
        public void setInputValue(String inputValue) { this.inputValue = inputValue; }

        public String getRiskLevel() { return riskLevel; }
        public void setRiskLevel(String riskLevel) { this.riskLevel = riskLevel; }

        public String getExplanation() { return explanation; }
        public void setExplanation(String explanation) { this.explanation = explanation; }

        public Timestamp getCreatedAt() { return createdAt; }
        public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
    }

    public void insertUrlScan(String username, String url, String risk, String explanation) throws SQLException {
        String sql = "INSERT INTO scan_history (username, type, input_value, risk_level, explanation, created_at) VALUES (?, 'URL', ?, ?, ?, NOW())";
        try (Connection c = DbPool.getDataSource().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, url);
            ps.setString(3, risk);
            ps.setString(4, explanation);
            ps.executeUpdate();
        }
    }

    public void insertPasswordScan(String username, String passwordHash, String risk, String explanation) throws SQLException {
        String sql = "INSERT INTO scan_history (username, type, input_value, risk_level, explanation, created_at) VALUES (?, 'PASSWORD', ?, ?, ?, NOW())";
        try (Connection c = DbPool.getDataSource().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, passwordHash); // store hash only, not raw password
            ps.setString(3, risk);
            ps.setString(4, explanation);
            ps.executeUpdate();
        }
    }

    public void insertSqlScan(String username, String sqlInput, String risk, String explanation) throws SQLException {
        String sql = "INSERT INTO scan_history (username, type, input_value, risk_level, explanation, created_at) VALUES (?, 'SQL', ?, ?, ?, NOW())";
        try (Connection c = DbPool.getDataSource().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, sqlInput);
            ps.setString(3, risk);
            ps.setString(4, explanation);
            ps.executeUpdate();
        }
    }

    public java.util.List<ScanRecord> findRecentByUser(String username, int limit) throws SQLException {
        if (limit <= 0) limit = 10;
        String sql = "SELECT type, input_value, risk_level, explanation, created_at FROM scan_history WHERE username = ? ORDER BY created_at DESC LIMIT ?";
        java.util.List<ScanRecord> list = new java.util.ArrayList<>();
        try (Connection c = DbPool.getDataSource().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setInt(2, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ScanRecord r = new ScanRecord();
                    r.setType(rs.getString("type"));
                    r.setInputValue(rs.getString("input_value"));
                    r.setRiskLevel(rs.getString("risk_level"));
                    r.setExplanation(rs.getString("explanation"));
                    r.setCreatedAt(rs.getTimestamp("created_at"));
                    list.add(r);
                }
            }
        }
        return list;
    }
}
