package TunArche.controllers;

import TunArche.entities.Concours;
import TunArche.entities.Participation;
import TunArche.entities.UserSession;
import TunArche.entities.user;
import TunArche.services.EmailService;
import TunArche.services.Participationlmpl;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class ParticipationFormController implements Initializable {

    @FXML
    private TextField nomArtisteField;

    @FXML
    private TextField emailArtisteField;

    @FXML
    private Label titreConcoursLabel;

    @FXML
    private ImageView imagePreview;

    private String imagePath;

    @FXML
    private Label imagePreviewPlaceholder;

    private final Participationlmpl participationService = new Participationlmpl();
    private final EmailService emailService = new EmailService();
    private Concours selectedConcours;

    /**
     * Initializes the controller.
     * This method is automatically called after the FXML file has been loaded.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Prefill form with current user's information if available
        prefillFormWithUserData();

        // Make name and email fields not editable since they're prefilled
        nomArtisteField.setEditable(false);
        emailArtisteField.setEditable(false);

        // Optional: Add styling to show they're not editable
        nomArtisteField.setStyle("-fx-background-color: #f4f4f4; -fx-opacity: 0.9;");
        emailArtisteField.setStyle("-fx-background-color: #f4f4f4; -fx-opacity: 0.9;");
    }

    /**
     * Prefills the form with the connected user's data
     */
    private void prefillFormWithUserData() {
        // Get the current user from session
        user currentUser = UserSession.getCurrentUser();

        if (currentUser != null) {
            // Set the user's name (combining first and last name)
            nomArtisteField.setText(currentUser.getName() + " " + currentUser.getLastName());

            // Set the user's email
            emailArtisteField.setText(currentUser.getEmail());
        } else {
            System.out.println("Aucun utilisateur connecté.");
        }
    }

    /**
     * Sets the contest information and updates the label
     * @param concours The Contest object containing the information
     */
    public void setConcours(Concours concours) {
        this.selectedConcours = concours;
        if (titreConcoursLabel != null) {
            titreConcoursLabel.setText(concours.getTitre());
        } else {
            System.out.println("Le label 'titreConcoursLabel' est null.");
        }
    }

    /**
     * Handles image selection
     * @param event Action event triggered by clicking the image selection button
     */
    @FXML
    private void handleImageSelection(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg"));
        File selectedFile = fileChooser.showOpenDialog(new Stage());
        if (selectedFile != null) {
            imagePath = selectedFile.getAbsolutePath();
            Image image = new Image("file:" + imagePath);
            imagePreview.setImage(image);

            // Hide the placeholder text when an image is selected
            if (imagePreviewPlaceholder != null) {
                imagePreviewPlaceholder.setVisible(false);
            }
        }
    }

    /**
     * Handles form submission
     * @param event Action event triggered by clicking the submit button
     */
    @FXML
    private void handleSubmit(ActionEvent event) {
        // Only validate image selection since other fields are prefilled
        if (imagePath == null) {
            showAlert(Alert.AlertType.ERROR, "Erreur de Saisie",
                    "Image requise",
                    "Veuillez sélectionner une image pour votre participation.");
            return;
        }

        // Get form data
        String nomArtiste = nomArtisteField.getText();
        String emailArtiste = emailArtisteField.getText();
        String concoursTitle = titreConcoursLabel.getText();

        // Create new participation
        Participation participation = new Participation();
        participation.setNom_artiste(UserSession.getCurrentUser().getId());
        participation.setConcours_id(selectedConcours != null ? selectedConcours.getId() : 1);
        participation.setDate_inscription(LocalDate.now().toString());
        participation.setImagePath(imagePath);
        participation.setNbr_votes(0);
        participation.setOeuvre_id(9); // À définir selon votre logique métier

        try {
            // Save the participation
            participationService.save(participation);

            // Send confirmation email
            boolean emailSent = emailService.sendConfirmationEmail(emailArtiste, nomArtiste, concoursTitle);

            // Show success message
            if (emailSent) {
                showAlert(Alert.AlertType.INFORMATION, "Participation Enregistrée",
                        "Votre participation a été enregistrée avec succès",
                        "Un email de confirmation a été envoyé à " + emailArtiste);
            } else {
                showAlert(Alert.AlertType.WARNING, "Participation Enregistrée",
                        "Votre participation a été enregistrée avec succès",
                        "Cependant, l'envoi de l'email de confirmation a échoué.");
            }

            // Close the form window automatically after successful submission
            closeWindow();

        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur",
                    "Une erreur s'est produite lors de l'enregistrement",
                    e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Handles the return action
     * @param event Action event triggered by clicking the return button
     */
    @FXML
    private void handleRetour(ActionEvent event) {
        closeWindow();
    }

    /**
     * Closes the current window
     */
    private void closeWindow() {
        Stage stage = (Stage) nomArtisteField.getScene().getWindow();
        stage.close();
    }

    /**
     * Validates email format
     * @param email Email address to validate
     * @return true if email is valid, false otherwise
     */
    private boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        return email.matches(emailRegex);
    }

    /**
     * Shows an alert dialog
     */
    private void showAlert(Alert.AlertType alertType, String title, String headerText, String contentText) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);
        alert.showAndWait();
    }

    /**
     * Resets the form (not needed now since we close the window, but kept for reference)
     */
    private void resetForm() {
        imagePreview.setImage(null);
        imagePath = null;
    }
}