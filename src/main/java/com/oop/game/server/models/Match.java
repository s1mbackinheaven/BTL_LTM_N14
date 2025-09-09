package com.oop.game.server.models;

import java.sql.Timestamp;

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
}
