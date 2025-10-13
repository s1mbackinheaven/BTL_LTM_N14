package com.oop.game.client.models;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class GameState {
    private final Map<String, ColorZone> board = new HashMap<>();

    public GameState() {
        board.put("WHITE", new ColorZone(0, 1, 1));
        board.put("BLUE", new ColorZone(0, -1, 2));
        board.put("RED", new ColorZone(0, 0, 5));
        board.put("YELLOW", new ColorZone(1, 0, 3));
        board.put("PURPLE", new ColorZone(-1, 0, 4));
    }

    public int calculateScore(int x, int y) {
        int f = new Random().nextInt(7) - 3; // -3 đến +3
        int sign = x >= 0 ? 1 : -1;
        int xf = x + f;
        int yf = y + sign * Math.floorDiv(f, 2);

        for (ColorZone zone : board.values()) {
            if (zone.match(xf, yf)) return zone.getScore();
        }
        return 0;
    }
}
