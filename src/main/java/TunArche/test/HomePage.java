package TunArche.test;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class HomePage extends Application {

    public static void main(String[] args) {
        launch(args); // Démarre l'application JavaFX
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            // Assure-toi que le fichier FXML est bien dans /src/main/resources/com/example/tunarche/
            Parent root = FXMLLoader.load(getClass().getResource("/com/example/tunarche/AfficherConcoursAdmin.fxml"));

            Scene scene = new Scene(root);
            primaryStage.setTitle("Liste des Concours");
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (Exception e) {
            System.out.println("Erreur lors du chargement de l'interface : " + e.getMessage());
            e.printStackTrace();
        }
    }
}
