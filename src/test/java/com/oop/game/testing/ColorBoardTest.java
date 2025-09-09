package com.oop.game.testing;

import com.oop.game.server.core.ColorBoard;
import com.oop.game.server.enums.Color;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test ColorBoard - bảng màu chữ thập và các thao tác
 */
public class ColorBoardTest {

    private ColorBoard colorBoard;

    @BeforeEach
    void setUp() {
        colorBoard = new ColorBoard();
    }

    /**
     * Test vị trí mặc định của 5 màu
     */
    @Test
    void testDefaultPositions() {
        assertEquals(Color.WHITE, colorBoard.getColorAt(0, 1));
        assertEquals(Color.BLUE, colorBoard.getColorAt(0, -1));
        assertEquals(Color.RED, colorBoard.getColorAt(0, 0));
        assertEquals(Color.YELLOW, colorBoard.getColorAt(1, 0));
        assertEquals(Color.PURPLE, colorBoard.getColorAt(-1, 0));
    }

    /**
     * Test điểm số tương ứng với màu
     */
    @Test
    void testColorScores() {
        assertEquals(1, Color.WHITE.getScore());
        assertEquals(2, Color.BLUE.getScore());
        assertEquals(5, Color.RED.getScore());
        assertEquals(4, Color.YELLOW.getScore());
        assertEquals(3, Color.PURPLE.getScore());
    }

    /**
     * Test ra ngoài bảng
     */
    @Test
    void testOutOfBounds() {
        assertNull(colorBoard.getColorAt(2, 2));
        assertNull(colorBoard.getColorAt(-3, -3));
        assertEquals(0, colorBoard.getScoreAt(5, 5));
        assertEquals(0, colorBoard.getScoreAt(-10, 10));
    }

    /**
     * Test hoán đổi 2 màu
     */
    @Test
    void testSwapColors() {
        // Trước khi hoán đổi
        assertEquals(Color.WHITE, colorBoard.getColorAt(0, 1));
        assertEquals(Color.RED, colorBoard.getColorAt(0, 0));

        // Hoán đổi WHITE và RED
        colorBoard.swapColors(Color.WHITE, Color.RED);

        // Sau khi hon đổi
        assertEquals(Color.RED, colorBoard.getColorAt(0, 1));
        assertEquals(Color.WHITE, colorBoard.getColorAt(0, 0));

        // Điểm số vẫn theo màu, không theo vị trí
        assertEquals(5, colorBoard.getScoreAt(0, 1)); // RED = 3 điểm
        assertEquals(1, colorBoard.getScoreAt(0, 0)); // WHITE = 1 điểm
    }

    /**
     * Test hiện chỉ 3/5 vùng màu
     */
    @Test
    void testRevealOnlyThreeColors() {
        // Ban đầu tất cả màu đều hiện
        for (Color color : Color.values()) {
            assertTrue(colorBoard.isColorVisible(color));
        }

        // Sau khi gọi revealOnlyThreeColors
        colorBoard.revealOnlyThreeColors();

        // Chỉ có đúng 3 màu được hiện
        int visibleCount = 0;
        for (Color color : Color.values()) {
            if (colorBoard.isColorVisible(color)) {
                visibleCount++;
            }
        }
        assertEquals(3, visibleCount);
    }

    /**
     * Test reset visibility
     */
    @Test
    void testResetVisibility() {
        // Ẩn bớt màu
        colorBoard.revealOnlyThreeColors();

        // Reset về hiện tất cả
        colorBoard.resetVisibility();

        // Kiểm tra tất cả màu đều hiện
        for (Color color : Color.values()) {
            assertTrue(colorBoard.isColorVisible(color),
                    "Màu " + color + " phải được hiện sau reset");
        }
    }

    /**
     * Test màu ẩn không cho điểm
     */
    @Test
    void testHiddenColorNoScore() {
        // Ẩn bớt màu
        colorBoard.revealOnlyThreeColors();

        // Kiểm tra màu ẩn trả về null
        for (Color color : Color.values()) {
            if (!colorBoard.isColorVisible(color)) {
                ColorBoard.Point pos = colorBoard.getCurrentPosition(color);
                assertNull(colorBoard.getColorAt(pos.x, pos.y));
                assertEquals(0, colorBoard.getScoreAt(pos.x, pos.y));
            }
        }
    }

    /**
     * Test hoán đổi màu ngẫu nhiên
     */
    @Test
    void testSwapRandomColors() {
        // Lưu vị trí ban đầu
        ColorBoard.Point[] originalPositions = new ColorBoard.Point[5];
        Color[] colors = Color.values();
        for (int i = 0; i < colors.length; i++) {
            originalPositions[i] = colorBoard.getCurrentPosition(colors[i]);
        }

        // Hoán đổi ngẫu nhiên
        colorBoard.swapRandomColors();

        // Kiểm tra có ít nhất 2 màu đổi vị trí
        int changedCount = 0;
        for (int i = 0; i < colors.length; i++) {
            ColorBoard.Point newPos = colorBoard.getCurrentPosition(colors[i]);
            if (!originalPositions[i].equals(newPos)) {
                changedCount++;
            }
        }
        assertEquals(2, changedCount); // Đúng 2 màu đổi chỗ
    }
}