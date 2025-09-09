package com.oop.game.server.db;

import java.sql.Timestamp;

/**
 * Model class representing a Match in the dart game system
 * Maps to the 'matches' table in the database
 */
public class Match {
    private int id;
    private String player1Username;
    private String player2Username;
    private String winnerUsername;
    private int player1Score;
    private int player2Score;
    private int eloChange;
    private Timestamp playedAt;

    // Constructors
    public Match() {
    }

    public Match(String player1Username, String player2Username) {
        this.player1Username = player1Username;
        this.player2Username = player2Username;
        this.player1Score = 0;
        this.player2Score = 0;
        this.eloChange = 0;
    }

    public Match(int id, String player1Username, String player2Username, String winnerUsername,
            int player1Score, int player2Score, int eloChange, Timestamp playedAt) {
        this.id = id;
        this.player1Username = player1Username;
        this.player2Username = player2Username;
        this.winnerUsername = winnerUsername;
        this.player1Score = player1Score;
        this.player2Score = player2Score;
        this.eloChange = eloChange;
        this.playedAt = playedAt;
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getPlayer1Username() {
        return player1Username;
    }

    public String getPlayer2Username() {
        return player2Username;
    }

    public String getWinnerUsername() {
        return winnerUsername;
    }

    public int getPlayer1Score() {
        return player1Score;
    }

    public int getPlayer2Score() {
        return player2Score;
    }

    public int getEloChange() {
        return eloChange;
    }

    public Timestamp getPlayedAt() {
        return playedAt;
    }

    // Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setPlayer1Username(String player1Username) {
        this.player1Username = player1Username;
    }

    public void setPlayer2Username(String player2Username) {
        this.player2Username = player2Username;
    }

    public void setWinnerUsername(String winnerUsername) {
        this.winnerUsername = winnerUsername;
    }

    public void setPlayer1Score(int player1Score) {
        this.player1Score = player1Score;
    }

    public void setPlayer2Score(int player2Score) {
        this.player2Score = player2Score;
    }

    public void setEloChange(int eloChange) {
        this.eloChange = eloChange;
    }

    public void setPlayedAt(Timestamp playedAt) {
        this.playedAt = playedAt;
    }

    // Business methods
    /**
     * Gets the loser's username
     * 
     * @return username of the player who lost, null if match is not finished
     */
    public String getLoserUsername() {
        if (winnerUsername == null) {
            return null;
        }
        return winnerUsername.equals(player1Username) ? player2Username : player1Username;
    }

    /**
     * Checks if the match is finished
     * 
     * @return true if there is a winner, false otherwise
     */
    public boolean isFinished() {
        return winnerUsername != null;
    }

    /**
     * Checks if a specific player won the match
     * 
     * @param username the username to check
     * @return true if the player won, false otherwise
     */
    public boolean didPlayerWin(String username) {
        return username != null && username.equals(winnerUsername);
    }

    /**
     * Gets the score of a specific player
     * 
     * @param username the username to get score for
     * @return the player's score, or -1 if player is not in this match
     */
    public int getPlayerScore(String username) {
        if (username == null) {
            return -1;
        }
        if (username.equals(player1Username)) {
            return player1Score;
        } else if (username.equals(player2Username)) {
            return player2Score;
        }
        return -1;
    }

    /**
     * Sets the match result when game ends normally
     * 
     * @param winnerUsername the username of the winner
     * @param winnerScore    the final score of the winner
     * @param loserScore     the final score of the loser
     * @param eloChange      the ELO change amount (positive value)
     */
    public void setMatchResult(String winnerUsername, int winnerScore, int loserScore, int eloChange) {
        this.winnerUsername = winnerUsername;
        this.eloChange = eloChange;

        if (winnerUsername.equals(player1Username)) {
            this.player1Score = winnerScore;
            this.player2Score = loserScore;
        } else {
            this.player1Score = loserScore;
            this.player2Score = winnerScore;
        }
    }

    /**
     * Sets the match result when a player leaves/disconnects
     * 
     * @param leaverUsername the username of the player who left
     */
    public void setMatchResultByLeaver(String leaverUsername) {
        // The player who didn't leave is the winner
        this.winnerUsername = leaverUsername.equals(player1Username) ? player2Username : player1Username;

        // Set scores - leaver gets 0, winner gets current score or minimum winning
        // score
        if (leaverUsername.equals(player1Username)) {
            this.player1Score = 0;
            this.player2Score = Math.max(this.player2Score, 16); // Minimum winning score
        } else {
            this.player1Score = Math.max(this.player1Score, 16); // Minimum winning score
            this.player2Score = 0;
        }

        // ELO change for leaving is different (51 points as mentioned in requirements)
        this.eloChange = 51;
    }

    /**
     * Checks if a player is participating in this match
     * 
     * @param username the username to check
     * @return true if the player is in this match, false otherwise
     */
    public boolean hasPlayer(String username) {
        return username != null &&
                (username.equals(player1Username) || username.equals(player2Username));
    }

    /**
     * Gets the opponent's username for a given player
     * 
     * @param username the player's username
     * @return the opponent's username, or null if player is not in this match
     */
    public String getOpponentUsername(String username) {
        if (username == null) {
            return null;
        }
        if (username.equals(player1Username)) {
            return player2Username;
        } else if (username.equals(player2Username)) {
            return player1Username;
        }
        return null;
    }

    /**
     * Gets match duration in minutes (if match is finished)
     * 
     * @return duration in minutes, or -1 if match is not finished or createdAt is
     *         not set
     */
    public long getMatchDurationMinutes() {
        if (playedAt == null || !isFinished()) {
            return -1;
        }
        // This would need a createdAt timestamp to calculate properly
        // For now, return 0 as placeholder
        return 0;
    }

    // Override methods
    @Override
    public String toString() {
        return "Match{" +
                "id=" + id +
                ", player1='" + player1Username + '\'' +
                ", player2='" + player2Username + '\'' +
                ", winner='" + winnerUsername + '\'' +
                ", scores=" + player1Score + "-" + player2Score +
                ", eloChange=" + eloChange +
                ", playedAt=" + playedAt +
                ", finished=" + isFinished() +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        Match match = (Match) obj;
        return id == match.id;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
}
