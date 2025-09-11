package com.oop.game.server.core;

import com.oop.game.server.enums.PowerUp;
import com.oop.game.server.models.User;

import java.util.List;
import java.util.ArrayList;

/**
 * Lưu thông tin cơ bản của người chơi trong hệ thống
 * Bao gồm thông tin persistent (elo, thống kê) và temporary (trạng thái online)
 */
public class Player {
    private String username;
    private int elo; // Điểm xếp hạng tổng
    private int totalWins; // Tổng số trận thắng
    private int totalLosses; // Tổng số trận thua
    private boolean isOnline; // Trạng thái online
    private boolean isBusy; // Đang trong trận đấu

    // Thông tin trong trận (temporary)
    private int currentScore; // Điểm hiện tại trong trận
    private List<PowerUp> availablePowerUps; // 3 phụ trợ được random
    private boolean isMyTurn; // Lượt của mình hay không

    public Player(String username) {
        this.username = username;
        this.elo = 36; // ELO khởi tạo
        this.totalWins = 0;
        this.totalLosses = 0;
        this.isOnline = true;
        this.isBusy = false;
        this.currentScore = 0;
        this.availablePowerUps = new ArrayList<>();
        this.isMyTurn = false;
    }

    public Player(User u) {
        this.username = u.getUsername();
        this.elo = u.getElo();
        this.totalLosses = u.getTotalLosses();
        this.totalWins = u.getTotalWins();
        this.isOnline = true;
        this.isBusy = false;

        this.currentScore = 0;
        this.availablePowerUps = new ArrayList<>();
        this.isMyTurn = false;
    }

    /**
     * Reset trạng thái khi bắt đầu trận mới
     */
    public void resetForNewGame() {
        this.currentScore = 0;
        this.availablePowerUps.clear();
        this.isMyTurn = false;
    }

    /**
     * Cập nhật ELO sau trận đấu
     */
    public void updateElo(int change) {
        this.elo += change;
        if (change > 0) {
            this.totalWins++;
        } else {
            this.totalLosses++;
        }
    }

    // Getters và Setters
    public String getUsername() {
        return username;
    }

    public int getElo() {
        return elo;
    }

    public int getTotalWins() {
        return totalWins;
    }

    public int getTotalLosses() {
        return totalLosses;
    }

    public boolean isOnline() {
        return isOnline;
    }

    public boolean isBusy() {
        return isBusy;
    }

    public int getCurrentScore() {
        return currentScore;
    }

    public List<PowerUp> getAvailablePowerUps() {
        return availablePowerUps;
    }

    public boolean isMyTurn() {
        return isMyTurn;
    }

    public void setOnline(boolean online) {
        this.isOnline = online;
    }

    public void setBusy(boolean busy) {
        this.isBusy = busy;
    }

    public void setCurrentScore(int score) {
        this.currentScore = score;
    }

    public void setAvailablePowerUps(List<PowerUp> powerUps) {
        this.availablePowerUps = new ArrayList<>(powerUps); // Tạo ArrayList mới
    }

    public void setMyTurn(boolean myTurn) {
        this.isMyTurn = myTurn;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;

        if (!(o instanceof Player))
            return false;

        Player p = (Player) o;
        return this.username == p.username;
    }
}