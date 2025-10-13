package com.oop.game.client.controllers;

import javafx.application.Platform;
import com.oop.game.client.network.ClientConfig;
import com.oop.game.client.network.GameClient;
import com.oop.game.client.utils.AlertUtils;
import com.oop.game.client.utils.FXUtils;
import com.oop.game.server.protocol.response.LoginResponse;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.scene.control.Label;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;


import java.io.IOException;

public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button loginButton;

    @FXML
    private Label statusLabel;

    @FXML
    private void onLoginClicked(ActionEvent event) {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();

        // 1. Kiểm tra đầu vào cơ bản
        if (username.isEmpty() || password.isEmpty()) {
            AlertUtils.showWarning("Thiếu thông tin", "Vui lòng nhập đầy đủ tên đăng nhập và mật khẩu.");
            return;
        }

        loginButton.setDisable(true);
        loginButton.setText("Đang đăng nhập...");

        new Thread(() -> {
            GameClient client = GameClient.getInstance();
            boolean connected = client.connect(ClientConfig.HOST, ClientConfig.PORT);
            if (!connected) {
                Platform.runLater(() -> {
                    AlertUtils.showError("Lỗi kết nối", "Không thể kết nối đến máy chủ. Vui lòng thử lại sau.");
                    resetLoginButton();
                });
                return;
            }
            LoginResponse response = client.login(username, password);
            Platform.runLater(() -> {
                if (response != null && response.isSuccess()) {
                    // Đăng nhập thành công
                    Stage currentStage = (Stage) loginButton.getScene().getWindow();

                    try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/oop/game/client/views/lobby.fxml"));
                        Parent root = loader.load();
                        LobbyController lobbyController = loader.getController();
                        lobbyController.initData(response.getPlayerInfo());
                        currentStage.setScene(new Scene(root));
                        currentStage.setTitle("🎯 Sảnh chờ");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    String errorMessage = (response != null) ? response.getErrorMessage() : "Đã xảy ra lỗi không xác định.";
                    AlertUtils.showError("Đăng nhập thất bại", errorMessage);
                    resetLoginButton();
                    client.disconnect();
                }
            });
        }).start();
    }

    private void resetLoginButton() {
        loginButton.setDisable(false);
        loginButton.setText("Đăng nhập");
    }
}