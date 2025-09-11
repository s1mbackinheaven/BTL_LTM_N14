package com.oop.game.server;

import com.oop.game.server.core.Player;
import com.oop.game.server.db.UserDAO;
import com.oop.game.server.managers.ClientManager;
import com.oop.game.server.models.User;
import com.oop.game.server.protocol.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.Runnable;
import java.net.Socket;

public class ClientHandler implements Runnable {
    private Socket socket;

    public ClientHandler(Socket _socket) {
        this.socket = _socket;
    }

    public void run() {
        try (ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream input = new ObjectInputStream(socket.getInputStream());) {

            // nhận req
            while (true) {
                try {
                    Object msg = input.readObject();

                    handlerMes(msg, output);
                    // xử lý req

                } catch (ClassNotFoundException e) {
                    System.err.println("Class not found");
                } catch (IOException e) {
                    System.err.println("IO error");
                    break;
                }
            }

        } catch (IOException e) {
            System.err.println("IO error");
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void handlerMes(Object obj, ObjectOutputStream objOP) {
        if (obj instanceof LoginRequest)
            handlerLoginReq((LoginRequest) obj, objOP);
        else if (obj instanceof MoveRequest)
            handlerMoveReq((MoveRequest) obj, objOP);
        else if (obj instanceof InviteRequest)
            handlerInviteReq((InviteRequest) obj, objOP);
        else if (obj instanceof PlayerListRequest)
            handlerPlayerListReq((PlayerListRequest) obj, objOP);
    }

    private void handlerLoginReq(LoginRequest req, ObjectOutputStream objOP) {

        UserDAO ud = new UserDAO();

        String un = req.getUsername();
        String pw = req.getPassword();

        if (un == null || pw == null) {

            LoginResponse res = new LoginResponse(false, "vui lòng ghi đủ thông tin", null);

            try {

                objOP.writeObject(res);
                objOP.flush();

                return;

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // check user
        boolean isUser = ud.authenticateUser(un, pw);
        ClientManager mClient = ClientManager.getInstance();

        if (isUser) {

            Player player = new Player(un);

            if (mClient.isOnline(player)) {
                LoginResponse res = new LoginResponse(false, "đã được đăng nhập ở nơi khác", null);

                try {

                    objOP.writeObject(res);
                    objOP.flush();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {

                User u = ud.getUserByUsername(un);
                Player player = new Player(u);

                LoginResponse res = new LoginResponse(true, "Login suscess", oPlayer);
            }
        } else {

        }

    }

    private void handlerMoveReq(MoveRequest req, ObjectOutputStream objOP) {
    }

    private void handlerPlayerListReq(PlayerListRequest obj, ObjectOutputStream objOP) {
    }

    private void handlerInviteReq(InviteRequest obj, ObjectOutputStream objOP) {
    }

    public String toString() {
        return socket.toString();
    }
}
