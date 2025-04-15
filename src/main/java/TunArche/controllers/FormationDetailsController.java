package TunArche.controllers;


import TunArche.entities.Evaluation;
import TunArche.entities.Formation;
import TunArche.services.EvaluationImpl;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.text.SimpleDateFormat;

public class FormationDetailsController {

    @FXML private Label nomFormationLabel, descriptionLabel, dateDebutLabel, dateFinLabel, placesRestantesLabel;
    @FXML private Button participerBtn, accederform, emojiBtn;
    @FXML private ToggleButton star1, star2, star3, star4, star5;
    @FXML private TextArea commentaireField;
    @FXML private VBox commentairesVBox;

    private int placesRestantes;
    private int note = 0;
    private LocalDate dateDebut;
    private LocalDate dateFin;
    private int formationId;
    private Formation formation;

    private final EvaluationImpl evaluationService = new EvaluationImpl();

    private final List<String> badWords = Arrays.asList(
            "merde", "con", "stupid", "fuck", "shit", "putain", "idiot", "dumb", "nul"
    );



    public void setFormation(Formation formation) {
        this.formation = formation;

        // Conversion Date vers LocalDate en toute sécurité
        this.dateDebut = formation.getDatedebut(); // LocalDate directement
        this.dateFin = formation.getDatefin();     // LocalDate directement
        this.placesRestantes = formation.getNbrplaces();
        this.formationId = formation.getId();

        nomFormationLabel.setText(formation.getTitre());
        descriptionLabel.setText(formation.getDescription());

        // Affichage formaté des dates
        dateDebutLabel.setText(dateDebut != null ? dateDebut.toString() : "Date début indisponible");
        dateFinLabel.setText(dateFin != null ? dateFin.toString() : "Date fin indisponible");
        placesRestantesLabel.setText(String.valueOf(placesRestantes));

        updateButtonStates();
        setStarLabels();
        chargerCommentaires();
    }

    private void updateButtonStates() {
        LocalDate today = LocalDate.now();
        accederform.setDisable(!today.equals(dateDebut));
        if (today.isAfter(dateFin)) {
            accederform.setText("FORMATION TERMINÉE");
            accederform.setDisable(true);
        }
    }

    private void setStarLabels() {
        ToggleButton[] stars = {star1, star2, star3, star4, star5};
        for (ToggleButton star : stars) {
            star.setText("★");
            star.setStyle("-fx-text-fill: gray; -fx-font-size: 20px;");
        }
    }

    private void updateStars(int selected) {
        ToggleButton[] stars = {star1, star2, star3, star4, star5};
        for (int i = 0; i < stars.length; i++) {
            stars[i].setStyle("-fx-text-fill: " + (i < selected ? "gold" : "gray") + "; -fx-font-size: 20px;");
        }
        note = selected;
    }

    @FXML
    void handleParticiper() {
        if (placesRestantes > 0) {
            placesRestantes--;
            placesRestantesLabel.setText(String.valueOf(placesRestantes));
        } else {
            showInfo("Aucune place restante !");
        }
    }

    @FXML
    void handleStar(ActionEvent event) {
        Object source = event.getSource();
        if (source == star1) updateStars(1);
        else if (source == star2) updateStars(2);
        else if (source == star3) updateStars(3);
        else if (source == star4) updateStars(4);
        else if (source == star5) updateStars(5);
    }

    @FXML
    void handleEnvoyer() {
        String commentaire = commentaireField.getText().trim();

        if (note == 0) {
            showWarning("Veuillez donner une note !");
            return;
        }

        if (commentaire.isEmpty()) {
            showWarning("Le commentaire ne peut pas être vide !");
            return;
        }

        if (contientBadWord(commentaire)) {
            showWarning("Le commentaire contient des mots inappropriés !");
            return;
        }

        Evaluation evaluation = new Evaluation();
        evaluation.setCommentaire(commentaire);
        evaluation.setNote(note);
        evaluation.setFormation(formation);

        try {
            evaluationService.create(evaluation);
            showInfo("Merci pour votre avis !");
            commentaireField.clear();
            updateStars(0);
            afficherCommentaire(commentaire, note);
        } catch (Exception e) {
            e.printStackTrace();
            showWarning("Erreur lors de l'enregistrement.");
        }
    }

    private void chargerCommentaires() {
        commentairesVBox.getChildren().clear();
        try {
            List<Evaluation> evaluations = evaluationService.showByFormation(formationId);
            for (Evaluation eval : evaluations) {
                afficherCommentaire(eval.getCommentaire(), eval.getNote());
            }
        } catch (Exception e) {
            e.printStackTrace();
            showWarning("Erreur lors du chargement des commentaires.");
        }
    }

    private void afficherCommentaire(String commentaire, int note) {
        Label label = new Label("★".repeat(note) + " " + commentaire);
        label.setStyle("-fx-background-color: white; -fx-padding: 10; -fx-border-radius: 8; -fx-background-radius: 8;");
        commentairesVBox.getChildren().add(label);
    }

    private boolean contientBadWord(String text) {
        String lower = text.toLowerCase();
        return badWords.stream().anyMatch(lower::contains);
    }

    private void showWarning(String message) {
        new Alert(Alert.AlertType.WARNING, message).showAndWait();
    }

    private void showInfo(String message) {
        new Alert(Alert.AlertType.INFORMATION, message).showAndWait();
    }

    @FXML
    void openEmojiPicker() {
        Dialog<String> emojiDialog = new Dialog<>();
        emojiDialog.setTitle("Choisir un emoji");
        emojiDialog.setHeaderText("Clique sur un emoji pour l'ajouter");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        String[] emojis = {"😊", "😍", "🥰", "😂", "😁", "😎", "😢", "😡", "😱", "🙄", "👍", "👎", "❤️", "🔥", "🎉"};
        int col = 0, row = 0;

        for (String emoji : emojis) {
            Button btn = new Button(emoji);
            btn.setStyle("-fx-font-size: 20px;");
            btn.setOnAction(e -> {
                commentaireField.appendText(emoji);
                emojiDialog.setResult(emoji);
                emojiDialog.close();
            });
            grid.add(btn, col++, row);
            if (col == 5) {
                col = 0;
                row++;
            }
        }

        emojiDialog.getDialogPane().setContent(grid);
        emojiDialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        emojiDialog.showAndWait();
    }
    @FXML
    void handleAccederFormation() {
        if (formation != null && formation.getLink() != null && !formation.getLink().isEmpty()) {
            try {
                java.awt.Desktop.getDesktop().browse(new java.net.URI(formation.getLink()));
            } catch (Exception e) {
                e.printStackTrace();
                showWarning("Impossible d'ouvrir le lien.");
            }
        } else {
            showWarning("Aucun lien disponible pour cette formation.");
        }
    }

}
