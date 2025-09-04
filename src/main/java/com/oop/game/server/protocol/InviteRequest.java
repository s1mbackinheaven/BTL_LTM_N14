package com.oop.game.server.protocol;

/**
 * Lời mời thách đấu
 */
public class InviteRequest extends Message {
    private String targetUsername; // Người được mời

    public InviteRequest(String senderUsername, String targetUsername) {
        super(MessageType.INVITE_REQUEST, senderUsername);
        this.targetUsername = targetUsername;
    }

    public String getTargetUsername() {
        return targetUsername;
    }
}
