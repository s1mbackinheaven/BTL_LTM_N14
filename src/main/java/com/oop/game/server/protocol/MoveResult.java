package com.oop.game.server.protocol;

/**
 * Kết quả nước đi từ server
 */
public class MoveResult extends Message {
    private int finalX, finalY; // Tọa độ cuối sau công thức
    private String hitColor; // Màu trúng (string để client dễ xử lý)
    private int scoreGained; // Điểm nhận được
    private int currentScore; // Tổng điểm hiện tại
    private int force; // Lực đẩy đã random
    private boolean hasExtraTurn; // Có được ném thêm không
    private boolean canSwapColor; // Có được đổi màu không (khi trúng)

    public MoveResult(String serverName, int finalX, int finalY, String hitColor,
            int scoreGained, int currentScore, int force, boolean hasExtraTurn, boolean canSwapColor) {
        super(MessageType.MOVE_RESULT, serverName);
        this.finalX = finalX;
        this.finalY = finalY;
        this.hitColor = hitColor;
        this.scoreGained = scoreGained;
        this.currentScore = currentScore;
        this.force = force;
        this.hasExtraTurn = hasExtraTurn;
        this.canSwapColor = canSwapColor;
    }

    // Getters
    public int getFinalX() {
        return finalX;
    }

    public int getFinalY() {
        return finalY;
    }

    public String getHitColor() {
        return hitColor;
    }

    public int getScoreGained() {
        return scoreGained;
    }

    public int getCurrentScore() {
        return currentScore;
    }

    public int getForce() {
        return force;
    }

    public boolean hasExtraTurn() {
        return hasExtraTurn;
    }

    public boolean canSwapColor() {
        return canSwapColor;
    }
}