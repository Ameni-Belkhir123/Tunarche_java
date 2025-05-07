package TunArche.controllers;

import TunArche.entities.Billet;
import TunArche.entities.Event;
import TunArche.entities.UserSession;
import TunArche.services.BilletImpl;
import TunArche.services.MailSender;
import TunArche.services.PDFGenerator;
import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.sql.Timestamp;
import java.time.*;
import java.util.Date;
import java.util.Random;

public class BilletFormController {
    @FXML
    private ComboBox<String> typeComboBox;
    @FXML
    private Label confirmationLabel;
    @FXML
    private ComboBox<String> modePaiementComboBox;

    private Event event; // Référencé depuis l'appelant

    @FXML
    private TextField typeField;

    @FXML
    private TextField modePaiementField;



    public void setEvent(Event event) {
        this.event = event;
    }
    @FXML
    public void initialize() {
        typeComboBox.getItems().addAll("Standard", "VIP", "Premium");
        modePaiementComboBox.getItems().addAll("Carte Bancaire", "Espèces", "PayPal");
    }


    @FXML
    private void handleReservation() {
        String typeBillet = typeComboBox.getValue();
        String modePaiement = modePaiementComboBox.getValue();

        Billet billet = new Billet();
        billet.setEventId(event.getId());
        billet.setType(typeBillet);
        billet.setNumero(generateRandomNumber(5));
        billet.setModePaiement(modePaiement);
        billet.setDateEmission(Timestamp.valueOf(LocalDateTime.now()));
        billet.setBuyerId(UserSession.getCurrentUser().getId());

        BilletImpl billetService = new BilletImpl();
        billetService.create(billet);

        try {
            File pdf = PDFGenerator.generateBilletPDF(billet);
            MailSender.sendMailWithAttachment(UserSession.getCurrentUser().getEmail(), pdf); // 💡 Remplacer par l'email réel du participant
        } catch (Exception e) {
            e.printStackTrace();
        }

        confirmationLabel.setText("Votre billet a été réservé avec succès !");
        PauseTransition pause = new PauseTransition(Duration.seconds(2));
        pause.setOnFinished(event -> {
            ((Stage) confirmationLabel.getScene().getWindow()).close();
        });
        pause.play();
    }


    private String generateRandomNumber(int length) {
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(random.nextInt(10)); // Génère un chiffre entre 0 et 9
        }
        return sb.toString();
    }}
