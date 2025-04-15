package TunArche.controllers;

import TunArche.entities.Concours;
import TunArche.services.Concourslmpl;
import javafx.animation.FadeTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import java.io.IOException;
import java.time.LocalDate;

public class AjouterConcours {

    @FXML private TextField titreField;
    @FXML private TextArea descriptionArea;
    @FXML private DatePicker dateDebutPicker;
    @FXML private DatePicker dateFinPicker;
    @FXML private Label erreurLabel;

    private boolean modeEdition = false;
    private Concours concoursExistant;
    private AdminConcoursController adminController;

    public void setAdminController(AdminConcoursController controller) {
        this.adminController = controller;
    }

    @FXML
    private void ajouterConcours(ActionEvent event) {
        erreurLabel.setText("");
        String titre = titreField.getText().trim();
        String description = descriptionArea.getText().trim();
        LocalDate dateDebut = dateDebutPicker.getValue();
        LocalDate dateFin = dateFinPicker.getValue();

        if (titre.isEmpty() || description.isEmpty() || dateDebut == null || dateFin == null) {
            erreurLabel.setText("Tous les champs sont obligatoires.");
            erreurLabel.setTextFill(Color.RED);
            return;
        }

        if (dateDebut.isAfter(dateFin)) {
            erreurLabel.setText("Date début doit précéder la date fin.");
            erreurLabel.setTextFill(Color.RED);
            return;
        }

        Concourslmpl service = new Concourslmpl();

        if (modeEdition && concoursExistant != null) {
            concoursExistant.setTitre(titre);
            concoursExistant.setDescription(description);
            concoursExistant.setDateDebut(dateDebut);
            concoursExistant.setDateFin(dateFin);
            service.update(concoursExistant);
            erreurLabel.setText("Concours mis à jour !");
        } else {
            Concours nouveau = new Concours(titre, description, dateDebut, dateFin);
            service.create(nouveau);
            erreurLabel.setText("Concours ajouté !");
        }

        erreurLabel.setTextFill(Color.GREEN);

        if (adminController != null) {
            adminController.reloadConcours();
            adminController.initialize();
        }
    }

    @FXML
    private void retourVersAdmin(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/tunarche/AfficherConcoursAdmin.fxml"));
            Parent adminView = loader.load();

            // ✅ On remplace toute la racine de la scène par la nouvelle vue
            titreField.getScene().setRoot(adminView);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void setConcoursPourEdition(Concours concours) {
        this.modeEdition = true;
        this.concoursExistant = concours;

        titreField.setText(concours.getTitre());
        descriptionArea.setText(concours.getDescription());
        dateDebutPicker.setValue(concours.getDateDebut());
        dateFinPicker.setValue(concours.getDateFin());
    }
}
