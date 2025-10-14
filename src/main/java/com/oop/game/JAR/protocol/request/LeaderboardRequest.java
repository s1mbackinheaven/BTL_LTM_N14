package com.oop.game.JAR.protocol.request;

import com.oop.game.JAR.enums.MessageType;
import com.oop.game.JAR.protocol.Message;

/**
 * Request bảng xếp hạng
 */
public class LeaderboardRequest extends Message {
    public LeaderboardRequest(String playerUsername) {
        super(MessageType.LEADERBOARD_REQUEST, playerUsername);
    }
}