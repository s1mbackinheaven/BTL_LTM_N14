package com.oop.game.server.dto;

import java.io.*;

public class PlayerInfoDTO implements Serializable {
    private String username;
    private int elo;
    private int totalWins;
    private int totalLosses;

    public PlayerInfoDTO(String username, int elo, int totalWins, int totalLosses) {
        this.username = username;
        this.elo = elo;
        this.totalWins = totalWins;
        this.totalLosses = totalLosses;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getElo() {
        return elo;
    }

    public void setElo(int elo) {
        this.elo = elo;
    }

    public int getTotalWins() {
        return totalWins;
    }

    public void setTotalWins(int totalWins) {
        this.totalWins = totalWins;
    }

    public int getTotalLosses() {
        return totalLosses;
    }

    public void setTotalLosses(int totalLosses) {
        this.totalLosses = totalLosses;
    }
}
