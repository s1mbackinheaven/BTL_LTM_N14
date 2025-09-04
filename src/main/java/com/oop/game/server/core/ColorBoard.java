package com.oop.game.server.core;

import java.util.*;

/**
 * Quản lý bảng ném phi tiêu hình chữ thập với 5 vùng màu
 * Xử lý hoán đổi màu và ẩn/hiện vùng theo phụ trợ
 */
public class ColorBoard {

    public enum Color {
        WHITE(1, 0, 1), // trắng: 1 điểm, tọa độ (0,1)
        BLUE(2, 0, -1), // xanh: 2 điểm, tọa độ (0,-1)
        RED(5, 0, 0), // đỏ: 5 điểm, tọa độ (0,0)
        YELLOW(4, 1, 0), // vàng: 4 điểm, tọa độ (1,0)
        PURPLE(3, -1, 0); // tím: 3 điểm, tọa độ (-1,0)

        private final int score;
        private final int defaultX;
        private final int defaultY;

        Color(int score, int defaultX, int defaultY) {
            this.score = score;
            this.defaultX = defaultX;
            this.defaultY = defaultY;
        }

        public int getScore() {
            return score;
        }

        public int getDefaultX() {
            return defaultX;
        }

        public int getDefaultY() {
            return defaultY;
        }
    }

    // Map lưu vị trí hiện tại của từng màu (có thể bị hoán đổi)
    private Map<Color, Point> currentPositions;
    // Set lưu màu nào đang được hiện (cho phụ trợ REVEAL_COLORS)
    private Set<Color> visibleColors;

    public ColorBoard() {
        initializeBoard();
    }

    /**
     * Khởi tạo bảng với vị trí mặc định
     */
    private void initializeBoard() {
        currentPositions = new HashMap<>();
        visibleColors = new HashSet<>();

        // Đặt màu ở vị trí mặc định
        for (Color color : Color.values()) {
            currentPositions.put(color, new Point(color.getDefaultX(), color.getDefaultY()));
            visibleColors.add(color); // Ban đầu hiện tất cả màu
        }
    }

    /**
     * Lấy màu tại tọa độ (x, y)
     * 
     * @return Color nếu trúng, null nếu ra ngoài bảng
     */
    public Color getColorAt(int x, int y) {
        for (Map.Entry<Color, Point> entry : currentPositions.entrySet()) {
            Point pos = entry.getValue();
            if (pos.x == x && pos.y == y && visibleColors.contains(entry.getKey())) {
                return entry.getKey();
            }
        }
        return null; // Ra ngoài bảng
    }

    /**
     * Lấy điểm tại tọa độ (x, y)
     */
    public int getScoreAt(int x, int y) {
        Color color = getColorAt(x, y);
        return color != null ? color.getScore() : 0;
    }

    /**
     * Hoán đổi vị trí 2 màu
     */
    public void swapColors(Color color1, Color color2) {
        Point pos1 = currentPositions.get(color1);
        Point pos2 = currentPositions.get(color2);

        currentPositions.put(color1, pos2);
        currentPositions.put(color2, pos1);
    }

    /**
     * Hoán đổi 2 màu ngẫu nhiên (cho phụ trợ SWAP_OPPONENT_COLORS)
     */
    public void swapRandomColors() {
        List<Color> colors = new ArrayList<>(Arrays.asList(Color.values()));
        Collections.shuffle(colors);
        swapColors(colors.get(0), colors.get(1));
    }

    /**
     * Hiện chỉ 3/5 vùng màu (cho phụ trợ REVEAL_COLORS)
     */
    public void revealOnlyThreeColors() {
        List<Color> colors = new ArrayList<>(Arrays.asList(Color.values()));
        Collections.shuffle(colors);

        visibleColors.clear();
        for (int i = 0; i < 3; i++) {
            visibleColors.add(colors.get(i));
        }
    }

    /**
     * Reset về trạng thái ban đầu (hiện tất cả màu)
     */
    public void resetVisibility() {
        visibleColors.clear();
        visibleColors.addAll(Arrays.asList(Color.values()));
    }

    /**
     * Lấy vị trí hiện tại của màu
     */
    public Point getCurrentPosition(Color color) {
        return currentPositions.get(color);
    }

    /**
     * Kiểm tra màu có đang được hiện không
     */
    public boolean isColorVisible(Color color) {
        return visibleColors.contains(color);
    }

    // Inner class Point
    public static class Point {
        public final int x, y;

        public Point(int x, int y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof Point) {
                Point other = (Point) obj;
                return x == other.x && y == other.y;
            }
            return false;
        }
    }
}