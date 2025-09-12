package com.oop.game.server.protocol.response;

import com.oop.game.server.enums.MessageType;
import com.oop.game.server.protocol.Message;

/**
 * Thông báo bắt đầu trận đấu
 */
public class GameStartMessage extends Message {
    private String opponentUsername;
    private String sessionId;
    private boolean isYourTurn; // true nếu là lượt của người chơi này

    public GameStartMessage(String playerUsername, String opponentUsername, String sessionId, boolean isYourTurn) {
        super(MessageType.GAME_START, playerUsername);
        this.opponentUsername = opponentUsername;
        this.sessionId = sessionId;
        this.isYourTurn = isYourTurn;
    }

    public String getOpponentUsername() {
        return opponentUsername;
    }

    public String getSessionId() {
        return sessionId;
    }

    public boolean isYourTurn() {
        return isYourTurn;
    }

    @Override
    public String toString() {
        return String.format("GameStartMessage[player=%s, opponent=%s, session=%s, yourTurn=%s]",
                getSenderUN(), opponentUsername, sessionId, isYourTurn);
    }
}
