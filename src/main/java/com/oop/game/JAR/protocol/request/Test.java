package com.oop.game.JAR.protocol.request;

import com.oop.game.JAR.protocol.Message;

public class Test extends Message {
    String mes;

    public Test(String msg) {
        super();
        this.mes = msg;
    }

    public String getMsg() {
        return this.mes;
    }
}
