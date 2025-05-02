package com.example.oeuvre.Tools;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    private static Connection connection;

    public static Connection getConnection() {
        if (connection == null) {
            try {
                // Charger le driver JDBC pour MySQL
                Class.forName("com.mysql.cj.jdbc.Driver");

                // Connexion à la base de données
                connection = DriverManager.getConnection(
                        "jdbc:mysql://localhost:3306/art_gallery", "root", ""
                );
            } catch (ClassNotFoundException | SQLException e) {
                e.printStackTrace();
            }
        }
        return connection;
    }
}


