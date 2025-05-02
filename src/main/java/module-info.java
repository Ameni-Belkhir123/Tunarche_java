module org.example.oeuvre {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires java.sql;
    requires jakarta.mail;

    opens com.example.oeuvre.Controllers to javafx.fxml;
    opens com.example.oeuvre to javafx.fxml, javafx.graphics;
    opens com.example.oeuvre.Entities to javafx.base;

    exports com.example.oeuvre;
}
