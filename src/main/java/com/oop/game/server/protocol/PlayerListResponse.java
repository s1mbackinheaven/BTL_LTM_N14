package com.oop.game.server.protocol;

import com.oop.game.server.core.Player;
import com.oop.game.server.enums.MessageType;

import java.util.List;

/**
 * Danh sách người chơi
 */

public class PlayerListResponse extends Message {
    private List<Player> players;

    public PlayerListResponse(String serverName, List<Player> players) {
        super(MessageType.PLAYER_LIST_RESPONSE, serverName);
        this.players = players;
    }

    public List<Player> getPlayers() {
        return players;
    }

}
