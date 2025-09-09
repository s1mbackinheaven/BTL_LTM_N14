package com.oop.game.server.models;

import java.sql.Timestamp;

public class Match {
    private int id;
    private int player1Id;
    private int player2Id;
    private int winnerId;
    private int player1Score;
    private int player2Score;
    private int eloChange;
    private Timestamp playedAt;

    // Constructors
    public Match() {
    }

    public Match(int player1Id, int player2Id) {
        this.player1Id = player1Id;
        this.player2Id = player2Id;
        this.player1Score = 0;
        this.player2Score = 0;
        this.eloChange = 0;
    }

    public Match(int id, int player1Id, int player2Id, int winnerId,
            int player1Score, int player2Score, int eloChange, Timestamp playedAt) {
        this.id = id;
        this.player1Id = player1Id;
        this.player2Id = player2Id;
        this.winnerId = winnerId;
        this.player1Score = player1Score;
        this.player2Score = player2Score;
        this.eloChange = eloChange;
        this.playedAt = playedAt;
    }

    // Getters
    public int getId() {
        return id;
    }

    public int getPlayer1Id() {
        return player1Id;
    }

    public int getPlayer2Id() {
        return player2Id;
    }

    public int getWinnerId() {
        return winnerId;
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

    public void setPlayer1Id(int player1Id) {
        this.player1Id = player1Id;
    }

    public void setPlayer2Id(int player2Id) {
        this.player2Id = player2Id;
    }

    public void setWinnerId(int winnerId) {
        this.winnerId = winnerId;
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
