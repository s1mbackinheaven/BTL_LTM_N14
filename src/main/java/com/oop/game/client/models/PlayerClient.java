package com.oop.game.client.models;

public class PlayerClient {
    private String username;
    private int score;
    private boolean isBusy;

    public PlayerClient(String username) {
        this.username = username;
        this.score = 0;
        this.isBusy = false;
    }

    public String getUsername() { return username; }
    public int getScore() { return score; }
    public boolean isBusy() { return isBusy; }

    public void setScore(int score) { this.score = score; }
    public void setBusy(boolean busy) { isBusy = busy; }
}
