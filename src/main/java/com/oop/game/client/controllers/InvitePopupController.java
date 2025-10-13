package com.oop.game.client.controllers;

import com.oop.game.client.network.GameClient;
import com.oop.game.server.protocol.response.InviteResponse;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class InvitePopupController {

    @FXML
    private Label inviteMessageLabel;

    private String inviterUsername;
    private Stage stage;

    public void setInviter(String inviterUsername, Stage stage) {
        this.inviterUsername = inviterUsername;
        this.stage = stage;
        inviteMessageLabel.setText(inviterUsername + " muốn thách đấu với bạn!");
    }

    @FXML
    private void onAcceptClicked(ActionEvent event) {
        String myUsername = GameClient.getInstance().getUsername();
        com.oop.game.server.protocol.response.InviteResponse response =
                new com.oop.game.server.protocol.response.InviteResponse(myUsername, inviterUsername, true);

        GameClient.getInstance().sendRequest(response);

        if (stage != null) {
            stage.close();
        }
    }

    @FXML
    private void onDeclineClicked(ActionEvent event) {
        String myUsername = GameClient.getInstance().getUsername();

        com.oop.game.server.protocol.response.InviteResponse response =
                new com.oop.game.server.protocol.response.InviteResponse(myUsername, inviterUsername, false);

        GameClient.getInstance().sendRequest(response);

        if (stage != null) {
            stage.close();
        }
    }
}