package com.oop.game.server.protocol;

import com.oop.game.server.enums.MessageType;

import java.io.Serializable;

/**
 * Lớp cha cho tất cả message trao đổi giữa client-server
 * Sử dụng Serializable để dễ dàng gửi qua socket
 */
public abstract class Message implements Serializable {
    private static final long serialVersionUID = 1L;

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