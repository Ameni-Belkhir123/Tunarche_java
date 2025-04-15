package TunArche.controllers;

import TunArche.entities.Concours;
import TunArche.services.Concourslmpl;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class AfficherConcoursUtilisateur {

    @FXML
    private FlowPane concoursContainer;

    @FXML
    private TextField searchField;

    private ObservableList<Concours> concoursList;
    private FilteredList<Concours> filteredConcours;

    private final Concourslmpl concoursService = new Concourslmpl();

    @FXML
    public void initialize() {
        // Charger les concours depuis la base de données
        List<Concours> concoursFromDb = concoursService.showAll();

        concoursList = FXCollections.observableArrayList(concoursFromDb);

        filteredConcours = new FilteredList<>(concoursList, p -> true);

        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredConcours.setPredicate(concours -> {
                if (newValue == null || newValue.isEmpty()) return true;
                String lowerCaseFilter = newValue.toLowerCase();
                return concours.getTitre().toLowerCase().contains(lowerCaseFilter)
                        || concours.getDescription().toLowerCase().contains(lowerCaseFilter);
            });
            afficherCartes(filteredConcours);
        });

        afficherCartes(filteredConcours);
    }

    private void afficherCartes(List<Concours> concoursList) {
        concoursContainer.getChildren().clear();

        for (Concours concours : concoursList) {
            VBox card = createConcoursCard(concours);
            concoursContainer.getChildren().add(card);
        }
    }

    private VBox createConcoursCard(Concours concours) {
        VBox card = new VBox(8);
        card.setPadding(new Insets(10));
        card.setAlignment(Pos.CENTER);
        card.setPrefWidth(220);
        card.setStyle("-fx-background-color: #ffffff; -fx-border-color: #ccc; -fx-border-radius: 8; -fx-background-radius: 8;");

        Label title = new Label(concours.getTitre());
        title.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");
        title.setWrapText(true);

        Label desc = new Label(getShortDescription(concours.getDescription(), 80));
        desc.setWrapText(true);
        desc.setStyle("-fx-text-fill: #444;");

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String datesText = "Du " + concours.getDateDebut().format(formatter) + " au " + concours.getDateFin().format(formatter);
        Label dates = new Label(datesText);
        dates.setStyle("-fx-font-size: 11px; -fx-text-fill: gray;");

        card.setOnMouseEntered(e -> card.setStyle("-fx-background-color: #e0ffe0; -fx-border-color: green; -fx-border-radius: 8; -fx-background-radius: 8; -fx-cursor: hand;"));
        card.setOnMouseExited(e -> card.setStyle("-fx-background-color: #ffffff; -fx-border-color: #ccc; -fx-border-radius: 8; -fx-background-radius: 8;"));
        card.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> redirectToDetails(concours));

        card.getChildren().addAll(title, desc, dates);
        return card;
    }

    private void redirectToDetails(Concours concours) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/detailsConcours.fxml")); // Adapter le chemin
            Parent root = loader.load();
            // DetailsConcoursController controller = loader.getController();
            // controller.setConcours(concours);
            Stage stage = new Stage();
            stage.setTitle("Détails du concours");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getShortDescription(String text, int maxLength) {
        return text.length() > maxLength ? text.substring(0, maxLength) + "..." : text;
    }
}
