package com.oop.game.testing;

import com.oop.game.server.core.*;
import com.oop.game.server.enums.PowerUp;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test GameEngine - xử lý công thức và logic 1 lượt ném
 */
public class GameEngineTest {

    private ColorBoard colorBoard;

    @BeforeEach
    void setUp() {
        colorBoard = new ColorBoard();
    }

    /**
     * Test công thức Xfinal = X + f
     */
    @Test
    void testXfinalFormula() {
        // Test với các giá trị f từ -3 đến 3
        for (int f = -3; f <= 3; f++) {
            for (int x = -2; x <= 2; x++) {
                int expectedXfinal = x + f;
                int actualXfinal = GameEngine.calculateFinalX(x, f);
                assertEquals(expectedXfinal, actualXfinal,
                        String.format("Xfinal sai với X=%d, f=%d", x, f));
            }
        }
    }

    /**
     * Test công thức Yfinal = Y + sign(X) * floor(f/2)
     */
    @Test
    void testYfinalFormula() {
        // Test case 1: X >= 0 → sign(X) = 1
        assertEquals(1, GameEngine.calculateFinalY(0, 0, 2)); // 0 + 1*(2/2) = 1
        assertEquals(2, GameEngine.calculateFinalY(1, 1, 2)); // 1 + 1*(2/2) = 2
        assertEquals(0, GameEngine.calculateFinalY(0, 2, 1)); // 0 + 1*(1/2) = 0

        // Test case 2: X < 0 → sign(X) = -1
        assertEquals(-1, GameEngine.calculateFinalY(0, -1, 2)); // 0 + (-1)*(2/2) = -1
        assertEquals(0, GameEngine.calculateFinalY(1, -2, 2)); // 1 + (-1)*(2/2) = 0
        assertEquals(1, GameEngine.calculateFinalY(1, -1, 1)); // 1 + (-1)*(1/2) = 1

        // Test case 3: f âm
        assertEquals(-1, GameEngine.calculateFinalY(0, 1, -2)); // 0 + 1*(-2/2) = -1
        assertEquals(1, GameEngine.calculateFinalY(0, -1, -2)); // 0 + (-1)*(-2/2) = 1
    }

    /**
     * Test mapping tọa độ → màu → điểm theo luật game
     */
    @Test
    void testColorMapping() {
        // Test các vị trí mặc định
        assertEquals(1, colorBoard.getScoreAt(0, 1)); // WHITE
        assertEquals(2, colorBoard.getScoreAt(0, -1)); // BLUE
        assertEquals(5, colorBoard.getScoreAt(0, 0)); // RED
        assertEquals(4, colorBoard.getScoreAt(1, 0)); // YELLOW
        assertEquals(3, colorBoard.getScoreAt(-1, 0)); // PURPLE

        // Test ra ngoài bảng = 0 điểm
        assertEquals(0, colorBoard.getScoreAt(2, 2));
        assertEquals(0, colorBoard.getScoreAt(-5, 3));
    }

    /**
     * Test phụ trợ DOUBLE_SCORE
     */
    @Test
    void testDoubleScorePowerUp() {
        PowerUp[] powerUps = {PowerUp.DOUBLE_SCORE};

        GameEngine.ThrowResult result = GameEngine.processThrow(
                0, 0, 0, colorBoard, powerUps, false);

        assertEquals(5, result.baseScore); // RED = 3 điểm gốc
        assertEquals(10, result.finalScore); // x2 = 6 điểm cuối
    }

    /**
     * Test phụ trợ ZERO_FORCE
     */
    @Test
    void testZeroForcePowerUp() {
        PowerUp[] powerUps = {PowerUp.ZERO_FORCE};

        // Với ZERO_FORCE, lực đẩy sẽ = 0 dù random ra bao nhiêu
        GameEngine.ThrowResult result = GameEngine.processThrow(
                1, 0, 3, colorBoard, powerUps, false // f=3 nhưng sẽ thành 0
        );

        assertEquals(1, result.finalX); // 1 + 0 = 1
        assertEquals(0, result.finalY); // 0 + 1*(0/2) = 0
        assertEquals(4, result.finalScore); // YELLOW = 4 điểm
    }

    /**
     * Test phụ trợ HALF_OPPONENT_NEXT (áp dụng cho đối thủ)
     */
    @Test
    void testHalfOpponentNextPowerUp() {
        PowerUp[] powerUps = {PowerUp.HALF_OPPONENT_NEXT};

        // Test với isOpponentTurn = true
        GameEngine.ThrowResult result = GameEngine.processThrow(
                0, 0, 0, colorBoard, powerUps, true);

        assertEquals(5, result.baseScore); // RED = 5 điểm gốc
        assertEquals(2, result.finalScore); // 5/2 = 1 (integer division)
    }

    /**
     * Test phụ trợ EXTRA_TURN
     */
    @Test
    void testExtraTurnPowerUp() {
        PowerUp[] powerUps = {PowerUp.EXTRA_TURN};

        GameEngine.ThrowResult result = GameEngine.processThrow(
                0, 1, 0, colorBoard, powerUps, false);

        assertTrue(result.hasExtraTurn);
        assertEquals(1, result.finalScore); // WHITE = 1 điểm
    }

    /**
     * Test kết hợp nhiều phụ trợ
     */
    @Test
    void testMultiplePowerUps() {
        PowerUp[] powerUps = {PowerUp.DOUBLE_SCORE, PowerUp.EXTRA_TURN};

        GameEngine.ThrowResult result = GameEngine.processThrow(
                -1, 0, 0, colorBoard, powerUps, false);

        assertEquals(3, result.baseScore); // PURPLE = 3 điểm gốc
        assertEquals(6, result.finalScore); // x2 = 10 điểm cuối
        assertTrue(result.hasExtraTurn); // Có thêm lượt
    }

    /**
     * Test random force trong phạm vi [-3, 3]
     */
    @Test
    void testRandomForceRange() {
        for (int i = 0; i < 100; i++) {
            int force = GameEngine.generateRandomForce();
            assertTrue(force >= -3 && force <= 3,
                    "Force phải trong khoảng [-3, 3], nhưng được: " + force);
        }
    }

    /**
     * Test random 3 phụ trợ từ 6 phụ trợ
     */
    @Test
    void testRandomPowerUps() {
        PowerUp[] powerUps = GameEngine.generateRandomPowerUps();

        assertEquals(3, powerUps.length);

        // Kiểm tra không có phụ trợ trùng
        for (int i = 0; i < powerUps.length; i++) {
            for (int j = i + 1; j < powerUps.length; j++) {
                assertNotEquals(powerUps[i], powerUps[j],
                        "Không được có phụ trợ trùng lặp");
            }
        }
    }
}