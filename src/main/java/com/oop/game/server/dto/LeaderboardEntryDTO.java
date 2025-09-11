package com.oop.game.server.dto;

import java.io.*;

/**
 * 1 entry trong bảng xếp hạng
 */
public class LeaderboardEntryDTO implements Serializable {
    private int rank;
    private String username;
    private int elo;
    private int totalWins;
    private int totalLosses;

    public LeaderboardEntryDTO(int rank, String username, int elo, int totalWins, int totalLosses) {
        this.rank = rank;
        this.username = username;
        this.elo = elo;
        this.totalWins = totalWins;
        this.totalLosses = totalLosses;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
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
