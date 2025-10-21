package server.controllers;

import java.io.ObjectOutputStream;
import java.sql.Connection;

import com.oop.game.JAR.protocol.request.LoginRequest;
import com.oop.game.JAR.protocol.request.RegisRequest;
import com.oop.game.JAR.protocol.response.LoginResponse;
import com.oop.game.JAR.protocol.response.RegisRespone;

import server.enums.loginStatus;
import server.enums.regisStatus;
import server.services.users.LoginService;
import server.services.users.RegisService;
import server.utils.LoginResult;

public class UserController {

    private final Connection con;
    private LoginService loginService;
    private RegisService regisService;

    public UserController(Connection con) {
        this.con = con;
        loginService = new LoginService(this.con);
        regisService = new RegisService(this.con);
    }

    public RegisRespone regis(Object msg) {

        RegisRequest req = (RegisRequest) msg;

        String un = req.getUsername();
        String pw = req.getPassword();


        regisStatus status = regisService.regis(un, pw);
        RegisRespone res = null;

        if (status == regisStatus.SUCCESS) {
            res = new RegisRespone(200, true, "Đăng ký thành công");
        } else if (status == regisStatus.USERNAME_EXITS) {
            res = new RegisRespone(400, false, "Tên đăng nhập đã tồn tại!");
        } else {
            res = new RegisRespone(500, false, "Lỗi server, vui lòng thử lại sau!");
        }

        return res;
    }

    public LoginResponse login(Object msg) {
        LoginRequest req = (LoginRequest) msg;

        String un = req.getUsername();
        String pw = req.getPassword();

        LoginResult result = loginService.login(un, pw);

        return buildLoginRes(result);
    }

    private LoginResponse buildLoginRes(LoginResult result) {

        LoginResponse res = null;
        loginStatus sts = result.status;

        switch (sts) {
            case ERROR -> res = new LoginResponse(500, false, "Có lỗi xảy ra, vui lòng thử lại");
            case USERNAME_NOT_EXITS -> res = new LoginResponse(404, false, "Không tìm thấy tên đưang nhập");
            case INCORRECT -> res = new LoginResponse(404, false, "Mật khẩu không đúng");
            case SUCCESS -> {
                res = new LoginResponse(200, true, "đăng nhập thành công");

            }
        }

        return res;
    }

}
