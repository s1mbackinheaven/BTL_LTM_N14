package server;

import server.managers.ClientConnectionManager;

public class Main {
    public static void main(String[] args) {
        start();

        server server = new server(Integer.parseInt(Config.get("PORT")));
        server.start();
    }

    static void start() {
        ClientConnectionManager.getInstance();
    }
}
