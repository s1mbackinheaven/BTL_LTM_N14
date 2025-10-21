package server.controllers;

import com.oop.game.JAR.protocol.request.InviteRequest;
import com.oop.game.JAR.protocol.response.InviteResponse;
import server.managers.ClientConnectionManager;

import java.sql.Connection;

public class PlayerController {
    private final Connection con;

    public PlayerController(Connection con) {
        this.con = con;
    }

    public InviteResponse invite(InviteRequest req){
        ClientConnectionManager.getInstance().
    }
}
