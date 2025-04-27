package TunArche.test;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class HomePage extends Application {
    public static void main(String[] args) {
        launch(args);
    }
    @Override
    public void start(Stage stage) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/formation.fxml"));
            Scene scene = new Scene(root);
            stage.setScene(scene); // ✅ utiliser l'objet 'stage' et non la classe
            stage.setTitle("Ajouter une Formation"); // facultatif mais sympa
            stage.show(); // ✅ appeler show() sur l'objet 'stage'
        } catch (IOException e) {
            System.out.println("Erreur de chargement FXML : " + e.getMessage());
            e.printStackTrace();
        }
    }

    }
