-- Database initialization script for Dart Game
-- This file will be automatically executed when MySQL container starts

USE dungcony;

-- Create users table
CREATE TABLE IF NOT EXISTS users (
    id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    elo INT DEFAULT 1000,
    total_wins INT DEFAULT 0,
    total_losses INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create matches table
CREATE TABLE IF NOT EXISTS matches (
    id INT PRIMARY KEY AUTO_INCREMENT,
    player1_username VARCHAR(50),
    player2_username VARCHAR(50),
    winner_username VARCHAR(50),
    player1_score INT,
    player2_score INT,
    elo_change INT,
    played_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (player1_username) REFERENCES users(username),
    FOREIGN KEY (player2_username) REFERENCES users(username)
);

-- Insert some test data (optional)
INSERT IGNORE INTO users (username, password, elo, total_wins, total_losses) VALUES
('testuser1', '123456', 1200, 5, 3),
('testuser2', '123456', 950, 2, 4),
('admin', 'admin123', 1500, 10, 1);

-- Root user already has all privileges, no need to grant additional permissions
