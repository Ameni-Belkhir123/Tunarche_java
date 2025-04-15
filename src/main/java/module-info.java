module com.example.tunarche {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires java.desktop;

    opens TunArche.test to javafx.graphics;
    opens TunArche.controllers to javafx.fxml; // ✅ autoriser le chargement FXML du contrôleur

    opens TunArche.entities to javafx.base;
    exports TunArche.controllers;


    opens com.example.tunarche to javafx.fxml;
    exports TunArche.test;
}