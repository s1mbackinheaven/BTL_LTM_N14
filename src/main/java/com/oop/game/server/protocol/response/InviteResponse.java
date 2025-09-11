package com.oop.game.server.protocol;

/**
 * Phản hồi lời mời thách đấu
 */
public class InviteResponse extends Message {
    private boolean accepted;
    private String inviterUsername; // Người đã mời

    public InviteResponse(String responderUsername, String inviterUsername, boolean accepted) {
        super(MessageType.INVITE_RESPONSE, responderUsername);
        this.inviterUsername = inviterUsername;
        this.accepted = accepted;
    }

    public boolean isAccepted() {
        return accepted;
    }

    public String getInviterUsername() {
        return inviterUsername;
    }
}