package com.oop.game.server.protocol;

import com.oop.game.server.enums.MessageType;

import java.util.List;

/**
 * Cập nhật trạng thái game realtime cho cả 2 người chơi
 * Gửi sau mỗi lượt để sync thông tin
 */
public class GameStateUpdate extends Message {
    private String currentPlayerTurn; // Username của người đang ném
    private PlayerGameState player1State; // Trạng thái player 1
    private PlayerGameState player2State; // Trạng thái player 2
    private ColorBoardState boardState; // Trạng thái bảng màu (cho đoán màu)

    public GameStateUpdate(String serverName, String currentPlayerTurn,
                           PlayerGameState player1State, PlayerGameState player2State,
                           ColorBoardState boardState) {
        super(MessageType.GAME_STATE_UPDATE, serverName);
        this.currentPlayerTurn = currentPlayerTurn;
        this.player1State = player1State;
        this.player2State = player2State;
        this.boardState = boardState;
    }

    // Getters
    public String getCurrentPlayerTurn() {
        return currentPlayerTurn;
    }

    public PlayerGameState getPlayer1State() {
        return player1State;
    }

    public PlayerGameState getPlayer2State() {
        return player2State;
    }

    public ColorBoardState getBoardState() {
        return boardState;
    }

    /**
     * Trạng thái của 1 người chơi trong trận
     */
    public static class PlayerGameState implements java.io.Serializable {
        public final String username;
        public final int currentScore;
        public final List<String> availablePowerUps; // String để client dễ hiển thị
        public final boolean isMyTurn;

        public PlayerGameState(String username, int currentScore,
                               List<String> availablePowerUps, boolean isMyTurn) {
            this.username = username;
            this.currentScore = currentScore;
            this.availablePowerUps = availablePowerUps;
            this.isMyTurn = isMyTurn;
        }
    }

    /**
     * Trạng thái bảng màu (ẩn thông tin để client phải đoán)
     */
    public static class ColorBoardState implements java.io.Serializable {
        public final List<String> visibleColors; // Màu nào đang hiện
        public final boolean hasRecentSwap; // Có vừa hoán đổi màu không
        public final int lastScoreGained; // Điểm vừa nhận (để đoán màu)

        public ColorBoardState(List<String> visibleColors, boolean hasRecentSwap, int lastScoreGained) {
            this.visibleColors = visibleColors;
            this.hasRecentSwap = hasRecentSwap;
            this.lastScoreGained = lastScoreGained;
        }
    }
}