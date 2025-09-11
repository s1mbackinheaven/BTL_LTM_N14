package com.oop.game.server.dto;

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