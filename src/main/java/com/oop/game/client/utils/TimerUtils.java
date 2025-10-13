package com.oop.game.client.utils;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.util.Duration;

public class TimerUtils {
    public static void startCountdown(Label label, int seconds, Runnable onTimeout) {
        final int[] time = {seconds};
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            time[0]--;
            Platform.runLater(() -> label.setText(time[0] + "s"));
            if (time[0] <= 0) {
                onTimeout.run();
            }
        }));
        timeline.setCycleCount(seconds);
        timeline.play();
    }
}
