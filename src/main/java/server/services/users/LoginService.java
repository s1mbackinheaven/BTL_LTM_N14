package server.services.users;

import server.DAO.UserDAO;
import server.enums.loginStatus;
import server.entities.User;
import server.utils.Hashing;
import server.utils.LoginResult;

import java.sql.Connection;

public class LoginService {
    private UserDAO userDAO;

    public LoginService(Connection con) {
        userDAO = new UserDAO(con);
    }

    public LoginResult login(String username, String password) {
        if (username == null || username.isBlank() || password == null || password.isBlank()) {
            return new LoginResult(loginStatus.INVALID_INPUT);
        }

        User user = userDAO.getUserByUsername(username);

        if (user == null)
            return new LoginResult(loginStatus.USERNAME_NOT_EXITS);

        if (Hashing.checkPassword(password, user.getPassword()))
            return new LoginResult(loginStatus.SUCCESS, user);

        return new LoginResult(loginStatus.ERROR);
    }
}
