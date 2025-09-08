package com.oop.game.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class GameServer {
    private int port;
    private ServerSocket server;

    public GameServer(int _port) {
        this.port = _port;
    }

    public void start() {
        try {
            server = new ServerSocket(port);

            // chạy các request
            while (true) {
                Socket client = server.accept();

                System.out.println("new client connected" + client.toString());
                new Thread(new ClientHandler(client)).start();

            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void Stop() throws IOException {
        server.close();
    }
}
