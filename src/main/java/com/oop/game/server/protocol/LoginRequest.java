package com.oop.game.server.protocol;

/**
 * Request đăng nhập từ client
 */
public class LoginRequest extends Message {
    private String username;
    private String password;

    public LoginRequest(String username, String password) {
        super(MessageType.LOGIN_REQUEST, username);
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
