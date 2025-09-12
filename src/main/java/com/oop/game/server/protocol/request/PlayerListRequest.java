package com.oop.game.server.protocol.request;

import com.oop.game.server.enums.MessageType;
import com.oop.game.server.protocol.Message;

/**
 * Request danh sách người chơi
 */
public class PlayerListRequest extends Message {
    public PlayerListRequest(String playerUsername) {
        super(MessageType.PLAYER_LIST_REQUEST, playerUsername);
    }
}