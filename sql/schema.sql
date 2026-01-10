CREATE DATABASE IF NOT EXISTS cyberedu CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE cyberedu;

CREATE TABLE users (
  id INT AUTO_INCREMENT PRIMARY KEY,
  username VARCHAR(100) NOT NULL UNIQUE,
  password_hash VARCHAR(255) NOT NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

CREATE TABLE scan_history (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  username VARCHAR(100),
  type ENUM('URL','PASSWORD','SQL') NOT NULL,
  input_value TEXT,
  risk_level VARCHAR(20),
  explanation TEXT,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  INDEX (username),
  INDEX (type),
  INDEX (created_at)
) ENGINE=InnoDB;

