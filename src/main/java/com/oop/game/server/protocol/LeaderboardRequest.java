package com.oop.game.server.protocol;

/**
 * Request bảng xếp hạng
 */
public class LeaderboardRequest extends Message {
    public LeaderboardRequest(String playerUsername) {
        super(MessageType.LEADERBOARD_REQUEST, playerUsername);
    }
}