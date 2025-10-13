package com.oop.game.client.network;

import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

import com.oop.game.client.controllers.GameController;
import com.oop.game.client.controllers.LeaderboardController;
import com.oop.game.client.controllers.LobbyController;
import com.oop.game.client.utils.AlertUtils;
import com.oop.game.server.dto.PlayerGameStateDTO;
import com.oop.game.server.dto.PlayerInfoDTO;
import com.oop.game.server.protocol.GameStart;
import com.oop.game.server.protocol.LeaderboardData;
import com.oop.game.server.protocol.request.InviteRequest;
import com.oop.game.server.protocol.request.LoginRequest;
import com.oop.game.server.protocol.ErrorMessage;
import com.oop.game.server.protocol.GameStateUpdate;
import com.oop.game.server.protocol.response.LoginResponse;
import com.oop.game.server.protocol.response.PlayerListResponse;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;

public class GameClient {
    private static GameClient instance;

    private Socket socket;
    private ObjectOutputStream objectOut;
    private ObjectInputStream objectIn;

    private PlayerInfoDTO currentUser;
    private LobbyController lobbyController;
    private LeaderboardController leaderboardController;
    private GameController gameController;

    private GameClient() {
    }

    public static synchronized GameClient getInstance() {
        if (instance == null) {
            instance = new GameClient();
        }
        return instance;
    }

    public boolean connect(String host, int port) {
        try {
            if (socket != null && !socket.isClosed()) return true;
            socket = new Socket(host, port);
            objectOut = new ObjectOutputStream(socket.getOutputStream());
            objectIn = new ObjectInputStream(socket.getInputStream());
            return true;
        } catch (IOException e) {
            System.err.println("Connection failed: " + e.getMessage());
            return false;
        }
    }

    public LoginResponse login(String username, String password) {
        try {
            sendRequest(new LoginRequest(username, password));
            Object response = objectIn.readObject();
            if (response instanceof LoginResponse) {
                LoginResponse loginResponse = (LoginResponse) response;
                if (loginResponse.isSuccess()) {
                    this.currentUser = loginResponse.getPlayerInfo();
                    startListenerThread();
                }
                return loginResponse;
            }
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Login error: " + e.getMessage());
        }
        return null;
    }

    private void startListenerThread() {
        Thread listenerThread = new Thread(() -> {
            try {
                while (socket != null && !socket.isClosed()) {
                    Object receivedObject = objectIn.readObject();
                    handleServerMessage(receivedObject);
                }
            } catch (SocketException e) {
                System.out.println("Disconnected from server.");
            } catch (IOException | ClassNotFoundException e) {
                System.err.println("Error listening to server: " + e.getMessage());
            } finally {
                disconnect();
            }
        }, "GameClient-Listener");
        listenerThread.setDaemon(true);
        listenerThread.start();
    }

    private void handleServerMessage(Object message) {
        if (message instanceof PlayerListResponse) {
            PlayerListResponse response = (PlayerListResponse) message;
            if (lobbyController != null) {
                Platform.runLater(() -> lobbyController.updateOnlinePlayersList(response.getPlayers()));
            }
        } else if (message instanceof GameStart) {
            GameStart gameStart = (GameStart) message;
            String myUsername = getUsername();
            String opponentUsername = gameStart.getOpponent();
            boolean amIFirst = gameStart.isFirstPlayer();
            List<String> myPowerUpNames = new ArrayList<>();
            if (gameStart.getMyPowerUps() != null) {
                myPowerUpNames = gameStart.getMyPowerUps()
                        .stream()
                        .map(Enum::name)
                        .collect(Collectors.toList());
            }
            PlayerGameStateDTO myState = new PlayerGameStateDTO(myUsername, 0, myPowerUpNames, amIFirst);
            PlayerGameStateDTO opponentState = new PlayerGameStateDTO(opponentUsername, 0, new ArrayList<>(), !amIFirst);
            GameStateUpdate initialUpdate = amIFirst
                    ? new GameStateUpdate("server", myState, opponentState, null)
                    : new GameStateUpdate("server", opponentState, myState, null);
            Platform.runLater(() -> switchToGameScene(initialUpdate));
        } else if (message instanceof GameStateUpdate) {
            GameStateUpdate update = (GameStateUpdate) message;
            if (gameController != null) {
                Platform.runLater(() -> gameController.updateGameState(update));
            }
        } else if (message instanceof InviteRequest) {
            InviteRequest invite = (InviteRequest) message;
            String inviterName = invite.getSenderUN(); // ✅ đúng field theo server
            if (lobbyController != null) {
                Platform.runLater(() -> lobbyController.showInvitationDialog(inviterName));
            }
        } else if (message instanceof LeaderboardData) {
            LeaderboardData data = (LeaderboardData) message;
            if (leaderboardController != null) {
                leaderboardController.updateLeaderboard(data.getEntries());
            }
        } else if (message instanceof ErrorMessage) {
            ErrorMessage error = (ErrorMessage) message;
            Platform.runLater(() -> AlertUtils.showError("Lỗi từ Server", error.getErrorMessage()));
        }
    }

    private void switchToGameScene(GameStateUpdate initialUpdate) {
        try {
            if (lobbyController == null) return;
            Stage currentStage = lobbyController.getStage();
            if (currentStage == null) return;

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/oop/game/client/views/game.fxml"));
            Parent root = loader.load();

            GameController newGameController = loader.getController();
            newGameController.initData(initialUpdate);

            this.lobbyController = null;
            this.gameController = newGameController;

            currentStage.setScene(new Scene(root));
            currentStage.setTitle("🎯 Trận Đấu Bắt Đầu!");

        } catch (IOException e) {
            e.printStackTrace();
            AlertUtils.showError("Lỗi tải giao diện", "Không thể bắt đầu trận đấu.");
        }
    }

    public void sendRequest(Object request) {
        if (objectOut != null) {
            try {
                objectOut.writeObject(request);
                objectOut.flush();
            } catch (IOException e) {
                System.err.println("Failed to send request: " + e.getMessage());
                disconnect();
            }
        }
    }

    public void disconnect() {
        try {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        } catch (IOException e) {
            System.err.println("Error while disconnecting: " + e.getMessage());
        } finally {
            objectIn = null;
            objectOut = null;
            socket = null;
            currentUser = null;
        }
    }

    public String getUsername() {
        return (currentUser != null) ? currentUser.getUsername() : null;
    }

    public PlayerInfoDTO getCurrentUser() {
        return currentUser;
    }

    public void setLobbyController(LobbyController controller) {
        this.lobbyController = controller;
    }

    public void setGameController(GameController controller) {
        this.gameController = controller;
    }

    public void setLeaderboardController(LeaderboardController controller) {
        this.leaderboardController = controller;
    }
}