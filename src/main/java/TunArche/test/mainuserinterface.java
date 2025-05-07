package TunArche.test;


import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class mainuserinterface extends Application {

    @Override
    public void start(Stage stage) {
        try {
            // Charger le fichier FXML

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/tunarche/login.fxml"));
            Parent root = loader.load();

            // Créer la scène avec le FXML chargé
            Scene scene = new Scene(root);


            // Configurer la fenêtre
            stage.setTitle("Gestion des Utilisateurs");
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Méthode main pour lancer l'application
    public static void main(String[] args) {
        launch(args);
    }
}
