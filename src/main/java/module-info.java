module com.oop.game {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.oop.game to javafx.fxml;
    exports com.oop.game;
}