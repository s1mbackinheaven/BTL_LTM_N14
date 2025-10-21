package server.controllers;

import com.oop.game.JAR.protocol.GameStart;
import server.models.Player;

import java.sql.Connection;

public class GameController {
    private Connection con;

    public GameController(Connection con) {
        this.con = con;
    }

    public GameStart startGame(Player p1, Player p2) {

    }

}
