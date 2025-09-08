package com.oop.game.server;

public class ServerMain {
    public static void main(String[] args) {
        GameServer server = new GameServer(Integer.parseInt(Config.get("PORT")));
        server.start();
    }
}
