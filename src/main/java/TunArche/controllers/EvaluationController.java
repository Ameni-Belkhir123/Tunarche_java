package TunArche.controllers;

import TunArche.entities.Evaluation;
import TunArche.entities.Formation;
import TunArche.entities.UserSession;
import TunArche.services.EvaluationImpl;
import TunArche.services.FormationImpl;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.*;

public class EvaluationController {

    @FXML
    private BarChart<String, Number> barChart;
    @FXML
    private CategoryAxis xAxis;
    @FXML
    private NumberAxis yAxis;
    @FXML
    private Label averageLabel;
    @FXML
    private Label totalReviewsLabel;

    @FXML
    private ListView<String> commentListView;

    @FXML private ProgressBar progress5;
    @FXML private ProgressBar progress4;
    @FXML private ProgressBar progress3;
    @FXML private ProgressBar progress2;
    @FXML private ProgressBar progress1;
    @FXML
    private MenuItem menuItemUser;
    @FXML
    private Button eventbutton;
    @FXML
    private Button btnBillets;
    private final EvaluationImpl evaluationService = new EvaluationImpl();

    private void updateProgressBar(ProgressBar progressBar, int count, int total) {
        double progress = total == 0 ? 0.0 : (double) count / total;
        progressBar.setProgress(progress);
    }

    public void setFormation(Formation formation) {
        List<Evaluation> evaluations = evaluationService.showByFormation(formation.getId());

        Map<Integer, Integer> noteCounts = new HashMap<>();
        for (int i = 1; i <= 5; i++) {
            noteCounts.put(i, 0);
        }

        double total = 0;
        List<String> commentaires = new ArrayList<>();

        for (Evaluation e : evaluations) {
            int note = e.getNote();
            noteCounts.put(note, noteCounts.get(note) + 1);
            total += note;

            if (e.getCommentaire() != null && !e.getCommentaire().isEmpty()) {
                commentaires.add("⭐ " + note + " - " + e.getCommentaire());
            }
        }

        int totalReviews = evaluations.size();
        double average = totalReviews == 0 ? 0.0 : total / totalReviews;

        averageLabel.setText(String.format("%.1f ★", average));
        totalReviewsLabel.setText(totalReviews + " avis");

        updateProgressBar(progress5, noteCounts.get(5), totalReviews);
        updateProgressBar(progress4, noteCounts.get(4), totalReviews);
        updateProgressBar(progress3, noteCounts.get(3), totalReviews);
        updateProgressBar(progress2, noteCounts.get(2), totalReviews);
        updateProgressBar(progress1, noteCounts.get(1), totalReviews);

        commentListView.getItems().setAll(commentaires);

        // BarChart update (facultatif mais utile si tu veux garder)
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Répartition des notes");

        for (int i = 5; i >= 1; i--) {
            series.getData().add(new XYChart.Data<>(String.valueOf(i), noteCounts.get(i)));
        }

        barChart.getData().clear();
        barChart.getData().add(series);
    }
    @FXML
    private ComboBox<Formation> formationComboBox;
    private FormationImpl formationService = new FormationImpl();

    @FXML
    public void initialize() {
        if (UserSession.getCurrentUser() != null) {
            String fullName = UserSession.getCurrentUser().getName() + " " + UserSession.getCurrentUser().getLastName();
            menuItemUser.setText("👤 " + fullName);
        } else {
            String fullName = UserSession.getCurrentUser().getName() + " " + UserSession.getCurrentUser().getLastName();
            menuItemUser.setText("👤 " + fullName);
        }
        loadFormations();

        // Afficher uniquement le titre dans la ComboBox
        formationComboBox.setCellFactory(lv -> new ListCell<Formation>() {
            @Override
            protected void updateItem(Formation formation, boolean empty) {
                super.updateItem(formation, empty);
                setText(empty || formation == null ? null : formation.getTitre());
            }
        });

        // Pour que le titre apparaisse aussi quand une formation est sélectionnée
        formationComboBox.setButtonCell(new ListCell<Formation>() {
            @Override
            protected void updateItem(Formation formation, boolean empty) {
                super.updateItem(formation, empty);
                setText(empty || formation == null ? null : formation.getTitre());
            }
        });

        // Optionnel : ajouter un listener pour mettre à jour les évaluations quand on choisit une formation
        formationComboBox.setOnAction(e -> {
            Formation selected = formationComboBox.getSelectionModel().getSelectedItem();
            if (selected != null) {
                setFormation(selected);
            }
        });
    }

    private void loadFormations() {
        List<Formation> formations = formationService.showAll(); // ou findAll()
        formationComboBox.getItems().addAll(formations);
    }
    @FXML
    public void goToFormation(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/tunarche/addFormation.fxml"));
            Parent root = loader.load();

            // Si tu veux passer des données au contrôleur de la formation, tu peux le faire ici
            formationController formationController = loader.getController();

            // Remplacer la scène actuelle par celle de formation
            Scene scene = new Scene(root);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void goToFront(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/tunarche/formation.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Formations - Vue Front");
            stage.setScene(new Scene(root));
            stage.show();

            // Fermer l'ancienne fenêtre (optionnel)
            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            currentStage.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void showAlert(String title, String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText("Information");
        alert.setContentText(msg);
        alert.showAndWait();
    }
    @FXML
    private void handleLogout() {
        UserSession.clear(); // Efface l'utilisateur connecté
        Stage stage = (Stage) eventbutton.getScene().getWindow();
        Parent root;
        try {
            root = FXMLLoader.load(getClass().getResource("/com/example/tunarche/login.fxml"));
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    public void handleformation(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/tunarche/addFormation.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) eventbutton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            showAlert("Navigation Error", "Failed to load Event Management interface: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    public void handelevent()
    {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/tunarche/event.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) eventbutton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            showAlert("Navigation Error", "Failed to load Event Management interface: " + e.getMessage());
            e.printStackTrace();
        }

    }
    @FXML
    public void handebillet()
    {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/tunarche/billet.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) btnBillets.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            showAlert("Navigation Error", "Failed to load Event Management interface: " + e.getMessage());
            e.printStackTrace();
        }

    }
    @FXML
    public void handlePublication(){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/tunarche/PublicationForm.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) btnBillets.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            showAlert("Navigation Error", "Failed to load Event Management interface: " + e.getMessage());
            e.printStackTrace();
        }
    }
    @FXML
    public void handleCommantaire(){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/tunarche/commantaire.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) btnBillets.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            showAlert("Navigation Error", "Failed to load Event Management interface: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void handeluser(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/tunarche/user.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) btnBillets.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            showAlert("Navigation Error", "Failed to load Event Management interface: " + e.getMessage());
            e.printStackTrace();
        }
    }


    public void handleevaluation(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/tunarche/evaluations.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) btnBillets.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            showAlert("Navigation Error", "Failed to load Event Management interface: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void handelconcours(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/tunarche/AfficherConcoursAdmin.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) btnBillets.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            showAlert("Navigation Error", "Failed to load Event Management interface: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void handelparticipation(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/tunarche/AfficherParticipationAdmin.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) btnBillets.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            showAlert("Navigation Error", "Failed to load Event Management interface: " + e.getMessage());
            e.printStackTrace();
        }
    }
}