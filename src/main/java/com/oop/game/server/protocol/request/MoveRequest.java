package com.oop.game.server.protocol;

import com.oop.game.server.enums.MessageType;
import com.oop.game.server.enums.PowerUp;

/**
 * Request gửi nước đi từ client
 */
public class MoveRequest extends Message {
    private int playerX;
    private int playerY;
    private PowerUp usedPowerUp; // Có thể null nếu không dùng phụ trợ

    public MoveRequest(String playerUsername, int playerX, int playerY, PowerUp usedPowerUp) {
        super(MessageType.MOVE_REQUEST, playerUsername);
        this.playerX = playerX;
        this.playerY = playerY;
        this.usedPowerUp = usedPowerUp;
    }

    public int getPlayerX() {
        return playerX;
    }

    public int getPlayerY() {
        return playerY;
    }

    public PowerUp getUsedPowerUp() {
        return usedPowerUp;
    }
}