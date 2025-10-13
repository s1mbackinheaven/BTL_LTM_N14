package com.oop.game.client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;

public class ViewPreviewer extends Application {

    private final Map<String, String> viewMap = new LinkedHashMap<>() {{
        put("Login", "/com/oop/game/client/views/login.fxml");
        put("Lobby", "/com/oop/game/client/views/lobby.fxml");
        put("Result", "/com/oop/game/client/views/result.fxml");
        put("Invite Popup", "/com/oop/game/client/views/invite_popup.fxml");
        put("game", "/com/oop/game/client/views/game.fxml");
        put("leaderboard", "/com/oop/game/client/views/leaderboard.fxml");
    }};

    @Override
    public void start(Stage stage) {
        BorderPane root = new BorderPane();
        ComboBox<String> comboBox = new ComboBox<>();
        comboBox.getItems().addAll(viewMap.keySet());
        comboBox.setPromptText("🔍 Chọn giao diện để xem");

        comboBox.setOnAction(e -> {
            String selected = comboBox.getValue();
            if (selected != null) {
                loadFXMLIntoRoot(root, viewMap.get(selected));
            }
        });

        root.setTop(comboBox);
        Scene scene = new Scene(root, 600, 400);
        stage.setTitle("🧩 JavaFX UI Previewer");
        stage.setScene(scene);
        stage.show();
    }

    private void loadFXMLIntoRoot(BorderPane root, String fxmlPath) {
        try {
            URL resource = getClass().getResource(fxmlPath);
            if (resource == null) {
                System.err.println("⚠️ Không tìm thấy file: " + fxmlPath);
                return;
            }
            FXMLLoader loader = new FXMLLoader(resource);
            root.setCenter(loader.load());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
