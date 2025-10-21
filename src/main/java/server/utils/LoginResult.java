package server.utils;

import server.entities.User;
import server.enums.loginStatus;

public class LoginResult {
    public final loginStatus status;
    public final User user;

    public LoginResult(loginStatus status, User user) {
        this.status = status;
        this.user = user;
    }

    public LoginResult(loginStatus status) {
        this(status, null);
    }
}
