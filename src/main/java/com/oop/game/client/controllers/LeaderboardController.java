package com.oop.game.client.controllers;

import com.oop.game.client.network.GameClient;
import com.oop.game.server.dto.LeaderboardEntryDTO;
import com.oop.game.server.protocol.request.LeaderboardRequest;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

public class LeaderboardController {

    @FXML
    private TableView<LeaderboardEntryDTO> leaderboardTable;
    @FXML
    private TableColumn<LeaderboardEntryDTO, Integer> rankColumn;
    @FXML
    private TableColumn<LeaderboardEntryDTO, String> usernameColumn;
    @FXML
    private TableColumn<LeaderboardEntryDTO, Integer> eloColumn;
    @FXML
    private TableColumn<LeaderboardEntryDTO, Integer> winsColumn;
    @FXML
    private TableColumn<LeaderboardEntryDTO, Integer> lossesColumn;

    private final ObservableList<LeaderboardEntryDTO> leaderboardData = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Liên kết cột bảng với thuộc tính DTO
        rankColumn.setCellValueFactory(new PropertyValueFactory<>("rank"));
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        eloColumn.setCellValueFactory(new PropertyValueFactory<>("elo"));
        winsColumn.setCellValueFactory(new PropertyValueFactory<>("totalWins"));
        lossesColumn.setCellValueFactory(new PropertyValueFactory<>("totalLosses"));

        leaderboardTable.setItems(leaderboardData);

        GameClient client = GameClient.getInstance();
        client.setLeaderboardController(this);
        client.sendRequest(new LeaderboardRequest(client.getUsername()));
    }

    public void updateLeaderboard(List<LeaderboardEntryDTO> entries) {
        Platform.runLater(() -> leaderboardData.setAll(entries));
    }

    @FXML
    private void onBackToLobby() {
        try {
            GameClient client = GameClient.getInstance();
            client.setLeaderboardController(null);

            Stage stage = (Stage) leaderboardTable.getScene().getWindow();

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/oop/game/client/views/lobby.fxml"));
            Parent root = loader.load();

            LobbyController lobbyController = loader.getController();
            lobbyController.initData(client.getCurrentUser());
            client.setLobbyController(lobbyController);

            stage.setScene(new Scene(root));
            stage.setTitle("🎯 Sảnh chờ");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
