package server.services.game;

import server.DAO.MatchDAO;
import server.models.Player;

import java.sql.Connection;

public class createGame {
    private MatchDAO matchDAO;

    public createGame(Connection con) {
        this.matchDAO = new MatchDAO(con);
    }

    public boolean create(Player p1, Player p2) {
        return matchDAO.createMatch(p1.getId(), p2.getId());
    }
}
