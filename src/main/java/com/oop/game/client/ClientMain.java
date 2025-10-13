package com.oop.game.client;

import com.oop.game.client.utils.FXUtils;
import javafx.application.Application;
import javafx.stage.Stage;

public class ClientMain extends Application {
    @Override
    public void start(Stage stage) {
        FXUtils.loadScene(stage, "/com/oop/game/client/views/login.fxml", "🎯 Game Ném Phi Tiêu - Đăng nhập");
        stage.setResizable(false);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
