package com.oop.game.server.protocol;

import com.oop.game.server.enums.MessageType;

/**
 * Request danh sách người chơi
 */
public class PlayerListRequest extends Message {
    public PlayerListRequest(String playerUsername) {
        super(MessageType.PLAYER_LIST_REQUEST, playerUsername);
    }
}