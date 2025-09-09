USE dungcony;

-- Create users table
CREATE TABLE IF NOT EXISTS users
(
    id           INT PRIMARY KEY AUTO_INCREMENT,
    username     VARCHAR(50) UNIQUE NOT NULL,
    password     VARCHAR(255)       NOT NULL,
    elo          INT       DEFAULT 1000,
    total_wins   INT       DEFAULT 0,
    total_losses INT       DEFAULT 0,
    created_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create matches table
CREATE TABLE IF NOT EXISTS matches
(
    id            INT PRIMARY KEY AUTO_INCREMENT,
    player1_id    INT,
    player2_id    INT,
    winner_id     INT,
    player1_score INT,
    player2_score INT,
    elo_change    INT,
    played_at     TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (player1_id) REFERENCES users (id),
    FOREIGN KEY (player2_id) REFERENCES users (id)
);
