package com.oop.game.server.protocol.response;

import com.oop.game.server.protocol.Message;
import com.oop.game.server.dto.PlayerInfo;

/**
 * Response đăng nhập từ server
 */
public class LoginResponse extends Message {
    private boolean success;
    private String errorMessage;
    private PlayerInfo playerInfo; // Thông tin người chơi nếu login thành công

    public LoginResponse(String senderId, boolean success, String errorMessage, PlayerInfo playerInfo) {
        super(MessageType.LOGIN_RESPONSE, senderId);
        this.success = success;
        this.errorMessage = errorMessage;
        this.playerInfo = playerInfo;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public PlayerInfo getPlayerInfo() {
        return playerInfo;
    }
}