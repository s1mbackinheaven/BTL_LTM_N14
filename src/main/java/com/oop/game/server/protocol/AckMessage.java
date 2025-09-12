package com.oop.game.server.protocol;

import com.oop.game.server.enums.MessageType;

/**
 * Ack từ server (xác nhận hoặc thông báo đơn giản)
 */
public class AckMessage extends Message {
    private String code;
    private String message;

    public AckMessage(String sender, String code, String message) {
        super(MessageType.ACK, sender);
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
