package com.oop.game.server.dto;

import com.oop.game.server.core.ColorBoard;
import com.oop.game.server.enums.Color;

import java.io.*;
import java.util.*;

/**
 * Trạng thái bảng màu (ẩn thông tin để client phải đoán)
 */
public class ColorBoardStateDTO implements Serializable {
    private List<String> visibleColors; // Màu nào đang hiện
    private boolean hasRecentSwap; // Có vừa hoán đổi màu không
    private int lastScoreGained; // Điểm vừa nhận (để đoán màu)

    public ColorBoardStateDTO(List<String> visibleColors, boolean hasRecentSwap, int lastScoreGained) {
        this.visibleColors = visibleColors;
        this.hasRecentSwap = hasRecentSwap;
        this.lastScoreGained = lastScoreGained;
    }

    public ColorBoardStateDTO(ColorBoard board) {
        this.visibleColors = new ArrayList<>();
        for (Color color : Color.values()) {
            if (board.isColorVisible(color)) {
                this.visibleColors.add(color.name());
            }
        }
        this.hasRecentSwap = false; // Default value, sẽ được set từ bên ngoài
        this.lastScoreGained = 0; // Default value, sẽ được set từ bên ngoài
    }

    public List<String> getVisibleColors() {
        return visibleColors;
    }

    public void setVisibleColors(List<String> visibleColors) {
        this.visibleColors = visibleColors;
    }

    public boolean isHasRecentSwap() {
        return hasRecentSwap;
    }

    public void setHasRecentSwap(boolean hasRecentSwap) {
        this.hasRecentSwap = hasRecentSwap;
    }

    public int getLastScoreGained() {
        return lastScoreGained;
    }

    public void setLastScoreGained(int lastScoreGained) {
        this.lastScoreGained = lastScoreGained;
    }
}