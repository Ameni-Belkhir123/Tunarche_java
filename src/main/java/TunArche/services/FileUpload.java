package TunArche.services;

import TunArche.entities.Formation;
import javafx.application.Application;
import javafx.stage.Stage;

import javafx.stage.FileChooser;

import java.io.File;
import java.util.Date;


public class FileUpload extends Application {

    public static File selectedFile;

    @Override
    public void start(Stage stage) throws Exception {

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Sélectionner une image");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );

        selectedFile = fileChooser.showOpenDialog(stage);
        if (selectedFile != null) {
            System.out.println("Image sélectionnée : " + selectedFile.getAbsolutePath());

            // Simule une formation
            Formation f = new Formation();
            f.setTitre("Formation avec image");
            f.setDescription("Exemple avec image");
            f.setDatedebut(new Date());
            f.setDatefin(new Date());
            f.setNbrplaces(10);
            f.setLink("http://example.com");
            f.setImage_name(selectedFile.getName());
            f.setImage_size((int) selectedFile.length());
            f.setUpdated_at(new Date());

            new FormationImpl().create(f);
        } else {
            System.out.println("Aucune image sélectionnée.");
        }

        System.exit(0);
    }

    public static void main(String[] args) {
        launch(args);
    }
}


