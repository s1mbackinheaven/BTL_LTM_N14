package com.oop.game.server.managers;

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

    private static GameSessionManager instance;

    public static synchronized GameSessionManager getInstance() {
        if (instance == null) {
            instance = new GameSessionManager();
        }
        return instance;
    }

    private GameSessionManager() {
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

        GameSession session = new GameSession(challenger, challenged);

        activeSessions.put(sessionId, session);

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
    public GameSession getSession(String sessionId) {
        return activeSessions.get(sessionId);
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
        String[] names = {player1, player2};
        java.util.Arrays.sort(names);
        return names[0] + "_vs_" + names[1] + "_" + System.currentTimeMillis();
    }

    public void endGameSession(String sessionId) {
        GameSession session = activeSessions.remove(sessionId);
        if (session != null) {
            // Xóa mapping player -> session
            playerToSession.remove(session.getPlayer1().getUsername());
            playerToSession.remove(session.getPlayer2().getUsername());

            System.out.println("✅ Kết thúc trận đấu: " + sessionId
                    + " | Winner: " + (session.getWinner() != null ? session.getWinner().getUsername() : "null")
                    + " | Reason: " + session.getEndReason());
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
