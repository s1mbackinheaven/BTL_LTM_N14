package server.core;

import com.oop.game.JAR.enums.GameEndReason;
import com.oop.game.JAR.enums.PowerUp;

import java.util.*;

/**
 * Quản lý 1 trận đấu giữa 2 người chơi
 * Stateful - lưu trạng thái trận đấu, xử lý luân phiên, điều kiện thắng
 */
public class GameSession {
    private Player player1, player2;
    private Player currentPlayer;
    private ColorBoard colorBoard;
    private boolean gameEnded;
    private Player winner;
    private GameEndReason endReason;

    // ✅ THÊM: ID của match trong database
    private int matchId;

    // sessionId để xử lý cache
    private String id;
    // Phụ trợ chờ kích hoạt (ví dụ: HALF_OPPONENT_NEXT)
    private Map<Player, List<PowerUp>> pendingPowerUps;

    public GameSession(Player challenger, Player challenged, String id) {
        this.id = id;
        this.player1 = challenger;
        this.player2 = challenged;
        this.currentPlayer = challenger; // Người thách đấu ném trước
        this.colorBoard = new ColorBoard();
        this.gameEnded = false;
        this.pendingPowerUps = new HashMap<>();

        initializeGame();
    }

    /**
     * Khởi tạo trận đấu: reset điểm, phân phụ trợ
     */
    private void initializeGame() {
        // Reset trạng thái 2 người chơi
        player1.resetForNewGame();
        player2.resetForNewGame();
        player1.setBusy(true);
        player2.setBusy(true);
        player1.setMyTurn(true); // Người thách đấu ném trước
        player2.setMyTurn(false);

        // Random 3 phụ trợ cho mỗi người
        player1.setAvailablePowerUps(new ArrayList<>(Arrays.asList(GameEngine.generateRandomPowerUps())));
        player2.setAvailablePowerUps(new ArrayList<>(Arrays.asList(GameEngine.generateRandomPowerUps())));

        // Khởi tạo pending power-ups
        pendingPowerUps.put(player1, new ArrayList<>());
        pendingPowerUps.put(player2, new ArrayList<>());
    }

    /**
     * Xử lý 1 lượt ném của người chơi hiện tại
     *
     * @param playerX     tọa độ X nhập vào
     * @param playerY     tọa độ Y nhập vào
     * @param usedPowerUp phụ trợ sử dụng (có thể null)
     * @return kết quả lượt ném
     */
    public GameEngine.ThrowResult processPlayerThrow(int playerX, int playerY, PowerUp usedPowerUp) {
        if (gameEnded) {
            throw new IllegalStateException("Game đã kết thúc!");
        }

        // Random lực đẩy
        int force = GameEngine.generateRandomForce();

        // Chuẩn bị phụ trợ kích hoạt
        PowerUp[] activePowerUps = prepareActivePowerUps(usedPowerUp);

        // ✅ FIX: Xác định đúng isOpponentTurn
        // Nếu có power-up HALF_OPPONENT_NEXT từ đối thủ đang pending, thì đây là lượt
        // bị ảnh hưởng
        Player opponent = getOpponent();
        boolean hasOpponentHalfEffect = pendingPowerUps.get(opponent).contains(PowerUp.HALF_OPPONENT_NEXT);

        // Xử lý lượt ném
        GameEngine.ThrowResult result = GameEngine.processThrow(
                playerX, playerY, force, colorBoard, activePowerUps, hasOpponentHalfEffect);

        // Cập nhật điểm
        currentPlayer.setCurrentScore(currentPlayer.getCurrentScore() + result.finalScore);

        // Xử lý phụ trợ ảnh hưởng bảng màu
        handleBoardEffects(usedPowerUp, result);

        // Xử lý phụ trợ ảnh hưởng đối thủ (pending)
        handleOpponentEffects(usedPowerUp);

        // Remove phụ trợ đã sử dụng
        if (usedPowerUp != null) {
            currentPlayer.getAvailablePowerUps().remove(usedPowerUp);
        }

        // Kiểm tra điều kiện thắng
        checkWinCondition();

        // Chuyển lượt (nếu không có extra turn)
        if (!result.hasExtraTurn && !gameEnded) {
            switchTurn();
        }

        return result;
    }

    /**
     * Chuẩn bị danh sách phụ trợ kích hoạt cho lượt này
     */
    private PowerUp[] prepareActivePowerUps(PowerUp usedPowerUp) {
        List<PowerUp> active = new ArrayList<>();

        // Thêm phụ trợ người chơi sử dụng
        if (usedPowerUp != null) {
            active.add(usedPowerUp);
        }

        // Thêm phụ trợ pending từ đối thủ (ví dụ: HALF_OPPONENT_NEXT)
        Player opponent = getOpponent();
        List<PowerUp> pending = pendingPowerUps.get(opponent);
        active.addAll(pending);
        pending.clear(); // Clear sau khi áp dụng

        return active.toArray(new PowerUp[0]);
    }

    /**
     * Xử lý phụ trợ ảnh hưởng bảng màu
     */
    private void handleBoardEffects(PowerUp usedPowerUp, GameEngine.ThrowResult result) {
        if (usedPowerUp == null)
            return;

        switch (usedPowerUp) {
            case REVEAL_COLORS:
                colorBoard.revealOnlyThreeColors();
                break;
            case SWAP_OPPONENT_COLORS:
                colorBoard.swapRandomColors();
                break;
            case DOUBLE_SCORE:
            case HALF_OPPONENT_NEXT:
            case EXTRA_TURN:
            case ZERO_FORCE:
                // Những power-up này không ảnh hưởng trực tiếp đến bảng màu
                break;
        }

        // TODO: Logic đổi màu cần được xử lý từ client
        // Client cần gửi thông tin màu muốn đổi trong MoveRequest
        // Hiện tại tạm thời comment auto swap
        // if (result.hitColor != null) {
        // Người chơi có quyền đổi màu trúng với màu khác
        // Cần thêm field Color swapFrom, Color swapTo vào MoveRequest
        // }
    }

    /**
     * Xử lý phụ trợ ảnh hưởng đối thủ (lưu vào pending)
     */
    private void handleOpponentEffects(PowerUp usedPowerUp) {
        if (usedPowerUp == PowerUp.HALF_OPPONENT_NEXT) {
            Player opponent = getOpponent();
            pendingPowerUps.get(opponent).add(PowerUp.HALF_OPPONENT_NEXT);
        }
    }

    /**
     * Kiểm tra điều kiện thắng (16 điểm)
     */
    private void checkWinCondition() {
        if (currentPlayer.getCurrentScore() >= 16) {
            endGame(currentPlayer, GameEndReason.REACH_TARGET_SCORE);
        }
    }

    /**
     * Chuyển lượt chơi
     */
    private void switchTurn() {
        currentPlayer.setMyTurn(false);
        currentPlayer = (currentPlayer == player1) ? player2 : player1;
        currentPlayer.setMyTurn(true);
    }

    /**
     * Kết thúc game với lý do
     */
    public void endGame(Player winner, GameEndReason reason) {

        if (gameEnded)
            return;

        this.gameEnded = true;
        this.winner = winner;
        this.endReason = reason;

        // Cập nhật ELO
        updateEloRatings(reason);

        // Reset trạng thái busy
        player1.setBusy(false);
        player2.setBusy(false);

        // Log kết quả
        Player loser = (winner == player1) ? player2 : player1;
        System.out.println("🏆 GAME KẾT THÚC:");
        System.out.println("   Winner: " + winner.getUsername() + " (" + winner.getCurrentScore() + " điểm)");
        System.out.println("   Loser: " + loser.getUsername() + " (" + loser.getCurrentScore() + " điểm)");
        System.out.println("   Reason: " + reason);
        System.out.println(
                "   ELO Change: Winner +" + (reason == GameEndReason.OPPONENT_LEFT ? 51 : 101) + ", Loser -36");

    }

    /**
     * Cập nhật ELO theo kết quả trận đấu
     */
    private void updateEloRatings(GameEndReason reason) {
        Player loser = (winner == player1) ? player2 : player1;

        switch (reason) {
            case REACH_TARGET_SCORE:
                winner.updateElo(101, true); // Thắng bình thường
                loser.updateElo(-36, false); // Thua bình thường
                break;
            case OPPONENT_LEFT:
                winner.updateElo(51, true); // Thắng do đối thủ rời
                loser.updateElo(-51, false); // Thua do rời game (bị phạt nặng hơn)
                break;
        }
    }

    /**
     * Xử lý người chơi rời trận
     */
    public void playerLeft(Player player) {
        Player remaining = (player == player1) ? player2 : player1;
        endGame(remaining, GameEndReason.OPPONENT_LEFT);
    }

    /**
     * Lấy đối thủ của current player
     */
    private Player getOpponent() {
        return (currentPlayer == player1) ? player2 : player1;
    }

    // Getters
    public Player getPlayer1() {
        return player1;
    }

    public Player getPlayer2() {
        return player2;
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    public String getId() {
        return id;
    }

    public ColorBoard getColorBoard() {
        return colorBoard;
    }

    public boolean isGameEnded() {
        return gameEnded;
    }

    public Player getWinner() {
        return winner;
    }

    public GameEndReason getEndReason() {
        return endReason;
    }

    // ✅ THÊM: Getter/Setter cho matchId
    public int getMatchId() {
        return matchId;
    }

    public void setMatchId(int matchId) {
        this.matchId = matchId;
    }

}