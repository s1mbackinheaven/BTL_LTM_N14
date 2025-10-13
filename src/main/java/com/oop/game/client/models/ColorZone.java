package com.oop.game.client.models;

public class ColorZone {
    private final int x, y, score;

    public ColorZone(int x, int y, int score) {
        this.x = x;
        this.y = y;
        this.score = score;
    }

    public boolean match(int px, int py) {
        return px == x && py == y;
    }

    public int getScore() {
        return score;
    }
}
