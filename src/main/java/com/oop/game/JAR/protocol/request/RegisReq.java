package com.oop.game.JAR.protocol.request;

import com.oop.game.JAR.enums.MessageType;
import com.oop.game.JAR.protocol.Message;

public class RegisterReq extends Message {
    private String username;
    private String password;

    public RegisterReq(String username, String password) {
        super(MessageType.REGIS_REQUEST, username);
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
