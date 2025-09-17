package com.oop.game.server.managers;

import com.oop.game.server.DAO.MatchDAO;
import com.oop.game.server.DAO.UserDAO;
import com.oop.game.server.core.GameSession;
import com.oop.game.server.core.Player;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Quản lý các trận đấu đang diễn ra
 * Singleton pattern để đảm bảo chỉ có 1 instance
 */
public class GameSessionManager {
    private final Map<String, GameSession> activeSessions;
    private final Map<String, String> playerToSession; // username -> sessionId
    private final MatchDAO matchDAO;
    private static GameSessionManager instance;

    public static synchronized GameSessionManager getInstance() {
        if (instance == null) {
            instance = new GameSessionManager();
        }
        return instance;
    }

    private GameSessionManager() {
        this.matchDAO = new MatchDAO();
        this.activeSessions = new ConcurrentHashMap<>();
        this.playerToSession = new ConcurrentHashMap<>();
    }

    /**
     * Tạo trận đấu mới giữa 2 người chơi
     *
     * @param challenger Người thách đấu
     * @param challenged Người được thách đấu
     * @return SessionId của trận đấu mới
     */
    public String createGameSession(Player challenger, Player challenged) {

        String sessionId = generateSessionId(challenger.getUsername(), challenged.getUsername());

        GameSession session = new GameSession(challenger, challenged, sessionId);

        activeSessions.put(sessionId, session);

        // ✅ LƯU VÀO DATABASE: Tạo match record ngay khi bắt đầu game

        int matchId = matchDAO.createMatch(challenger.getId(), challenged.getId());

        if (matchId > 0) {
            session.setMatchId(matchId); // Cần thêm field này vào GameSession
            System.out.println("💾 Đã tạo match record trong DB với ID: " + matchId);
        } else {
            System.err.println("❌ Lỗi tạo match record trong DB!");
        }

        // Lưu mapping player -> session
        playerToSession.put(challenger.getUsername(), sessionId);
        playerToSession.put(challenged.getUsername(), sessionId);

        System.out.println("🎮 Tạo trận đấu mới: " + sessionId + " giữa " +
                challenger.getUsername() + " vs " + challenged.getUsername());

        return sessionId;
    }

    /**
     * Lấy trận đấu theo session ID
     */
    public GameSession getSessionById(String sessionId) {
        return activeSessions.get(sessionId);
    }

    public String getSessionIdByUsername(String un) {
        return playerToSession.get(un);
    }

    /**
     * Lấy trận đấu theo username của người chơi
     */
    public GameSession getSessionByPlayer(String username) {
        String sessionId = playerToSession.get(username);
        return sessionId != null ? activeSessions.get(sessionId) : null;
    }

    /**
     * Kiểm tra người chơi có đang trong trận đấu không
     */
    public boolean isPlayerInGame(String username) {
        return playerToSession.containsKey(username);
    }

    /**
     * Lấy số lượng trận đấu đang diễn ra
     */
    public int getActiveSessionCount() {
        return activeSessions.size();
    }

    /**
     * Tạo session ID duy nhất
     */
    private String generateSessionId(String player1, String player2) {
        // Sắp xếp tên theo thứ tự alphabet để đảm bảo tính nhất quán
        String[] names = { player1, player2 };
        java.util.Arrays.sort(names);
        return names[0] + "_vs_" + names[1] + "_" + System.currentTimeMillis();
    }

    public void endGameSession(String sessionId) {

        GameSession session = activeSessions.remove(sessionId);

        if (session != null) {

            // ✅ LƯU KẾT QUẢ VÀO DATABASE
            saveGameResultToDatabase(session);

            // Xóa mapping player -> session
            playerToSession.remove(session.getPlayer1().getUsername());
            playerToSession.remove(session.getPlayer2().getUsername());

            System.out.println("✅ Kết thúc trận đấu: " + sessionId
                    + " | Winner: " + (session.getWinner() != null ? session.getWinner().getUsername() : "null")
                    + " | Reason: " + session.getEndReason());
        }
    }

    /**
     * Lưu kết quả game vào database
     */
    private void saveGameResultToDatabase(GameSession session) {
        if (session.getMatchId() <= 0) {
            System.err.println("❌ Không có match ID để lưu kết quả!");
            return;
        }

        try {
            Player winner = session.getWinner();
            Player loser = (winner == session.getPlayer1()) ? session.getPlayer2() : session.getPlayer1();

            if (winner != null) {
                // Tính ELO change dựa trên lý do kết thúc
                int eloChange = switch (session.getEndReason()) {
                    case REACH_TARGET_SCORE -> 101;
                    case OPPONENT_LEFT -> 51;
                    default -> 0;
                };

                // Lưu kết quả match
                boolean saved = matchDAO.finishMatch(
                        session.getMatchId(),
                        winner.getId(),
                        winner.getCurrentScore(),
                        loser.getCurrentScore(),
                        eloChange);

                if (saved) {
                    // Cập nhật thống kê users
                    updateUserStats(winner, loser);
                    System.out.println("💾 Đã lưu kết quả trận đấu vào DB");
                } else {
                    System.err.println("❌ Lỗi lưu kết quả trận đấu!");
                }

            } else {
                System.err.println("❌ Không có winner để lưu kết quả!");
            }

        } catch (Exception e) {
            System.err.println("❌ Lỗi lưu game result: " + e.getMessage());
        }
    }

    /**
     * Cập nhật thống kê win/loss của users
     */
    private void updateUserStats(Player winner, Player loser) {
        try {
            UserDAO userDAO = new UserDAO();

            // Cập nhật winner
            userDAO.updateUserStats(winner.getId(), winner.getElo(), true);

            // Cập nhật loser
            userDAO.updateUserStats(loser.getId(), loser.getElo(), false);

            System.out.println("📊 Đã cập nhật thống kê users");

        } catch (Exception e) {
            System.err.println("❌ Lỗi cập nhật user stats: " + e.getMessage());
        }
    }

    /**
     * In thông tin debug
     */
    public void printStatus() {
        System.out.println("=== GameSessionManager Status ===");
        System.out.println("Active sessions: " + getActiveSessionCount());
        System.out.println("Sessions: " + activeSessions.keySet());
        System.out.println("Player mappings: " + playerToSession);
        System.out.println("================================");
    }
}
