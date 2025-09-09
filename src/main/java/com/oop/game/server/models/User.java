package com.oop.game.server.models;

import java.sql.Timestamp;

public class User {
    private int id; // id
    private String username; // tên tài khoản
    private String password; // mật khẩu
    private int elo; // điểm elo
    private int totalWins; // tổng số trận thắng
    private int totalLosses; // tổng số trận thua
    private Timestamp createdAt; // thời gian tạo acc

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
}
