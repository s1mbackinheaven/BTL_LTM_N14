package com.oop.game.server.protocol;

import com.oop.game.server.dto.ColorBoardStateDTO;
import com.oop.game.server.dto.PlayerGameStateDTO;
import com.oop.game.server.enums.MessageType;

/**
 * Cập nhật trạng thái game realtime cho cả 2 người chơi
 * Gửi sau mỗi lượt để sync thông tin
 */
public class GameStateUpdate extends Message {

    private PlayerGameStateDTO player1State; // Trạng thái player 1
    private PlayerGameStateDTO player2State; // Trạng thái player 2
    private ColorBoardStateDTO boardState; // Trạng thái bảng màu (cho đoán màu)

    public GameStateUpdate(String serverName,
            PlayerGameStateDTO player1State,
            PlayerGameStateDTO player2State,
            ColorBoardStateDTO boardState) {
        super(MessageType.GAME_STATE_UPDATE, serverName);
        this.player1State = player1State;
        this.player2State = player2State;
        this.boardState = boardState;
    }

    // Getters
    public String getCurrentPlayerTurn() {
        return player1State.isMyTurn() == true ? player1State.getUsername() : player2State.getUsername();
    }

    public PlayerGameStateDTO getPlayer1State() {
        return player1State;
    }

    public PlayerGameStateDTO getPlayer2State() {
        return player2State;
    }

    public ColorBoardStateDTO getBoardState() {
        return boardState;
    }
}