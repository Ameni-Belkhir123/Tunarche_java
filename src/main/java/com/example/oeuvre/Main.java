package com.example.oeuvre;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws IOException {
        try {
            // Charger le fichier FXML de la page de connexion
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/oeuvre/AdminView.fxml"));
            // Créer la scène
            Scene scene = new Scene(loader.load());
            // Définir le titre de la fenêtre
            primaryStage.setTitle("Art Gallery App - Connexion");
            // Définir la scène sur le stage
            primaryStage.setScene(scene);
            // Afficher la fenêtre
            primaryStage.show();
        } catch (IOException e) {
            // Afficher une erreur si le fichier FXML ne peut pas être chargé
            System.err.println("Erreur lors du chargement de l'interface : " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        // Lancer l'application JavaFX
        launch();
    }
}
