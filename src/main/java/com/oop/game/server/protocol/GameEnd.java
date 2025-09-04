package com.oop.game.server.protocol;

/**
 * Thông báo kết thúc trận đấu
 */
public class GameEnd extends Message {
    private String winner;
    private String reason; // "REACH_TARGET_SCORE" hoặc "OPPONENT_LEFT"
    private int finalScoreWinner;
    private int finalScoreLoser;
    private int eloChangeWinner;
    private int eloChangeLoser;

    public GameEnd(String serverName, String winner, String reason,
            int finalScoreWinner, int finalScoreLoser,
            int eloChangeWinner, int eloChangeLoser) {
        super(MessageType.GAME_END, serverName);
        this.winner = winner;
        this.reason = reason;
        this.finalScoreWinner = finalScoreWinner;
        this.finalScoreLoser = finalScoreLoser;
        this.eloChangeWinner = eloChangeWinner;
        this.eloChangeLoser = eloChangeLoser;
    }

    // Getters
    public String getWinner() {
        return winner;
    }

    public String getReason() {
        return reason;
    }

    public int getFinalScoreWinner() {
        return finalScoreWinner;
    }

    public int getFinalScoreLoser() {
        return finalScoreLoser;
    }

    public int getEloChangeWinner() {
        return eloChangeWinner;
    }

    public int getEloChangeLoser() {
        return eloChangeLoser;
    }
}
