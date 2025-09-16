module com.oop.game {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires java.desktop;
    requires jbcrypt;

    opens com.oop.game to javafx.fxml;

    exports com.oop.game;
}