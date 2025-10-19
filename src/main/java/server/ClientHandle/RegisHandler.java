package server.ClientHandle;

import com.oop.game.JAR.protocol.request.RegisRequest;
import server.DAO.UserDAO;

import java.io.ObjectOutputStream;

public class RegisHandler extends Handler {

    private RegisRequest msg;
    private UserDAO userDao;

    public RegisHandler(Object msg, ObjectOutputStream obj) {
        super(msg, obj);
        this.msg = (RegisRequest) msg;
        this.userDao = new UserDAO();
    }

    private void regis() {

        String un = msg.getUsername();
        String pw = msg.getPassword();

        userDao.registerUser(un, pw);


    }


}
