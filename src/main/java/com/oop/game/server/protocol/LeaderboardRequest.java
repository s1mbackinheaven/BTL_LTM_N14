package com.oop.game.server.protocol;

import com.oop.game.server.enums.MessageType;

/**
 * Request bảng xếp hạng
 */
public class LeaderboardRequest extends Message {
    public LeaderboardRequest(String playerUsername) {
        super(MessageType.LEADERBOARD_REQUEST, playerUsername);
    }
}