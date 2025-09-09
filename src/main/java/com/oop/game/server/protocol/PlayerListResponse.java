package com.oop.game.server.protocol;

import com.oop.game.server.enums.MessageType;

import java.util.List;

/**
 * Danh sách người chơi online
 */
public class PlayerListResponse extends Message {
    private List<OnlinePlayer> onlinePlayers;

    public PlayerListResponse(String serverName, List<OnlinePlayer> onlinePlayers) {
        super(MessageType.PLAYER_LIST_RESPONSE, serverName);
        this.onlinePlayers = onlinePlayers;
    }

    public List<OnlinePlayer> getOnlinePlayers() {
        return onlinePlayers;
    }

    /**
     * Thông tin người chơi online
     */
    public static class OnlinePlayer implements java.io.Serializable {
        public final String username;
        public final int elo;
        public final boolean isBusy; // Đang trong trận hay không

        public OnlinePlayer(String username, int elo, boolean isBusy) {
            this.username = username;
            this.elo = elo;
            this.isBusy = isBusy;
        }
    }
}
