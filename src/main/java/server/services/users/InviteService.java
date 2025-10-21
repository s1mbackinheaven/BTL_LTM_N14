package server.services.users;

import server.enums.InviteStatus;
import server.managers.ClientManager;
import server.managers.InviteManager;
import server.models.Invite;
import server.models.Player;

/**
 * Service xử lý business logic cho invite system
 */
public class InviteService {
    private final InviteManager inviteManager;
    private final ClientManager clientManager;

    public InviteService() {
        this.inviteManager = InviteManager.getInstance();
        this.clientManager = ClientManager.getInstance();
    }

    public InviteStatus validateAndCreateInvite(String senderUsername, String targetUsername) {
        // 1. Validate input
        if (senderUsername == null || senderUsername.isBlank() || targetUsername == null || targetUsername.isBlank()) {
            return InviteStatus.INVALID_INPUT;
        }

        // 2. Kiểm tra không thể mời chính mình
        if (senderUsername.equals(targetUsername)) {
            return InviteStatus.SELF_INVITE;
        }

        // 3. Kiểm tra người gửi có online không
        Player sender = clientManager.getUserByName(senderUsername);
        if (sender == null) {
            return InviteStatus.SENDER_NOT_FOUND;
        }

        // 4. Kiểm tra người nhận có online không
        Player target = clientManager.getUserByName(targetUsername);
        if (target == null) {
            return InviteStatus.TARGET_NOT_FOUND;
        }

        // 5. Kiểm tra người gửi có đang bận không
        if (sender.isBusy()) {
            return InviteStatus.SENDER_BUSY;
        }

        // 6. Kiểm tra người nhận có đang bận không
        if (target.isBusy()) {
            return InviteStatus.TARGET_BUSY;
        }

        // 7. Kiểm tra đã gửi invite trước đó chưa
        if (inviteManager.hasInvite(senderUsername, targetUsername)) {
            return InviteStatus.DUPLICATE_INVITE;
        }

        // 8. Tạo invite mới
        Invite invite = new Invite(senderUsername, targetUsername);
        boolean added = inviteManager.addInvite(invite);

        if (!added) {
            return InviteStatus.DUPLICATE_INVITE;
        }

        return InviteStatus.SENT;
    }

    /**
     * Validate response (accept/reject)
     */
    public InviteStatus validateInviteResponse(String responderUsername, String inviterUsername) {
        // 1. Validate input
        if (responderUsername == null || responderUsername.isBlank() ||
                inviterUsername == null || inviterUsername.isBlank()) {
            return InviteStatus.INVALID_INPUT;
        }

        // 2. Kiểm tra người phản hồi có online không
        Player responder = clientManager.getUserByName(responderUsername);
        if (responder == null) {
            return InviteStatus.TARGET_NOT_FOUND;
        }

        // 3. Kiểm tra người mời có online không
        Player inviter = clientManager.getUserByName(inviterUsername);
        if (inviter == null) {
            return InviteStatus.SENDER_NOT_FOUND;
        }

        // 4. Kiểm tra invite có tồn tại không
        Invite invite = inviteManager.getInvite(inviterUsername, responderUsername);
        if (invite == null || !invite.isPending()) {
            return InviteStatus.EXPIRED;
        }

        return InviteStatus.ACCEPTED; // Validation passed
    }

    /**
     * Kiểm tra 2 người chơi có thể bắt đầu game không
     */
    public InviteStatus validateGameStart(String player1Username, String player2Username) {
        Player player1 = clientManager.getUserByName(player1Username);
        Player player2 = clientManager.getUserByName(player2Username);

        if (player1 == null || player2 == null) {
            return InviteStatus.SENDER_NOT_FOUND;
        }

        if (player1.isBusy() || player2.isBusy()) {
            return InviteStatus.SENDER_BUSY;
        }

        return InviteStatus.ACCEPTED;
    }

    /**
     * Handle khi invite được accept
     */
    public void handleInviteAccepted(String inviterUsername, String responderUsername) {
        // Remove invite khỏi pending
        inviteManager.removeInvite(inviterUsername, responderUsername);

        // Remove các invite khác liên quan đến 2 người này
        inviteManager.removeAllInvitesForUser(inviterUsername);
        inviteManager.removeAllInvitesForUser(responderUsername);
    }

    /**
     * Handle khi invite bị reject
     */
    public void handleInviteRejected(String inviterUsername, String responderUsername) {
        // Chỉ remove invite này
        inviteManager.removeInvite(inviterUsername, responderUsername);
    }

    /**
     * Handle khi invite bị cancel
     */
    public void handleInviteCancel(String inviterUsername, String targetUsername) {
        inviteManager.removeInvite(inviterUsername, targetUsername);
    }

    /**
     * Kiểm tra có cross-invite không (A mời B, B mời A)
     * Nếu có thì auto-match
     */
    public boolean checkAndHandleCrossInvite(String username1, String username2) {
        if (inviteManager.hasCrossInvite(username1, username2)) {
            System.out.println("🎯 [InviteService] Cross-invite detected: " + username1 + " <-> " + username2);
            // Remove cả 2 invite
            inviteManager.removeInvite(username1, username2);
            inviteManager.removeInvite(username2, username1);
            return true;
        }
        return false;
    }

    /**
     * Lấy thông tin Player
     */
    public Player getPlayer(String username) {
        return clientManager.getUserByName(username);
    }
}
