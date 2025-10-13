package com.oop.game.client.network;

import java.io.BufferedReader;
import java.io.IOException;

public class ListenerThread extends Thread {
    private final BufferedReader in;

    public ListenerThread(BufferedReader in) {
        this.in = in;
    }

    @Override
    public void run() {
        try {
            String line;
            while ((line = in.readLine()) != null) {
                System.out.println("Server: " + line);
            }
        } catch (IOException e) {
            System.err.println("ListenerThread error: " + e.getMessage());
        }
    }
}
