package com.oop.game;

import com.oop.game.server.Config;
import com.oop.game.server.db.DAO;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 320, 240);
        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        System.out.println("Starting HelloApplication...");

        // Test database connection (optional)
        try {
            Connection con = DAO.getConnection();
            System.out.println("Database connected successfully!");
            con.close();
        } catch (Exception e) {
            System.err.println("Database connection failed: " + e.getMessage());
        }

        // Launch JavaFX application
        launch(args);
    }
}