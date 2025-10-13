package com.oop.game.client.controllers;

import com.oop.game.client.network.GameClient;
import com.oop.game.server.protocol.GameEnd;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;

public class ResultController {

    @FXML
    private Label resultTitleLabel;
    @FXML
    private Label reasonLabel;
    @FXML
    private Label scoreLabel;
    @FXML
    private Label eloLabel;

    public void initData(GameEnd gameEndResult, String myUsername) {
        String winner = gameEndResult.getWinner();

        if (winner == null) {
            // Trường hợp hòa
            resultTitleLabel.setText("HÒA!");
            resultTitleLabel.setStyle("-fx-text-fill: #FFC107;");
            eloLabel.setText("Không thay đổi ELO");
        } else if (winner.equals(myUsername)) {
            // Trường hợp thắng
            resultTitleLabel.setText("🏆 BẠN THẮNG! 🏆");
            resultTitleLabel.setStyle("-fx-text-fill: #4CAF50;");
            eloLabel.setText("ELO: +" + gameEndResult.getEloChangeWinner());
        } else {
            resultTitleLabel.setText("BẠN THUA");
            resultTitleLabel.setStyle("-fx-text-fill: #D32F2F;");
            eloLabel.setText("ELO: " + gameEndResult.getEloChangeLoser());
        }

        reasonLabel.setText("Lý do: " + gameEndResult.getReason());
        scoreLabel.setText(gameEndResult.getFinalScoreWinner() + " - " + gameEndResult.getFinalScoreLoser());
    }

    @FXML
    private void onReturnToLobby() {
        try {
            Stage stage = (Stage) resultTitleLabel.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/oop/game/client/views/lobby.fxml"));
            Parent root = loader.load();
            LobbyController lobbyController = loader.getController();
            lobbyController.initData(GameClient.getInstance().getCurrentUser());
            stage.setScene(new Scene(root));
            stage.setTitle("🎯 Sảnh chờ");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}