package com.oop.game.JAR.protocol.request;

import com.oop.game.JAR.enums.MessageType;
import com.oop.game.JAR.protocol.Message;

/**
 * Request danh sách người chơi
 */
public class PlayerListRequest extends Message {
    public PlayerListRequest(String playerUsername) {
        super(MessageType.PLAYER_LIST_REQUEST, playerUsername);
    }
}