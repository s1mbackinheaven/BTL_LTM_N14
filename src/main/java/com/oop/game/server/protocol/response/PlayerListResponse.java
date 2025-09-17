package com.oop.game.server.protocol.response;

import com.oop.game.server.dto.PlayerInfoDTO;
import com.oop.game.server.enums.MessageType;
import com.oop.game.server.protocol.Message;

import java.util.List;

/**
 * Danh sách người chơi
 */

public class PlayerListResponse extends Message {
    private List<PlayerInfoDTO> players;

    public PlayerListResponse(String serverName, List<PlayerInfoDTO> players) {
        super(MessageType.PLAYER_LIST_RESPONSE, serverName);
        this.players = players;
    }

    public List<PlayerInfoDTO> getPlayers() {
        return players;
    }

}
