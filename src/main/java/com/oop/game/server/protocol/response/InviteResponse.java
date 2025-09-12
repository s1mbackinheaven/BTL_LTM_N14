package com.oop.game.server.protocol.response;

import com.oop.game.server.enums.MessageType;
import com.oop.game.server.protocol.Message;

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

    public void sendTarget() {

    }
}