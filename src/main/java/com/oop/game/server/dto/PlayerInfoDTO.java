package com.oop.game.server.dto;

import com.oop.game.server.core.Player;

import java.io.*;

public class PlayerInfoDTO implements Serializable {
    private String username;
    private int elo;
    private int totalWins;
    private int totalLosses;
    private boolean isBusy;

    public PlayerInfoDTO(String username, int elo, int totalWins, int totalLosses, boolean isBusy) {
        this.username = username;
        this.elo = elo;
        this.totalWins = totalWins;
        this.totalLosses = totalLosses;
        this.isBusy = isBusy;
    }

    public PlayerInfoDTO(Player p) {
        this.username = p.getUsername();
        this.elo = p.getElo();
        this.totalLosses = p.getTotalLosses();
        this.totalWins = p.getTotalWins();
        this.isBusy = p.isBusy();
    }

    /* GETTER*/
    public String getUsername() {
        return username;
    }

    public int getTotalWins() {
        return totalWins;
    }

    public int getElo() {
        return elo;
    }

    public int getTotalLosses() {
        return totalLosses;
    }

    //SETTER
    public void setUsername(String username) {
        this.username = username;
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
}
