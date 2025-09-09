package com.oop.game.testing;

import com.oop.game.server.core.*;
import com.oop.game.server.enums.GameEndReason;
import com.oop.game.server.enums.PowerUp;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test GameSession - quản lý trận đấu hoàn chỉnh
 */
public class GameSessionTest {

    private Player player1, player2;
    private GameSession gameSession;

    @BeforeEach
    void setUp() {
        player1 = new Player("Alice");
        player2 = new Player("Bob");
        gameSession = new GameSession(player1, player2);
    }

    /**
     * Test khởi tạo trận đấu
     */
    @Test
    void testGameInitialization() {
        // Người thách đấu ném trước
        assertEquals(player1, gameSession.getCurrentPlayer());
        assertTrue(player1.isMyTurn());
        assertFalse(player2.isMyTurn());

        // Cả 2 đều busy
        assertTrue(player1.isBusy());
        assertTrue(player2.isBusy());

        // Điểm ban đầu = 0
        assertEquals(0, player1.getCurrentScore());
        assertEquals(0, player2.getCurrentScore());

        // Mỗi người có 3 phụ trợ
        assertEquals(3, player1.getAvailablePowerUps().size());
        assertEquals(3, player2.getAvailablePowerUps().size());

        // Game chưa kết thúc
        assertFalse(gameSession.isGameEnded());
    }

    /**
     * Test xử lý 1 lượt ném bình thường
     */
    // @Test
    // void testNormalThrow() {
    // // Player1 ném vào RED (0,0)
    // GameEngine.ThrowResult result = gameSession.processPlayerThrow(0, 0, null);

    // // Kiểm tra kết quả
    // assertNotNull(result);
    // assertTrue(player1.getCurrentScore() > 0);

    // // Chuyển lượt sang player2
    // assertEquals(player2, gameSession.getCurrentPlayer());
    // assertFalse(player1.isMyTurn());
    // assertTrue(player2.isMyTurn());
    // }
    @Test
    void testNormalThrow() {
        // Dùng ZERO_FORCE để đảm bảo trúng đích
        player1.getAvailablePowerUps().clear();
        player1.getAvailablePowerUps().add(PowerUp.ZERO_FORCE);

        GameEngine.ThrowResult result = gameSession.processPlayerThrow(0, 0, PowerUp.ZERO_FORCE);

        // Với ZERO_FORCE, (0,0) sẽ chính xác trúng RED = 5 điểm
        assertEquals(5, player1.getCurrentScore());
        assertEquals(player2, gameSession.getCurrentPlayer());
    }

    /**
     * Test sử dụng phụ trợ EXTRA_TURN
     */
    @Test
    void testExtraTurnPowerUp() {
        // Thêm EXTRA_TURN vào danh sách phụ trợ của player1
        player1.getAvailablePowerUps().clear();
        player1.getAvailablePowerUps().add(PowerUp.EXTRA_TURN);

        GameEngine.ThrowResult result = gameSession.processPlayerThrow(0, 1, PowerUp.EXTRA_TURN);

        // Vẫn là lượt của player1
        assertEquals(player1, gameSession.getCurrentPlayer());
        assertTrue(player1.isMyTurn());
        assertFalse(player2.isMyTurn());

        // Phụ trợ đã bị remove
        assertFalse(player1.getAvailablePowerUps().contains(PowerUp.EXTRA_TURN));
    }

    /**
     * Test điều kiện thắng (16 điểm)
     */
    @Test
    void testWinCondition() {
        // Set điểm gần thắng
        player1.setCurrentScore(14);

        // Ném vào Red (5 điểm) để thắng
        gameSession.processPlayerThrow(0, 0, null);

        // Game kết thúc, player1 thắng
        assertTrue(gameSession.isGameEnded());
        assertEquals(player1, gameSession.getWinner());
        assertEquals(GameEndReason.REACH_TARGET_SCORE, gameSession.getEndReason());

        // ELO được cập nhật
        assertEquals(137, player1.getElo()); // +101
        assertEquals(0, player2.getElo()); // -36

        // Không còn busy
        assertFalse(player1.isBusy());
        assertFalse(player2.isBusy());
    }

    /**
     * Test người chơi rời trận
     */
    @Test
    void testPlayerLeft() {
        gameSession.playerLeft(player2);

        // Game kết thúc, player1 thắng
        assertTrue(gameSession.isGameEnded());
        assertEquals(player1, gameSession.getWinner());
        assertEquals(GameEndReason.OPPONENT_LEFT, gameSession.getEndReason());

        // ELO: thắng +51, thua -36
        assertEquals(87, player1.getElo());
        assertEquals(0, player2.getElo());
    }

    /**
     * Test phụ trợ HALF_OPPONENT_NEXT
     */
    @Test
    void testHalfOpponentNextPowerUp() {
        // Player1 dùng HALF_OPPONENT_NEXT
        player1.getAvailablePowerUps().clear();
        player1.getAvailablePowerUps().add(PowerUp.HALF_OPPONENT_NEXT);

        gameSession.processPlayerThrow(0, 1, PowerUp.HALF_OPPONENT_NEXT);

        // Chuyển lượt sang player2
        assertEquals(player2, gameSession.getCurrentPlayer());

        // Player2 ném vào PURPLE (3 điểm) nhưng chỉ được 2 điểm (3/2=1)
        GameEngine.ThrowResult result = gameSession.processPlayerThrow(-1, 0, null);

        assertEquals(3, result.baseScore);
        assertEquals(1, result.finalScore); // Bị giảm nửa
        assertEquals(1, player2.getCurrentScore());
    }

    /**
     * Test luân phiên lượt chơi
     */
    @Test
    void testTurnAlternation() {
        // Lượt 1: Player1
        assertEquals(player1, gameSession.getCurrentPlayer());
        gameSession.processPlayerThrow(0, 0, null);

        // Lượt 2: Player2
        assertEquals(player2, gameSession.getCurrentPlayer());
        gameSession.processPlayerThrow(1, 0, null);

        // Lượt 3: Player1
        assertEquals(player1, gameSession.getCurrentPlayer());
        gameSession.processPlayerThrow(0, -1, null);

        // Lượt 4: Player2
        assertEquals(player2, gameSession.getCurrentPlayer());
    }

    /**
     * Test không thể ném khi game đã kết thúc
     */
    @Test
    void testCannotThrowAfterGameEnd() {
        // Kết thúc game
        gameSession.endGame(player1, GameEndReason.REACH_TARGET_SCORE);

        // Thử ném sẽ throw exception
        assertThrows(IllegalStateException.class, () -> {
            gameSession.processPlayerThrow(0, 0, null);
        });
    }
}