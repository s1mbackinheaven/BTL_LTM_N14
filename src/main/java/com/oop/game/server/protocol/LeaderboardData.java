package com.oop.game.server.protocol;

import com.oop.game.server.enums.MessageType;

import java.util.List;

/**
 * Dữ liệu bảng xếp hạng
 */
public class LeaderboardData extends Message {
    private List<LeaderboardEntry> entries;

    public LeaderboardData(String serverName, List<LeaderboardEntry> entries) {
        super(MessageType.LEADERBOARD_RESPONSE, serverName);
        this.entries = entries;
    }

    public List<LeaderboardEntry> getEntries() {
        return entries;
    }

    /**
     * 1 entry trong bảng xếp hạng
     */
    public static class LeaderboardEntry implements java.io.Serializable {
        public final int rank;
        public final String username;
        public final int elo;
        public final int totalWins;
        public final int totalLosses;

        public LeaderboardEntry(int rank, String username, int elo, int totalWins, int totalLosses) {
            this.rank = rank;
            this.username = username;
            this.elo = elo;
            this.totalWins = totalWins;
            this.totalLosses = totalLosses;
        }
    }
}
