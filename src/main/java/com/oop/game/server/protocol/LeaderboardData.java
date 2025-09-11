package com.oop.game.server.protocol;

import java.util.List;

import com.oop.game.server.dto.LeaderboardEntryDTO;

/**
 * Dữ liệu bảng xếp hạng
 */
public class LeaderboardData extends Message {
    private List<LeaderboardEntryDTO> entries;

    public LeaderboardData(String serverName, List<LeaderboardEntryDTO> entries) {
        super(MessageType.LEADERBOARD_RESPONSE, serverName);
        this.entries = entries;
    }

    public List<LeaderboardEntryDTO> getEntries() {
        return entries;
    }
}
