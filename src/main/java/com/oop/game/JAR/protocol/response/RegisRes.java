package com.oop.game.JAR.protocol.response;

import com.oop.game.JAR.enums.MessageType;
import com.oop.game.JAR.protocol.Message;

public class RegisRes extends Message {
    private boolean success;
    private String errorMessage;

    public RegisRes(boolean success, String errorMessage) {
        super(MessageType.REGIS_RESPONSE, "SYSTEM");
        this.success = success;
        this.errorMessage = errorMessage;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
