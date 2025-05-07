package TunArche.test;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class saharconcours extends Application {

    @Override
    public void start( Stage primaryStage) throws IOException {

            // Charger le fichier FXML

        Parent root = FXMLLoader.load(getClass().getResource("/com/example/tunarche/afficher_concours_grid.fxml"));
        primaryStage.setScene(new Scene(root));
        primaryStage.setTitle("Concours");
        primaryStage.show();




    }

    // Méthode main pour lancer l'application
    public static void main(String[] args) {
        launch(args);
    }
}
