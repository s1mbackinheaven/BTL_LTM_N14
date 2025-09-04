package com.oop.game.server.protocol;

import java.io.Serializable;

/**
 * Lớp cha cho tất cả message trao đổi giữa client-server
 * Sử dụng Serializable để dễ dàng gửi qua socket
 */
public abstract class Message implements Serializable {
    private static final long serialVersionUID = 1L;

    public enum MessageType {
        // Authentication
        LOGIN_REQUEST,
        LOGIN_RESPONSE,
        LOGOUT_REQUEST,

        // Lobby
        PLAYER_LIST_REQUEST,
        PLAYER_LIST_RESPONSE,
        PLAYER_STATUS_UPDATE,

        // Game invitation
        INVITE_REQUEST,
        INVITE_RESPONSE,

        // Game play
        GAME_START,
        MOVE_REQUEST,
        MOVE_RESULT,
        GAME_STATE_UPDATE,
        GAME_END,
        PLAYER_LEFT,

        // Power-ups
        POWERUP_USE,
        POWERUP_EFFECT,

        // Leaderboard
        LEADERBOARD_REQUEST,
        LEADERBOARD_RESPONSE,

        // Error
        ERROR
    }

    private MessageType type;
    private long timestamp;
    private String senderId;

    public Message(MessageType type, String senderId) {
        this.type = type;
        this.senderId = senderId;
        this.timestamp = System.currentTimeMillis();
    }

    // Getters
    public MessageType getType() {
        return type;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getSenderId() {
        return senderId;
    }

    @Override
    public String toString() {
        return String.format("%s[type=%s, sender=%s, time=%d]",
                getClass().getSimpleName(), type, senderId, timestamp);
    }
}