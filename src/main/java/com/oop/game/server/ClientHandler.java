package com.oop.game.server;

import com.oop.game.server.core.GameSession;
import com.oop.game.server.core.Player;
import com.oop.game.server.core.GameEngine;
import com.oop.game.server.DAO.UserDAO;
import com.oop.game.server.managers.ClientManager;
import com.oop.game.server.models.User;
import com.oop.game.server.protocol.ErrorMessage;
import com.oop.game.server.protocol.request.LoginRequest;
import com.oop.game.server.protocol.Message;
import com.oop.game.server.protocol.request.InviteRequest;
import com.oop.game.server.protocol.request.MoveRequest;
import com.oop.game.server.protocol.request.PlayerListRequest;
import com.oop.game.server.protocol.response.LoginResponse;
import com.oop.game.server.protocol.response.PlayerListResponse;
import com.oop.game.server.protocol.response.InviteResponse;
import com.oop.game.server.managers.GameSessionManager;
import com.oop.game.server.managers.ClientConnectionManager;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ClientHandler implements Runnable {
    private final Socket socket;
    private final ClientManager mClient;
    private final GameSessionManager gameSessionManager;
    private final ClientConnectionManager connectionManager;
    private Player currentPlayer; // Người chơi hiện tại trong session này

    public ClientHandler(Socket socket) {
        this.socket = socket;
        this.mClient = ClientManager.getInstance();
        this.gameSessionManager = GameSessionManager.getInstance();
        this.connectionManager = ClientConnectionManager.getInstance();
    }

    @Override
    public void run() {
        try (ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream input = new ObjectInputStream(socket.getInputStream())) {

            while (true) {
                try {
                    Object msg = input.readObject();
                    handlerMes(msg, output);
                } catch (ClassNotFoundException e) {
                    System.err.println("❌ Class not found: " + e.getMessage());
                } catch (IOException e) {
                    System.err.println("⚠️ Client " + socket + " đã ngắt kết nối");
                    // Cleanup khi client disconnect
                    cleanupOnDisconnect();
                    break;
                }
            }

        } catch (IOException e) {
            System.err.println("❌ Lỗi IO khi tạo stream cho client " + socket);
        } finally {
            // Cleanup khi kết thúc thread
            cleanupOnDisconnect();
            try {
                socket.close();
            } catch (IOException e) {
                System.err.println("❌ Không thể đóng socket: " + e.getMessage());
            }
        }
    }

    private void handlerMes(Object obj, ObjectOutputStream objOP) throws IOException {
        if (obj instanceof LoginRequest) {
            // đăng nhập
            handlerLoginReq((LoginRequest) obj, objOP);
        } else if (obj instanceof MoveRequest) {
            // ném phi tiêu
            handlerMoveReq((MoveRequest) obj, objOP);
        } else if (obj instanceof InviteRequest) {
            // lời mời thách đấu
            handlerInviteReq((InviteRequest) obj, objOP);
        } else if (obj instanceof InviteResponse) {
            handlerInviteResponse((InviteResponse) obj, objOP);
        } else if (obj instanceof PlayerListRequest) {
            // danh sách người chơi online
            handlerPlayerListReq((PlayerListRequest) obj, objOP);
        } else if (obj instanceof InviteResponse) {
            // phản hồi lời mời thách đấu
            handlerInviteResponse((InviteResponse) obj, objOP);
        } else {
            System.err.println("⚠️ Nhận được message không xác định từ client: " + obj);
        }
    }

    private void handlerLoginReq(LoginRequest req, ObjectOutputStream objOP) {
        UserDAO userDAO = new UserDAO();
        String username = req.getUsername();
        String password = req.getPassword();

        if (username == null || password == null) {
            OP(new LoginResponse(false, "Vui lòng nhập đủ thông tin", null), objOP);
            return;
        }

        if (!userDAO.authenticateUser(username, password)) {
            OP(new LoginResponse(false, "Sai tài khoản hoặc mật khẩu", null), objOP);
            return;
        }

        User user = userDAO.getUserByUsername(username);
        Player player = new Player(user);

        if (mClient.isOnline(player)) {
            OP(new LoginResponse(false, "Người chơi đang đăng nhập ở nơi khác", null), objOP);
            return;
        }

        // Lưu thông tin người chơi hiện tại
        this.currentPlayer = player;

        // Thêm vào danh sách online
        mClient.addUserOnline(player);

        // Đăng ký kết nối để có thể gửi message đến client này
        connectionManager.registerClient(player.getUsername(), objOP);

        OP(new LoginResponse(true, "Đăng nhập thành công", player), objOP);
    }

    private void handlerMoveReq(MoveRequest req, ObjectOutputStream objOP) {
        if (currentPlayer == null) {
            OP(new ErrorMessage("SERVER", "NOT_LOGGED_IN", "Chưa đăng nhập"), objOP);
            return;
        }

        // Kiểm tra người chơi có đang trong trận đấu không
        if (!gameSessionManager.isPlayerInGame(currentPlayer.getUsername())) {
            OP(new ErrorMessage("SERVER", "NOT_IN_GAME", "Không đang trong trận đấu"), objOP);
            return;
        }

        GameSession game = gameSessionManager.getSessionByPlayer(currentPlayer.getUsername());
        if (game == null || game.isGameEnded()) {
            OP(new ErrorMessage("SERVER", "GAME_ENDED", "Trận đấu đã kết thúc"), objOP);
            return;
        }

        try {
            // Xử lý lượt ném
            GameEngine.ThrowResult result = game.processPlayerThrow(
                    req.getX(),
                    req.getY(),
                    req.getUsedPowerUp());

            System.out.println("🎯 " + currentPlayer.getUsername() + " ném (" + req.getX() + "," + req.getY() +
                    ") -> " + result.finalScore + " điểm | Tổng: " + currentPlayer.getCurrentScore());

            // Game sẽ tự động kết thúc trong processPlayerThrow nếu đạt 16 điểm

        } catch (Exception e) {
            System.err.println("❌ Lỗi khi xử lý move request: " + e.getMessage());
            OP(new ErrorMessage("SERVER", "MOVE_FAILED", "Không thể thực hiện nước đi"), objOP);
        }
    }

    private void handlerPlayerListReq(PlayerListRequest req, ObjectOutputStream objOP) {
        // TODO: xử lý player list request
        OP(new PlayerListResponse(req.getSenderUN(), mClient.getListUserOnline()), objOP);
    }

    private void handlerInviteReq(InviteRequest req, ObjectOutputStream objOP) {
        String senderUN = req.getSenderUN();
        String targetUN = req.getTargetUsername();

        // Kiểm tra người gửi có đang online không
        Player sender = mClient.getUserByName(senderUN);
        if (sender == null) {
            OP(new ErrorMessage("SERVER", ErrorMessage.PLAYER_NOT_FOUND, "Người gửi không online"), objOP);
            return;
        }

        // Kiểm tra người được mời có online không
        Player target = mClient.getUserByName(targetUN);
        if (target == null) {
            OP(new ErrorMessage("SERVER", ErrorMessage.PLAYER_NOT_FOUND, "Người chơi " + targetUN + " không online"),
                    objOP);
            return;
        }

        // Kiểm tra người gửi có đang trong trận đấu không
        if (sender.isBusy()) {
            OP(new ErrorMessage("SERVER", ErrorMessage.PLAYER_BUSY, "Bạn đang trong trận đấu"), objOP);
            return;
        }

        // Kiểm tra người được mời có đang trong trận đấu không
        if (target.isBusy()) {
            OP(new ErrorMessage("SERVER", ErrorMessage.PLAYER_BUSY, "Người chơi " + targetUN + " đang bận"), objOP);
            return;
        }

        // Kiểm tra không thể mời chính mình
        if (senderUN.equals(targetUN)) {
            OP(new ErrorMessage("SERVER", "INVALID_INVITE", "Không thể mời chính mình"), objOP);
            return;
        }

        // Gửi lời mời đến người được mời
        InviteRequest inviteToTarget = new InviteRequest(senderUN, targetUN);
        boolean sentSuccessfully = connectionManager.sendMessageToClient(targetUN, inviteToTarget);

        if (sentSuccessfully) {
            // Gửi response xác nhận cho người gửi
            OP(new InviteResponse(senderUN, targetUN, true), objOP);
            System.out.println("📨 " + senderUN + " đã gửi lời mời thách đấu đến " + targetUN);
        } else {
            // Nếu không gửi được, báo lỗi
            OP(new ErrorMessage("SERVER", "SEND_FAILED", "Không thể gửi lời mời đến " + targetUN), objOP);
        }
    }

    private void handlerInviteResponse(InviteResponse response, ObjectOutputStream objOP) {

        String responderUN = response.getSenderUN();
        String inviterUN = response.getInviterUsername();
        boolean accepted = response.isAccepted();

        // Kiểm tra người phản hồi có online không
        Player responder = mClient.getUserByName(responderUN);
        if (responder == null) {
            OP(new ErrorMessage("SERVER", ErrorMessage.PLAYER_NOT_FOUND, "Người phản hồi không online"), objOP);
            return;
        }

        // Kiểm tra người mời có online không
        Player inviter = mClient.getUserByName(inviterUN);
        if (inviter == null) {
            OP(new ErrorMessage("SERVER", ErrorMessage.PLAYER_NOT_FOUND, "Người mời không còn online"), objOP);
            return;
        }

        if (accepted) {
            // Kiểm tra cả 2 người chơi có còn free không
            if (responder.isBusy() || inviter.isBusy()) {
                OP(new ErrorMessage("SERVER", ErrorMessage.PLAYER_BUSY, "Một trong hai người chơi đã bận"), objOP);
                return;
            }

            // Tạo trận đấu mới
            try {

                // Gửi response xác nhận cho người phản hồi
                OP(new InviteResponse(responderUN, inviterUN, true), objOP);

                // khởi tạo game đấu
                String sessionId = gameSessionManager.createGameSession(inviter, responder);

                System.out.println("🎮 Trận đấu bắt đầu giữa " + inviterUN + " và " + responderUN + " (Session: "
                        + sessionId + ")");

            } catch (Exception e) {
                System.err.println("❌ Lỗi khi tạo trận đấu: " + e.getMessage());
                OP(new ErrorMessage("SERVER", "GAME_CREATION_FAILED", "Không thể tạo trận đấu"), objOP);
            }
        } else {
            // Từ chối lời mời
            System.out.println("❌ " + responderUN + " đã từ chối lời mời từ " + inviterUN);
            OP(new InviteResponse(responderUN, inviterUN, false), objOP);

            // Thông báo cho người mời
            connectionManager.sendMessageToClient(inviterUN,
                    new InviteResponse(inviterUN, responderUN, false));
        }
    }

    /**
     * Cleanup khi client disconnect
     */
    private void cleanupOnDisconnect() {
        if (currentPlayer != null) {
            // Nếu đang trong trận đấu, kết thúc trận đấu
            if (gameSessionManager.isPlayerInGame(currentPlayer.getUsername())) {
                GameSession game = gameSessionManager.getSessionByPlayer(currentPlayer.getUsername());

                if (game != null && !game.isGameEnded()) {
                    // Xử lý người chơi rời trận
                    game.playerLeft(currentPlayer);
                    System.out.println("⚠️ " + currentPlayer.getUsername() + " đã rời game giữa chừng");
                }
            }

            // Hủy đăng ký kết nối
            connectionManager.unregisterClient(currentPlayer.getUsername());

            // Xóa khỏi danh sách online
            mClient.userOffLine(currentPlayer);
            System.out.println("👋 " + currentPlayer.getUsername() + " đã offline");
        }
    }

    private void OP(Message res, ObjectOutputStream obj) {
        try {
            obj.writeObject(res);
            obj.flush();
        } catch (IOException e) {
            System.err.println("❌ Lỗi khi gửi response cho client " + socket + ": " + e.getMessage());
        }
    }

    @Override
    public String toString() {
        return "ClientHandler{" + "socket=" + socket + '}';
    }
}
