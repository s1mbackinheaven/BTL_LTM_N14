package com.oop.game.server.demo;

import com.oop.game.server.core.Player;
import com.oop.game.server.managers.ClientManager;
import com.oop.game.server.managers.GameSessionManager;
import com.oop.game.server.protocol.request.InviteRequest;
import com.oop.game.server.protocol.response.InviteResponse;

/**
 * Demo class để test chức năng invitation system
 * Chạy class này để xem cách hoạt động của hệ thống thách đấu
 */
public class InvitationDemo {

    public static void main(String[] args) {
        System.out.println("🎯 Demo hệ thống thách đấu - Game Ném Phi Tiêu");
        System.out.println("=".repeat(50));

        // Khởi tạo các manager
        ClientManager clientManager = ClientManager.getInstance();
        GameSessionManager gameSessionManager = GameSessionManager.getInstance();

        // Tạo 2 người chơi demo
        Player player1 = new Player("Alice");
        Player player2 = new Player("Bob");

        // Thêm vào danh sách online
        clientManager.addUserOnline(player1);
        clientManager.addUserOnline(player2);

        System.out.println("👥 Danh sách người chơi online:");
        clientManager.getListUserOnline().forEach(p -> System.out
                .println("  - " + p.getUsername() + " (ELO: " + p.getElo() + ", Busy: " + p.isBusy() + ")"));

        System.out.println("\n📨 Alice gửi lời mời thách đấu đến Bob...");

        // Tạo invitation request
        InviteRequest inviteRequest = new InviteRequest("Alice", "Bob");
        System.out.println("  Request: " + inviteRequest);

        // Kiểm tra trạng thái trước khi mời
        System.out.println("\n🔍 Trạng thái trước khi mời:");
        System.out.println("  Alice busy: " + player1.isBusy());
        System.out.println("  Bob busy: " + player2.isBusy());

        // Giả lập Bob chấp nhận lời mời
        System.out.println("\n✅ Bob chấp nhận lời mời...");
        InviteResponse acceptResponse = new InviteResponse("Bob", "Alice", true);
        System.out.println("  Response: " + acceptResponse);

        // Tạo trận đấu
        String sessionId = gameSessionManager.createGameSession(player1, player2);
        System.out.println("  Session ID: " + sessionId);

        // Kiểm tra trạng thái sau khi bắt đầu trận
        System.out.println("\n🔍 Trạng thái sau khi bắt đầu trận:");
        System.out.println("  Alice busy: " + player1.isBusy());
        System.out.println("  Bob busy: " + player2.isBusy());
        System.out.println("  Alice trong game: " + gameSessionManager.isPlayerInGame("Alice"));
        System.out.println("  Bob trong game: " + gameSessionManager.isPlayerInGame("Bob"));

        // Hiển thị thông tin trận đấu
        System.out.println("\n📊 Thông tin trận đấu:");
        System.out.println("  Số trận đang diễn ra: " + gameSessionManager.getActiveSessionCount());
        System.out.println("  Session: " + gameSessionManager.getSession(sessionId));

        // Kết thúc trận đấu
        System.out.println("\n🏁 Kết thúc trận đấu...");
        gameSessionManager.endGameSession(sessionId);

        System.out.println("\n🔍 Trạng thái sau khi kết thúc trận:");
        System.out.println("  Alice busy: " + player1.isBusy());
        System.out.println("  Bob busy: " + player2.isBusy());
        System.out.println("  Số trận đang diễn ra: " + gameSessionManager.getActiveSessionCount());

        System.out.println("\n✅ Demo hoàn thành!");
        System.out.println("=".repeat(50));
    }
}