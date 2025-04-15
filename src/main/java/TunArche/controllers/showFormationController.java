package TunArche.controllers;

import TunArche.entities.Formation;
import TunArche.services.FormationImpl;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.ResourceBundle;

public class showFormationController  {
    @FXML
    private GridPane formationGrid;

    @FXML
    private TextField searchField;

    private FormationImpl formationService = new FormationImpl();

    @FXML
    public void initialize() {
        List<Formation> formations = formationService.showAll();

        int col = 0;
        int row = 0;
        for (Formation formation : formations) {
            VBox card = createFormationCard(formation);
            formationGrid.add(card, col, row);
            col++;
            if (col > 2) {
                col = 0;
                row++;
            }
        }
    }

    private VBox createFormationCard(Formation formation) {
        VBox card = new VBox(8);
        card.setPadding(new Insets(10));
        card.setAlignment(Pos.CENTER);
        card.setPrefWidth(220);
        card.setStyle("-fx-background-color: #ffffff; -fx-border-color: #ccc; -fx-border-radius: 8; -fx-background-radius: 8;");

        // Image
        ImageView imageView = new ImageView();
        imageView.setFitWidth(200);
        imageView.setFitHeight(120);

        File imageFile = new File("src/main/resources/images/" + formation.getImage_name());
        if (imageFile.exists()) {
            imageView.setImage(new Image(imageFile.toURI().toString()));
        } else {
            System.out.println("⚠️ Image introuvable : " + formation.getImage_name());
        }

        // Titre
        Label title = new Label(formation.getTitre());
        title.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");
        title.setWrapText(true);

        // Description
        Label desc = new Label(getShortDescription(formation.getDescription(), 80));
        desc.setWrapText(true);
        desc.setStyle("-fx-text-fill: #444;");

        card.setOnMouseEntered(e -> card.setStyle("-fx-background-color: #e0ffe0; -fx-border-color: green; -fx-border-radius: 8; -fx-background-radius: 8; -fx-cursor: hand;"));
        card.setOnMouseExited(e -> card.setStyle("-fx-background-color: #ffffff; -fx-border-color: #ccc; -fx-border-radius: 8; -fx-background-radius: 8;"));
        card.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> redirectToDetails(formation));

        card.getChildren().addAll(imageView, title, desc);
        return card;
    }

    private void redirectToDetails(Formation formation) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/displayFormations.fxml"));
            Parent root = loader.load();

            FormationDetailsController controller = loader.getController();
            controller.setFormation(formation);

            Stage stage = new Stage();
            stage.setTitle("Détails de la formation");
            stage.setScene(new Scene(root));
            stage.show();

            // Optional: Fermer l’ancienne fenêtre
            // ((Stage) formationGrid.getScene().getWindow()).close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getShortDescription(String text, int maxLength) {
        return text.length() > maxLength ? text.substring(0, maxLength) + "..." : text;
    }}