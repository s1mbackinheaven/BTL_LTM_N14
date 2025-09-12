package com.oop.game.server.DAO;

import com.oop.game.server.Config;

import java.sql.Connection;
import java.sql.DriverManager;

public class DAO {
    public static Connection getConnection() throws Exception {
        String url = Config.get("DB_URL");
        String user = Config.get("DB_USER");
        String password = Config.get("DB_PASS");

        return DriverManager.getConnection(url, user, password);
    }
}
