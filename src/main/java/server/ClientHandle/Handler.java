package server.ClientHandle;

import java.io.ObjectOutputStream;

public class Handler {
    private ObjectOutputStream obj;

    public Handler(Object msg, ObjectOutputStream obj) {
        this.obj = obj;
    }
}
