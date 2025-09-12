package com.oop.game.server.protocol.request;

import com.oop.game.server.enums.MessageType;
import com.oop.game.server.protocol.Message;

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
