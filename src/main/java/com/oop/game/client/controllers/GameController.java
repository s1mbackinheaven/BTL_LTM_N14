package com.oop.game.client.controllers;

import com.oop.game.client.network.GameClient;
import com.oop.game.client.utils.AlertUtils;
import com.oop.game.server.dto.PlayerGameStateDTO;
import com.oop.game.server.protocol.GameEnd;
import com.oop.game.server.protocol.GameStateUpdate;
import com.oop.game.server.protocol.request.MoveRequest;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import java.util.Timer;
import java.util.TimerTask;

public class GameController {

    @FXML
    private Label playerLabel, opponentLabel, playerScore, opponentScore, timerLabel, turnIndicatorLabel;
    @FXML
    private TextField xField, yField;
    @FXML
    private Button throwButton;
    @FXML
    private HBox playerPowerUpsBox, opponentPowerUpsBox;
    @FXML
    private VBox controlsBox;
    @FXML
    private Canvas dartboardCanvas;

    @FXML
    private void initialize() {
        drawDartboard();
    }

    private String myUsername;
    private String opponentUsername;
    private Timer countdownTimer;

    public void initData(GameStateUpdate initialUpdate) {
        this.myUsername = GameClient.getInstance().getUsername();
        GameClient.getInstance().setGameController(this);
        updateGameState(initialUpdate);
        drawDartboard();
    }

    public void updateGameState(GameStateUpdate update) {
        Platform.runLater(() -> {
            PlayerGameStateDTO myState = update.getPlayer1State().getUsername().equals(myUsername)
                    ? update.getPlayer1State() : update.getPlayer2State();
            PlayerGameStateDTO opponentState = update.getPlayer1State().getUsername().equals(myUsername)
                    ? update.getPlayer2State() : update.getPlayer1State();

            this.opponentUsername = opponentState.getUsername();

            playerLabel.setText(myState.getUsername() + " (Bạn)");
            opponentLabel.setText(opponentState.getUsername());
            playerScore.setText(String.valueOf(myState.getCurrentScore()));
            opponentScore.setText(String.valueOf(opponentState.getCurrentScore()));

            if (myState.isMyTurn()) {
                turnIndicatorLabel.setText("🎯 ĐẾN LƯỢT BẠN!");
                controlsBox.setDisable(false);
                startCountdown();
            } else {
                turnIndicatorLabel.setText("⏳ Đợi " + opponentUsername + "...");
                controlsBox.setDisable(true);
                stopCountdown();
                timerLabel.setText("--");
            }
        });
    }

    @FXML
    private void onThrowClicked() {
        try {
            int x = Integer.parseInt(xField.getText().trim());
            int y = Integer.parseInt(yField.getText().trim());

            MoveRequest moveRequest = new MoveRequest(myUsername, x, y, null);
            GameClient.getInstance().sendRequest(moveRequest);

            controlsBox.setDisable(true);
            stopCountdown();
            turnIndicatorLabel.setText("🎯 Đang chờ kết quả...");

            simulateThrow(x, y);

        } catch (NumberFormatException e) {
            AlertUtils.showError("Dữ liệu không hợp lệ", "Tọa độ X và Y phải là số nguyên.");
        }
    }

    private void drawDartboard() {
        if (dartboardCanvas == null) return;

        GraphicsContext gc = dartboardCanvas.getGraphicsContext2D();
        double w = dartboardCanvas.getWidth();
        double h = dartboardCanvas.getHeight();
        double cx = w / 2;
        double cy = h / 2;

        double[] radii = {175, 140, 110, 80, 50};
        Color[] colors = {
                Color.WHITE,       // 1
                Color.web("#2196F3"),   // 2 Blue
                Color.web("#9C27B0"),   // 3 Purple
                Color.web("#FFEB3B"),   // 4 Yellow
                Color.web("#F44336")    // 5 Red
        };

        gc.clearRect(0, 0, w, h);

        for (int i = 0; i < colors.length; i++) {
            gc.setFill(colors[i]);
            double r = radii[i];
            gc.fillOval(cx - r, cy - r, r * 2, r * 2);
        }

        gc.setFill(Color.BLACK);
        gc.fillOval(cx - 5, cy - 5, 10, 10);
    }

    private void simulateThrow(int x, int y) {
        if (dartboardCanvas == null) return;
        GraphicsContext gc = dartboardCanvas.getGraphicsContext2D();
        double cx = dartboardCanvas.getWidth() / 2;
        double cy = dartboardCanvas.getHeight() / 2;

        drawDartboard();

        double scale = 10; // đơn vị phóng to để dễ nhìn
        double px = cx + x * scale;
        double py = cy - y * scale;

        gc.setFill(Color.BLACK);
        gc.fillOval(px - 4, py - 4, 8, 8);
    }

    public void showGameResult(GameEnd gameEndMessage) {
        Platform.runLater(() -> {
            stopCountdown();
            String resultText = "Trận đấu kết thúc!\n";
            resultText += "Lý do: " + gameEndMessage.getReason() + "\n";
            if (gameEndMessage.getWinner() != null) {
                if (gameEndMessage.getWinner().equals(myUsername)) {
                    resultText += "🏆 Chúc mừng, bạn đã chiến thắng!";
                } else {
                    resultText += "😢 Bạn đã thua. Chúc may mắn lần sau!";
                }
            } else {
                resultText += "🤝 Kết quả hòa!";
            }
            AlertUtils.showInfo("Kết quả trận đấu", resultText);
        });
    }

    private void startCountdown() {
        stopCountdown();
        countdownTimer = new Timer();
        final int[] timeLeft = {15};
        timerLabel.setText(String.valueOf(timeLeft[0]));

        countdownTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                timeLeft[0]--;
                Platform.runLater(() -> timerLabel.setText(String.valueOf(timeLeft[0])));

                if (timeLeft[0] <= 0) {
                    Platform.runLater(() -> {
                        xField.setText("0");
                        yField.setText("0");
                        onThrowClicked();
                    });
                    stopCountdown();
                }
            }
        }, 1000, 1000);
    }

    private void stopCountdown() {
        if (countdownTimer != null) {
            countdownTimer.cancel();
            countdownTimer = null;
        }
    }
}
