package com.oop.game.testing;

import com.oop.game.server.core.*;
import com.oop.game.server.enums.GameEndReason;
import com.oop.game.server.enums.PowerUp;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test tích hợp toàn bộ flow game
 */
public class IntegrationTest {

    /**
     * Test một trận đấu hoàn chỉnh
     */
    @Test
    void testCompleteGameFlow() {
        // Tạo 2 người chơi
        Player alice = new Player("Alice");
        Player bob = new Player("Bob");

        // Lưu ELO ban đầu
        int aliceInitialElo = alice.getElo(); // 1000
        int bobInitialElo = bob.getElo(); // 1000

        // Bắt đầu trận đấu
        GameSession game = new GameSession(alice, bob);

        // Mô phỏng vài lượt ném (GIẢM số lượt)
        int turnCount = 0;
        while (!game.isGameEnded() && turnCount < 20) { // Giảm từ 50 xuống 20
            Player currentPlayer = game.getCurrentPlayer();

            // Dùng ZERO_FORCE để đảm bảo trúng và tăng điểm nhanh
            currentPlayer.getAvailablePowerUps().clear();
            currentPlayer.getAvailablePowerUps().add(PowerUp.ZERO_FORCE);

            // Ném vào RED (0,0) = 5 điểm để thắng nhanh
            GameEngine.ThrowResult result = game.processPlayerThrow(0, 0, PowerUp.ZERO_FORCE);

            // Kiểm tra kết quả hợp lệ
            assertNotNull(result);
            assertTrue(result.finalScore >= 0);

            turnCount++;
        }

        // Game phải kết thúc trong 20 lượt
        assertTrue(game.isGameEnded(), "Game phải kết thúc trong 20 lượt");
        assertNotNull(game.getWinner());

        // Người thắng phải có >= 16 điểm hoặc đối thủ rời trận
        if (game.getEndReason() == GameEndReason.REACH_TARGET_SCORE) {
            assertTrue(game.getWinner().getCurrentScore() >= 16);
        }

        // ELO phải được cập nhật ĐÚNG 1 LẦN
        int aliceEloChange = alice.getElo() - aliceInitialElo;
        int bobEloChange = bob.getElo() - bobInitialElo;
        int totalEloChange = aliceEloChange + bobEloChange;

        // Chỉ có 2 trường hợp: thắng bình thường (65) hoặc đối thủ rời trận (15)
        assertTrue(totalEloChange == 65 || totalEloChange == 15,
                String.format("Tổng ELO change sai: %d (Alice: %d, Bob: %d)",
                        totalEloChange, aliceEloChange, bobEloChange));
    }

    /**
     * Test scenario với nhiều phụ trợ
     */
    @Test
    void testPowerUpInteractions() {
        Player p1 = new Player("P1");
        Player p2 = new Player("P2");
        GameSession game = new GameSession(p1, p2);

        // Test DOUBLE_SCORE + EXTRA_TURN
        p1.getAvailablePowerUps().clear();
        p1.getAvailablePowerUps().add(PowerUp.DOUBLE_SCORE);
        p1.getAvailablePowerUps().add(PowerUp.EXTRA_TURN);
        p1.getAvailablePowerUps().add(PowerUp.ZERO_FORCE);

        // Lượt 1: EXTRA_TURN + ZERO_FORCE (để vẫn là lượt P1)
        GameEngine.ThrowResult result1 = game.processPlayerThrow(-1, 0, PowerUp.EXTRA_TURN);
        assertEquals(3, result1.baseScore); // PURPLE = 3 điểm
        assertEquals(3, result1.finalScore); // Chưa dùng DOUBLE_SCORE
        assertTrue(result1.hasExtraTurn); // Có thêm lượt

        // Vẫn lượt của P1 vì có EXTRA_TURN
        assertEquals(p1, game.getCurrentPlayer());

        // Lượt 2: DOUBLE_SCORE + ZERO_FORCE
        GameEngine.ThrowResult result2 = game.processPlayerThrow(0, 0, PowerUp.DOUBLE_SCORE);
        assertEquals(5, result2.baseScore); // RED = 5 điểm
        assertEquals(10, result2.finalScore); // 5*2 = 10

        // Tổng điểm P1 = 3 + 10 = 13
        assertEquals(13, p1.getCurrentScore());
    }

    /**
     * Test ELO calculation với các scenario khác nhau
     */
    @Test
    void testEloCalculation() {
        // Scenario 1: Thắng bình thường
        Player winner = new Player("Winner");
        Player loser = new Player("Loser");
        GameSession game1 = new GameSession(winner, loser);

        game1.endGame(winner, GameEndReason.REACH_TARGET_SCORE);
        assertEquals(137, winner.getElo());
        assertEquals(0, loser.getElo());
        assertEquals(1, winner.getTotalWins());
        assertEquals(1, loser.getTotalLosses());

        // Scenario 2: Thắng do đối thủ rời trận
        Player winner2 = new Player("Winner2");
        Player leaver = new Player("Leaver");
        GameSession game2 = new GameSession(winner2, leaver);

        game2.playerLeft(leaver);
        assertEquals(87, winner2.getElo());
        assertEquals(0, leaver.getElo());
    }
}