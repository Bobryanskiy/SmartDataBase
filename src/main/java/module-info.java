module com.github.bobryanskiy.smartdb {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires eu.hansolo.tilesfx;
    requires java.sql;
    requires java.desktop;

    opens com.github.bobryanskiy.smartdb to javafx.fxml;
    exports com.github.bobryanskiy.smartdb;
}