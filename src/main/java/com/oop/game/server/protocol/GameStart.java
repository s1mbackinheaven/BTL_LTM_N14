package com.oop.game.server.protocol;

import com.oop.game.server.enums.MessageType;

import java.util.List;

/**
 * Thông báo bắt đầu trận đấu
 */
public class GameStart extends Message {
    private String opponent; // Tên đối thủ
    private List<String> myPowerUps; // 3 phụ trợ của mình
    private boolean isFirstPlayer; // Có ném trước không

    public GameStart(String serverName, String opponent, List<String> myPowerUps, boolean isFirstPlayer) {
        super(MessageType.GAME_START, serverName);
        this.opponent = opponent;
        this.myPowerUps = myPowerUps;
        this.isFirstPlayer = isFirstPlayer;
    }

    public String getOpponent() {
        return opponent;
    }

    public List<String> getMyPowerUps() {
        return myPowerUps;
    }

    public boolean isFirstPlayer() {
        return isFirstPlayer;
    }
}