package com.oop.game.client.models;

public enum PowerUp {
    DOUBLE_POINTS("Nhân đôi điểm"),
    HALF_ENEMY("Trừ nửa điểm đối thủ"),
    REVEAL_ZONES("Hiện 3/5 vùng màu"),
    SWAP_ZONES("Đổi 2 vùng màu"),
    EXTRA_THROW("Ném thêm 1 lần"),
    ZERO_FORCE("Lực đẩy = 0");

    private final String label;

    PowerUp(String label) { this.label = label; }

    public String getLabel() { return label; }

    public static String[] names() {
        PowerUp[] list = values();
        String[] arr = new String[list.length];
        for (int i = 0; i < list.length; i++) arr[i] = list[i].getLabel();
        return arr;
    }
}
