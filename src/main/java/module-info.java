module com.oop.game {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires java.desktop;
    requires jbcrypt;

    opens com.oop.game to javafx.fxml;
    opens com.oop.game.client to javafx.fxml;
    opens com.oop.game.client.controllers to javafx.fxml;
    opens com.oop.game.client.utils to javafx.fxml;
    opens com.oop.game.client.models to javafx.fxml;

    exports com.oop.game;
    exports com.oop.game.client;
}
