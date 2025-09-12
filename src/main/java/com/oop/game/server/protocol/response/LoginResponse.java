package com.oop.game.server.protocol.response;

import com.oop.game.server.core.Player;
import com.oop.game.server.enums.MessageType;
import com.oop.game.server.dto.PlayerInfoDTO;
import com.oop.game.server.protocol.Message;

import java.io.*;

/**
 * Response đăng nhập từ server
 */
public class LoginResponse extends Message {
    private boolean success;
    private String errorMessage;
    private PlayerInfoDTO playerInfo; // Thông tin người chơi nếu login thành

    public LoginResponse(boolean success, String errorMessage, Player player) {
        super(MessageType.LOGIN_RESPONSE, "SYSTEM");
        this.success = success;
        this.errorMessage = errorMessage;
        this.playerInfo = new PlayerInfoDTO(player);
    }

    public boolean isSuccess() {
        return success;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public PlayerInfoDTO getPlayerInfo() {
        return playerInfo;
    }

}