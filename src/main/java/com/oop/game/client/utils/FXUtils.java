package com.oop.game.client.utils;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class FXUtils {
    public static void loadScene(Stage stage, String fxmlResourcePath, String title) {
        try {
            URL resource = FXUtils.class.getResource(fxmlResourcePath);
            if (resource == null) {
                System.err.println("FXML resource not found: " + fxmlResourcePath);
                return;
            }
            FXMLLoader loader = new FXMLLoader(resource);
            Scene scene = new Scene(loader.load());
            // load css if exists
            URL css = FXUtils.class.getResource("/com/oop/game/client/assets/styles.css");
            if (css != null) {
                scene.getStylesheets().add(css.toExternalForm());
            }
            stage.setTitle(title);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
