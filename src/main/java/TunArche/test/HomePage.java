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
            Parent root = FXMLLoader.load(getClass().getResource("/com/example/tunarche/DispayPublication.fxml"));
            //PublicationForm.fxml (back de publication)                            |--> PublicationFormController
            // DispayPublication.fxml(fron les cartes)                              |-->showPublicationControlleur
            //commantaire.fxml(back de comm)                                        |-->CommantaireControlleur
            //publication.fxml(page contient liste des commentaires                 |-->PublicationControlleur
            //PublicationDetails.fxml ( page details de chaque publication)         |-->PublicationDetailsController

            Scene scene = new Scene(root);
            stage.setScene(scene); // ✅ utiliser l'objet 'stage' et non la classe
            stage.show(); // ✅ appeler show() sur l'objet 'stage'
        } catch (IOException e) {
            System.out.println("Erreur de chargement FXML : " + e.getMessage());
            e.printStackTrace();
        }
    }
}