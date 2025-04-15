package TunArche.services;

import TunArche.entities.Formation;
import javafx.application.Application;
import javafx.stage.Stage;

import javafx.stage.FileChooser;

import java.io.File;
import java.time.LocalDate;
import java.util.Date;


public class FileUpload extends Application {

    public static File selectedFile;

    @Override
    public void start(Stage primaryStage) {
        try {
            // Configuration du FileChooser
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Sélectionner une image");
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg", "*.gif"),
                    new FileChooser.ExtensionFilter("Tous les fichiers", "*.*")
            );

            // Affiche la boîte de dialogue et attend la sélection
            selectedFile = fileChooser.showOpenDialog(primaryStage);

            if (selectedFile != null) {
                System.out.println("Image sélectionnée : " + selectedFile.getAbsolutePath());

                // Crée une nouvelle formation avec le fichier sélectionné
                Formation formation = createFormationWithImage(selectedFile);

                // Sauvegarde la formation
                FormationImpl formationService = new FormationImpl();
                formationService.create(formation);

                System.out.println("Formation créée avec succès !");
            } else {
                System.out.println("Aucune image sélectionnée.");
            }
        } catch (Exception e) {
            System.err.println("Erreur lors de la sélection du fichier : " + e.getMessage());
            e.printStackTrace();
        } finally {
            System.exit(0); // Ferme l'application après traitement
        }
    }

    /**
     * Crée une formation avec les métadonnées de l'image sélectionnée
     */
    private Formation createFormationWithImage(File imageFile) {
        Formation formation = new Formation();

        // Métadonnées de base
        formation.setTitre("Formation avec image");
        formation.setDescription("Exemple avec image");
        formation.setDatedebut(LocalDate.now());
        formation.setDatefin(LocalDate.now().plusDays(7)); // 1 semaine après
        formation.setNbrplaces(10);
        formation.setLink("http://example.com");

        // Métadonnées de l'image
        formation.setImage_name(imageFile.getName());
        formation.setImage_size((int) imageFile.length());
        formation.setUpdated_at(LocalDate.now());

        return formation;
    }

    public static void main(String[] args) {
        // Lance l'application JavaFX
        launch(args);
    }
}


