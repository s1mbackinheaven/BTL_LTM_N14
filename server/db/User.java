package com.oop.game.server.db;

import java.sql.Timestamp;

/**
 * Model class representing a User in the dart game system
 * Maps to the 'users' table in the database
 */
public class User {
    private int id;
    private String username;
    private String password;
    private int elo;
    private int totalWins;
    private int totalLosses;
    private Timestamp createdAt;

    // Constructors
    public User() {
        this.elo = 1000; // Default ELO
        this.totalWins = 0;
        this.totalLosses = 0;
    }

    public User(String username, String password) {
        this();
        this.username = username;
        this.password = password;
    }

    public User(int id, String username, String password, int elo, int totalWins, int totalLosses,
            Timestamp createdAt) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.elo = elo;
        this.totalWins = totalWins;
        this.totalLosses = totalLosses;
        this.createdAt = createdAt;
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public int getElo() {
        return elo;
    }

    public int getTotalWins() {
        return totalWins;
    }

    public int getTotalLosses() {
        return totalLosses;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    // Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setElo(int elo) {
        this.elo = elo;
    }

    public void setTotalWins(int totalWins) {
        this.totalWins = totalWins;
    }

    public void setTotalLosses(int totalLosses) {
        this.totalLosses = totalLosses;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    // Business methods
    /**
     * Calculates total games played
     * 
     * @return total number of games played
     */
    public int getTotalGames() {
        return totalWins + totalLosses;
    }

    /**
     * Calculates win rate as percentage
     * 
     * @return win rate (0.0 to 1.0), returns 0.0 if no games played
     */
    public double getWinRate() {
        int totalGames = getTotalGames();
        if (totalGames == 0) {
            return 0.0;
        }
        return (double) totalWins / totalGames;
    }

    /**
     * Updates ELO after a match
     * 
     * @param newElo the new ELO value
     */
    public void updateElo(int newElo) {
        this.elo = newElo;
    }

    /**
     * Records a win for this user
     * 
     * @param eloGained ELO points gained from winning
     */
    public void recordWin(int eloGained) {
        this.totalWins++;
        this.elo += eloGained;
    }

    /**
     * Records a loss for this user
     * 
     * @param eloLost ELO points lost from losing
     */
    public void recordLoss(int eloLost) {
        this.totalLosses++;
        this.elo -= eloLost;
        // Ensure ELO doesn't go below 0
        if (this.elo < 0) {
            this.elo = 0;
        }
    }

    /**
     * Checks if user has reached the ELO threshold to see the formula
     * 
     * @return true if ELO >= 1036, false otherwise
     */
    public boolean canSeeFormula() {
        return this.elo >= 1036;
    }

    // Override methods
    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", elo=" + elo +
                ", totalWins=" + totalWins +
                ", totalLosses=" + totalLosses +
                ", winRate=" + String.format("%.2f", getWinRate() * 100) + "%" +
                ", createdAt=" + createdAt +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        User user = (User) obj;
        return id == user.id && username.equals(user.username);
    }

    @Override
    public int hashCode() {
        return username.hashCode();
    }
}
