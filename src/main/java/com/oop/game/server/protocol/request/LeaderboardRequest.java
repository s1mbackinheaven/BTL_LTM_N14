package com.oop.game.server.protocol.request;

import com.oop.game.server.enums.MessageType;
import com.oop.game.server.protocol.Message;

/**
 * Request bảng xếp hạng
 */
public class LeaderboardRequest extends Message {
    public LeaderboardRequest(String playerUsername) {
        super(MessageType.LEADERBOARD_REQUEST, playerUsername);
    }
}