package com.oop.game.client.controllers;

import com.oop.game.client.network.GameClient;
import com.oop.game.server.protocol.request.PlayerListRequest;
import com.oop.game.client.utils.AlertUtils;
import com.oop.game.client.utils.FXUtils;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import com.oop.game.server.dto.PlayerInfoDTO;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class LobbyController {

    @FXML
    private Label usernameLabel;
    @FXML
    private Label eloLabel;
    @FXML
    private Label statsLabel;
    @FXML
    private ListView<PlayerInfoDTO> playerListView;
    @FXML
    private Button inviteButton;
    @FXML
    private Button leaderboardButton;
    @FXML
    private Button logoutButton;

    private final ObservableList<PlayerInfoDTO> onlinePlayers = FXCollections.observableArrayList();
    private PlayerInfoDTO currentUser;

    @FXML
    public void initialize() {
        setupPlayerListView();
        updateUserInfoDisplay();
        inviteButton.setDisable(true);
    }

    public void initData(PlayerInfoDTO currentUserInfo) {
        this.currentUser = currentUserInfo;
        updateUserInfoDisplay();

        GameClient.getInstance().setLobbyController(this);
        GameClient.getInstance().sendRequest(new PlayerListRequest(currentUser.getUsername()));
    }

    private void setupPlayerListView() {
        playerListView.setItems(onlinePlayers);
        playerListView.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(PlayerInfoDTO player, boolean empty) {
                super.updateItem(player, empty);
                if (empty || player == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    HBox hbox = new HBox(10);
                    hbox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

                    Text statusIcon = new Text(player.isBusy() ? "🔴" : "🟢");
                    Label nameLabel = new Label(player.getUsername());
                    nameLabel.setFont(Font.font("System", FontWeight.BOLD, 14));
                    Label eloLabel = new Label("ELO: " + player.getElo());
                    Pane spacer = new Pane();
                    HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);
                    Label statusLabel = new Label(player.isBusy() ? "Đang trong trận" : "Sẵn sàng");
                    statusLabel.setTextFill(player.isBusy() ? Color.RED : Color.GREEN);

                    hbox.getChildren().addAll(statusIcon, nameLabel, spacer, eloLabel, statusLabel);
                    setGraphic(hbox);
                }
            }
        });

        playerListView.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> {
                    if (newSelection == null) {
                        inviteButton.setDisable(true);
                    } else {
                        boolean canInvite = !newSelection.isBusy() && !newSelection.getUsername().equals(currentUser.getUsername());
                        inviteButton.setDisable(!canInvite);
                    }
                }
        );
    }

    private void updateUserInfoDisplay() {
        if (currentUser != null) {
            usernameLabel.setText(currentUser.getUsername());
            eloLabel.setText("ELO: " + currentUser.getElo());
            statsLabel.setText(String.format("Thắng: %d / Thua: %d", currentUser.getTotalWins(), currentUser.getTotalLosses()));
        }
    }

    @FXML
    private void onInviteClicked() {
        PlayerInfoDTO target = playerListView.getSelectionModel().getSelectedItem();

        if (target == null) {
            AlertUtils.showWarning("Chưa chọn người chơi", "Vui lòng chọn một người chơi để mời.");
            return;
        }
        if (target.isBusy()) {
            AlertUtils.showWarning("Người chơi đang bận", target.getUsername() + " hiện đang trong trận đấu khác.");
            return;
        }
        if (target.getUsername().equals(currentUser.getUsername())) {
            AlertUtils.showWarning("Không thể tự mời", "Bạn không thể mời chính mình.");
            return;
        }

        try {
            String myUN = currentUser.getUsername();
            String targetUN = target.getUsername();

            com.oop.game.server.protocol.request.InviteRequest invite =
                    new com.oop.game.server.protocol.request.InviteRequest(myUN, targetUN);

            GameClient.getInstance().sendRequest(invite);

            AlertUtils.showInfo("Đã gửi lời mời", "Đã gửi lời mời đấu đến " + target.getUsername() + ".");
        } catch (Exception e) {
            e.printStackTrace();
            AlertUtils.showError("Lỗi gửi lời mời", "Không thể gửi lời mời, vui lòng thử lại.");
        }
    }


    @FXML
    private void onLeaderboardClicked() {
        Stage stage = (Stage) leaderboardButton.getScene().getWindow();
        FXUtils.loadScene(stage, "/com/oop/game/client/views/leaderboard.fxml", "🏆 Bảng Xếp Hạng");
    }

    @FXML
    private void onLogoutClicked() {
        FXUtils.loadScene((javafx.stage.Stage) logoutButton.getScene().getWindow(),
                "/com/oop/game/client/views/login-view.fxml", "🎯 Game Ném Phi Tiêu - Đăng nhập");
    }

    public void updateOnlinePlayersList(List<PlayerInfoDTO> players) {
        Platform.runLater(() -> onlinePlayers.setAll(players));
    }

    public void showInvitationDialog(String inviterUsername) {
        Platform.runLater(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/oop/game/client/views/invite_popup.fxml"));
                Parent root = loader.load();

                Stage popupStage = new Stage();
                InvitePopupController controller = loader.getController();
                controller.setInviter(inviterUsername, popupStage);

                popupStage.initModality(Modality.APPLICATION_MODAL);
                popupStage.setTitle("Lời mời thách đấu!");
                popupStage.setScene(new Scene(root));
                popupStage.setResizable(false);
                popupStage.showAndWait();

            } catch (IOException e) {
                e.printStackTrace();
                AlertUtils.showError("Lỗi giao diện", "Không thể hiển thị popup lời mời.");
            }
        });
    }

    public Stage getStage() {
        if (logoutButton != null && logoutButton.getScene() != null) {
            return (Stage) logoutButton.getScene().getWindow();
        }
        return null;
    }
}