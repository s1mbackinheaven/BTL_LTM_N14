package com.oop.game.server.dto;

import java.io.*;
import java.util.*;

// Trạng thái người chơi trong trận
public class PlayerGameStateDTO implements Serializable {
    private String username;
    private int currentScore;
    private List<String> availablePowerUps; // String để client dễ hiển thị
    private boolean isMyTurn;

    public PlayerGameStateDTO(String username, int currentScore,
            List<String> availablePowerUps, boolean isMyTurn) {
        this.username = username;
        this.currentScore = currentScore;
        this.availablePowerUps = availablePowerUps;
        this.isMyTurn = isMyTurn;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getCurrentScore() {
        return currentScore;
    }

    public void setCurrentScore(int currentScore) {
        this.currentScore = currentScore;
    }

    public List<String> getAvailablePowerUps() {
        return availablePowerUps;
    }

    public void setAvailablePowerUps(List<String> availablePowerUps) {
        this.availablePowerUps = availablePowerUps;
    }

    public boolean isMyTurn() {
        return isMyTurn;
    }

    public void setMyTurn(boolean myTurn) {
        isMyTurn = myTurn;
    }
}
