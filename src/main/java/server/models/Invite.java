package server.models;

import server.enums.InviteStatus;

import java.time.Instant;

/**
 * Model đại diện cho một lời mời thách đấu
 */
public class Invite {
    private final String inviterUsername; // Người gửi lời mời
    private final String targetUsername; // Người nhận lời mời
    private final Instant createdAt; // Thời gian tạo
    private final long expiresAt; // Thời gian hết hạn (timestamp)
    private InviteStatus status; // Trạng thái hiện tại

    public static final long INVITE_TIMEOUT_MS = 30_000; // 30 giây

    public Invite(String inviterUsername, String targetUsername) {
        this.inviterUsername = inviterUsername;
        this.targetUsername = targetUsername;
        this.createdAt = Instant.now();
        this.expiresAt = System.currentTimeMillis() + INVITE_TIMEOUT_MS;
        this.status = InviteStatus.SENT;
    }

    /**
     * Kiểm tra lời mời đã hết hạn chưa
     */
    public boolean isExpired() {
        return System.currentTimeMillis() > expiresAt;
    }

    /**
     * Kiểm tra lời mời có còn pending không
     */
    public boolean isPending() {
        return status == InviteStatus.SENT && !isExpired();
    }

    /**
     * Lấy số giây còn lại
     */
    public long getRemainingSeconds() {
        long remaining = (expiresAt - System.currentTimeMillis()) / 1000;
        return Math.max(0, remaining);
    }

    // Getters
    public String getInviterUsername() {
        return inviterUsername;
    }

    public String getTargetUsername() {
        return targetUsername;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public long getExpiresAt() {
        return expiresAt;
    }

    public InviteStatus getStatus() {
        return status;
    }

    // Setter for status
    public void setStatus(InviteStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Invite{" +
                "inviter='" + inviterUsername + '\'' +
                ", target='" + targetUsername + '\'' +
                ", status=" + status +
                ", remaining=" + getRemainingSeconds() + "s" +
                '}';
    }

    /**
     * Tạo key unique cho invite (inviter -> target)
     */
    public static String createKey(String inviterUsername, String targetUsername) {
        return inviterUsername + "->" + targetUsername;
    }

    /**
     * Lấy key của invite này
     */
    public String getKey() {
        return createKey(inviterUsername, targetUsername);
    }
}
