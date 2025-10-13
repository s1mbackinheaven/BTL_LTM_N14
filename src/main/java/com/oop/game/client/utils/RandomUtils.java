package com.oop.game.client.utils;

import java.util.Random;

public class RandomUtils {
    private static final Random R = new Random();

    public static int randInt(int min, int max) {
        return R.nextInt(max - min + 1) + min;
    }
}
