package com.cyberedu.util;

import java.util.Properties;

import javax.sql.DataSource;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class DbPool {
    private static HikariDataSource ds;

    static {
        try (java.io.InputStream is = DbPool.class.getClassLoader().getResourceAsStream("application.properties")) {
            if (is == null) {
                throw new IllegalStateException("application.properties not found on classpath");
            }
            Properties p = new Properties();
            p.load(is);

            String jdbcUrl = p.getProperty("db.url");
            if (jdbcUrl == null || jdbcUrl.isBlank()) {
                throw new IllegalStateException("db.url property is missing from application.properties");
            }

            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(jdbcUrl);
            // Explicit driver class ensures Hikari can find the MySQL driver in some container classloader setups
            config.setDriverClassName("com.mysql.cj.jdbc.Driver");
            config.setUsername(p.getProperty("db.user"));
            config.setPassword(p.getProperty("db.password"));
            config.setMaximumPoolSize(10);
            config.setMinimumIdle(2);
            config.setPoolName("CyberEduHikariPool");
            // Ensure the driver class is loaded so DriverManager knows about it
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
            } catch (ClassNotFoundException cnfe) {
                throw new IllegalStateException("MySQL JDBC driver not found in classpath", cnfe);
            }
            ds = new HikariDataSource(config);
        } catch (Exception e) {
            // Wrap and rethrow so the container shows a clear message and stack trace in logs
            throw new ExceptionInInitializerError(e);
        }
    }

    public static DataSource getDataSource() {
        return ds;
    }
}
