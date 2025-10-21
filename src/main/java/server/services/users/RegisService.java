package server.services.users;

import server.DAO.UserDAO;
import server.enums.regisStatus;
import server.utils.Hashing;

import java.sql.Connection;

public class RegisService {
    private UserDAO userDAO;

    public RegisService(Connection con) {
        this.userDAO = new UserDAO(con);
    }

    public regisStatus regis(String username, String password) {
        if (username == null || username.isBlank() || password == null || password.isBlank()) {
            return regisStatus.INVALID_INPUT;
        }

        if (userExist(username))
            return regisStatus.USERNAME_EXITS;

        String hashPW = Hashing.hashPassword(password);

        return userDAO.createUser(username, hashPW) ? regisStatus.SUCCESS : regisStatus.ERROR;
    }


    private boolean userExist(String username) {
        return userDAO.userExists(username);
    }
}
