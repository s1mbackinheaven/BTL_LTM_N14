package com.oop.game.server.protocol;

import com.oop.game.server.enums.MessageType;

/**
 * Thông báo lỗi từ server
 */
public class ErrorMessage extends Message {
    private String errorCode;
    private String errorMessage;
    private String details;

    public ErrorMessage(String serverName, String errorCode, String errorMessage, String details) {
        super(MessageType.ERROR, serverName);
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.details = details;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public String getDetails() {
        return details;
    }

    // Các mã lỗi thường dùng
    public static final String INVALID_CREDENTIALS = "INVALID_CREDENTIALS";
    public static final String PLAYER_NOT_FOUND = "PLAYER_NOT_FOUND";
    public static final String PLAYER_BUSY = "PLAYER_BUSY";
    public static final String GAME_NOT_FOUND = "GAME_NOT_FOUND";
    public static final String INVALID_MOVE = "INVALID_MOVE";
    public static final String NOT_YOUR_TURN = "NOT_YOUR_TURN";
}