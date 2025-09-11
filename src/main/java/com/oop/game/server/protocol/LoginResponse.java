package com.oop.game.server.protocol;

import com.oop.game.server.core.Player;
import com.oop.game.server.enums.MessageType;

import java.io.*;

/**
 * Response đăng nhập từ server
 */
public class LoginResponse extends Message {
    private boolean success;
    private String errorMessage;
    private PlayerInfo playerInfo; // Thông tin người chơi nếu login thành công

    public LoginResponse(boolean success, String errorMessage, Player player) {
        super(MessageType.LOGIN_RESPONSE, null);
        this.success = success;
        this.errorMessage = errorMessage;
        this.playerInfo = new PlayerInfo(player);
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

    /**
     * Inner class chứa thông tin cơ bản của player
     */
    private class PlayerInfo implements Serializable {
        public final String username;
        public final int elo;
        public final int totalWins;
        public final int totalLosses;


        public PlayerInfo(Player player) {
            this.username = player.getUsername();
            this.elo = player.getElo();
            this.totalLosses = player.getTotalLosses();
            this.totalWins = player.getTotalWins();
        }
    }
}