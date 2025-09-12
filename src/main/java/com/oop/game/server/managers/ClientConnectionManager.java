package com.oop.game.server.managers;

import com.oop.game.server.protocol.Message;

import java.io.ObjectOutputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Quản lý kết nối socket của các client
 * Cho phép gửi message đến client cụ thể
 */
public class ClientConnectionManager {
    private final Map<String, ObjectOutputStream> clientConnections;

    private static ClientConnectionManager instance;

    public static synchronized ClientConnectionManager getInstance() {
        if (instance == null) {
            instance = new ClientConnectionManager();
        }
        return instance;
    }

    private ClientConnectionManager() {
        this.clientConnections = new ConcurrentHashMap<>();
    }

    /**
     * Đăng ký kết nối của client
     * 
     * @param username     Tên người chơi
     * @param outputStream Output stream của client
     */
    public void registerClient(String username, ObjectOutputStream outputStream) {
        clientConnections.put(username, outputStream);
        System.out.println("🔗 Đăng ký kết nối cho " + username);
    }

    /**
     * Hủy đăng ký kết nối của client
     * 
     * @param username Tên người chơi
     */
    public void unregisterClient(String username) {
        clientConnections.remove(username);
        System.out.println("🔌 Hủy đăng ký kết nối cho " + username);
    }

    /**
     * Gửi message đến client cụ thể
     * 
     * @param username Tên người chơi nhận message
     * @param message  Message cần gửi
     * @return true nếu gửi thành công, false nếu thất bại
     */
    public boolean sendMessageToClient(String username, Message message) {
        ObjectOutputStream outputStream = clientConnections.get(username);
        if (outputStream == null) {
            System.err.println("❌ Không tìm thấy kết nối cho " + username);
            return false;
        }

        try {
            outputStream.writeObject(message);
            outputStream.flush();
            System.out.println("📤 Đã gửi message đến " + username + ": " + message.getClass().getSimpleName());
            return true;
        } catch (Exception e) {
            System.err.println("❌ Lỗi khi gửi message đến " + username + ": " + e.getMessage());
            // Xóa kết nối bị lỗi
            clientConnections.remove(username);
            return false;
        }
    }

    /**
     * Gửi message đến nhiều client
     * 
     * @param usernames Danh sách tên người chơi
     * @param message   Message cần gửi
     * @return Số lượng client nhận được message thành công
     */
    public int sendMessageToClients(String[] usernames, Message message) {
        int successCount = 0;
        for (String username : usernames) {
            if (sendMessageToClient(username, message)) {
                successCount++;
            }
        }
        return successCount;
    }

    /**
     * Kiểm tra client có đang kết nối không
     * 
     * @param username Tên người chơi
     * @return true nếu đang kết nối
     */
    public boolean isClientConnected(String username) {
        return clientConnections.containsKey(username);
    }

    /**
     * Lấy số lượng client đang kết nối
     * 
     * @return Số lượng client
     */
    public int getConnectedClientCount() {
        return clientConnections.size();
    }

    /**
     * In thông tin debug
     */
    public void printStatus() {
        System.out.println("=== ClientConnectionManager Status ===");
        System.out.println("Connected clients: " + getConnectedClientCount());
        System.out.println("Usernames: " + clientConnections.keySet());
        System.out.println("=====================================");
    }
}
