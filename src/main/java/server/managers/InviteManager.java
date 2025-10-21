package server.managers;

import server.enums.InviteStatus;
import server.models.Invite;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Quản lý các lời mời thách đấu đang pending
 * Singleton pattern - thread-safe
 */
public class InviteManager {

    private final Map<String, Invite> pendingInvites;

    private static InviteManager instance;

    public static synchronized InviteManager getInstance() {
        if (instance == null) {
            instance = new InviteManager();
        }
        return instance;
    }

    private InviteManager() {
        this.pendingInvites = new ConcurrentHashMap<>();
        // Khởi động cleanup thread
        startCleanupTask();
    }

    public boolean addInvite(Invite invite) {
        String key = invite.getKey();

        // Kiểm tra đã tồn tại chưa
        if (pendingInvites.containsKey(key)) {
            return false;
        }

        pendingInvites.put(key, invite);
        System.out.println("📨 [InviteManager] Thêm invite: " + invite);
        return true;
    }

    public Invite getInvite(String inviterUsername, String targetUsername) {
        String key = Invite.createKey(inviterUsername, targetUsername);
        Invite invite = pendingInvites.get(key);

        // Nếu invite hết hạn, tự động remove
        if (invite != null && invite.isExpired()) {
            removeInvite(inviterUsername, targetUsername);
            return null;
        }

        return invite;
    }

    public boolean hasInvite(String inviterUsername, String targetUsername) {
        Invite invite = getInvite(inviterUsername, targetUsername);
        return invite != null && invite.isPending();
    }

    /**
     * Kiểm tra có cross-invite không (A mời B, B mời A)
     */
    public boolean hasCrossInvite(String username1, String username2) {
        return hasInvite(username1, username2) && hasInvite(username2, username1);
    }

    /**
     * Remove invite khỏi hệ thống
     */
    public void removeInvite(String inviterUsername, String targetUsername) {
        String key = Invite.createKey(inviterUsername, targetUsername);
        Invite removed = pendingInvites.remove(key);
        if (removed != null) {
            System.out.println("🗑️ [InviteManager] Remove invite: " + removed);
        }
    }

    public void removeAllInvitesForUser(String username) {
        List<String> keysToRemove = pendingInvites.keySet().stream()
                .filter(key -> key.startsWith(username + "->") || key.endsWith("->" + username))
                .collect(Collectors.toList());

        for (String key : keysToRemove) {
            pendingInvites.remove(key);
        }

        if (!keysToRemove.isEmpty()) {
            System.out.println("🗑️ [InviteManager] Removed " + keysToRemove.size() + " invites for " + username);
        }
    }

    /**
     * Lấy tất cả invite mà user đã gửi (đang pending)
     */
    public List<Invite> getInvitesSentBy(String username) {
        return pendingInvites.values().stream()
                .filter(invite -> invite.getInviterUsername().equals(username))
                .filter(Invite::isPending)
                .collect(Collectors.toList());
    }

    /**
     * Lấy tất cả invite mà user nhận được (đang pending)
     */
    public List<Invite> getInvitesReceivedBy(String username) {
        return pendingInvites.values().stream()
                .filter(invite -> invite.getTargetUsername().equals(username))
                .filter(Invite::isPending)
                .collect(Collectors.toList());
    }

    /**
     * Cleanup expired invites định kỳ
     */
    private void startCleanupTask() {
        Thread cleanupThread = new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(10_000); // Check mỗi 10 giây
                    cleanupExpiredInvites();
                } catch (InterruptedException e) {
                    System.err.println("⚠️ [InviteManager] Cleanup thread interrupted");
                    break;
                }
            }
        });
        cleanupThread.setDaemon(true);
        cleanupThread.setName("InviteCleanupThread");
        cleanupThread.start();
        System.out.println("🧹 [InviteManager] Cleanup task started");
    }

    /**
     * Xóa các invite đã expired
     */
    private void cleanupExpiredInvites() {
        List<String> expiredKeys = pendingInvites.entrySet().stream()
                .filter(entry -> entry.getValue().isExpired())
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        for (String key : expiredKeys) {
            Invite invite = pendingInvites.remove(key);
            if (invite != null) {
                invite.setStatus(InviteStatus.EXPIRED);
                System.out.println("⏰ [InviteManager] Invite expired: " + invite);
            }
        }
    }

    /**
     * In trạng thái hiện tại (debug)
     */
    public void printStatus() {
        System.out.println("=== InviteManager Status ===");
        System.out.println("Pending invites: " + pendingInvites.size());
        pendingInvites.values().forEach(invite -> System.out.println("  - " + invite));
        System.out.println("===========================");
    }

    /**
     * Lấy số lượng invite đang pending
     */
    public int getPendingInviteCount() {
        return (int) pendingInvites.values().stream()
                .filter(Invite::isPending)
                .count();
    }
}
