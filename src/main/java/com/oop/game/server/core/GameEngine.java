package com.oop.game.server.core;

import java.util.Random;

import com.oop.game.server.enums.Color;
import com.oop.game.server.enums.PowerUp;

/**
 * Engine xử lý logic 1 lượt ném phi tiêu
 * Stateless - chỉ nhận input và trả output, không lưu trạng thái
 */
public class GameEngine {
    private static final Random random = new Random();

    /**
     * Xử lý 1 lượt ném hoàn chỉnh
     *
     * @param playerX        tọa độ X người chơi nhập
     * @param playerY        tọa độ Y người chơi nhập
     * @param force          lực đẩy f (đã được random từ -3 đến 3)
     * @param colorBoard     bảng màu hiện tại
     * @param activePowerUps phụ trợ đang kích hoạt
     * @param isOpponentTurn có phải lượt của đối thủ không
     * @return kết quả lượt ném
     */
    public static ThrowResult processThrow(int playerX, int playerY, int force,
                                           ColorBoard colorBoard,
                                           PowerUp[] activePowerUps,
                                           boolean isOpponentTurn) {

        // Áp dụng phụ trợ ZERO_FORCE nếu có
        int actualForce = force;
        for (PowerUp powerUp : activePowerUps) {
            if (powerUp != null && powerUp.affectsForce()) {
                actualForce = 0;
                break;
            }
        }

        // Tính tọa độ cuối theo công thức
        int finalX = calculateFinalX(playerX, actualForce);
        int finalY = calculateFinalY(playerY, playerX, actualForce);

        // Xác định màu trúng và điểm gốc
        Color hitColor = colorBoard.getColorAt(finalX, finalY);
        int baseScore = hitColor != null ? hitColor.getScore() : 0;

        // Áp dụng phụ trợ ảnh hưởng điểm số
        int finalScore = baseScore;
        for (PowerUp powerUp : activePowerUps) {
            if (powerUp != null) {
                finalScore = powerUp.applyScoreEffect(finalScore, isOpponentTurn);
            }
        }

        // Kiểm tra có thêm lượt không
        boolean hasExtraTurn = false;
        for (PowerUp powerUp : activePowerUps) {
            if (powerUp != null && powerUp.grantsExtraTurn()) {
                hasExtraTurn = true;
                break;
            }
        }

        return new ThrowResult(finalX, finalY, hitColor, baseScore, finalScore, hasExtraTurn);
    }

    /**
     * Tính Xfinal theo công thức: Xfinal = X + f
     */
    public static int calculateFinalX(int playerX, int force) {
        return playerX + force;
    }

    /**
     * Tính Yfinal theo công thức: Yfinal = Y + sign(X) * floor(f/2)
     * sign(X) = -1 nếu X < 0, sign(X) = 1 nếu X >= 0
     */
    public static int calculateFinalY(int playerY, int playerX, int force) {
        int sign = playerX < 0 ? -1 : 1;
        return playerY + sign * (force / 2); // floor tự động với int division
    }

    /**
     * Random lực đẩy từ -3 đến 3
     */
    public static int generateRandomForce() {
        return random.nextInt(7) - 3; // 0-6 rồi trừ 3 = -3 đến 3
    }

    /**
     * Random 3 phụ trợ từ 6 phụ trợ có sẵn
     */
    public static PowerUp[] generateRandomPowerUps() {
        PowerUp[] allPowerUps = PowerUp.values();
        PowerUp[] selected = new PowerUp[3];

        // Shuffle và lấy 3 cái đầu
        PowerUp[] shuffled = allPowerUps.clone();
        for (int i = shuffled.length - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            PowerUp temp = shuffled[i];
            shuffled[i] = shuffled[j];
            shuffled[j] = temp;
        }

        System.arraycopy(shuffled, 0, selected, 0, 3);
        return selected;
    }

    /**
     * Class lưu kết quả 1 lượt ném
     */
    public static class ThrowResult {
        public final int finalX, finalY;
        public final Color hitColor;
        public final int baseScore;
        public final int finalScore;
        public final boolean hasExtraTurn;

        public ThrowResult(int finalX, int finalY, Color hitColor,
                           int baseScore, int finalScore, boolean hasExtraTurn) {
            this.finalX = finalX;
            this.finalY = finalY;
            this.hitColor = hitColor;
            this.baseScore = baseScore;
            this.finalScore = finalScore;
            this.hasExtraTurn = hasExtraTurn;
        }
    }
}