module com.example.tunarche {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires javafx.media;
    requires java.sql;
    requires jbcrypt;
    requires java.mail;
    requires java.desktop; // Required for webcam-capture
    requires java.base;
    requires kernel;
    requires layout;
    requires barcodes;
    requires org.controlsfx.controls;
    requires twilio;
    requires org.json;
    requires webcam.capture;
    requires com.github.librepdf.openpdf;
    requires openhtmltopdf.pdfbox;
    requires okhttp3;
    requires org.apache.httpcomponents.httpcore;
    requires org.apache.httpcomponents.httpclient;
    requires com.google.zxing;


    opens TunArche.test to javafx.graphics;
    opens TunArche.controllers to javafx.fxml;
    opens com.example.tunarche to javafx.fxml;

    exports TunArche.entities;
    exports TunArche.test;
    exports TunArche.controllers;
    exports TunArche.services;
    exports com.example.tunarche;
}