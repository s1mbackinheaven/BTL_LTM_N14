package com.oop.game.server;

import com.oop.game.server.protocol.InviteRequest;
import com.oop.game.server.protocol.LoginRequest;
import com.oop.game.server.protocol.MoveRequest;
import com.oop.game.server.protocol.PlayerListRequest;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
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
