package TunArche.controllers;

import TunArche.entities.Formation;
import TunArche.services.FormationImpl;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Text;

import java.util.List;
import java.util.stream.Collectors;

public class artistFormationController {
    @FXML private GridPane formationGrid;
    @FXML private MenuItem btnLogout;
    @FXML private MenuItem menuItemUser;
    @FXML private Button userbutton;
    @FXML private TextField searchField;
    @FXML private Pagination pagination;

    private final FormationImpl formationService = new FormationImpl();
    private final int COLUMNS = 2;
    private final int ITEMS_PER_PAGE = 6;

    private ObservableList<Formation> observableFormations;
    private FilteredList<Formation> filteredFormations;

    @FXML
    public void initialize() {
        // Initialisation des listes observables
        List<Formation> allFormations = formationService.showAll();
        observableFormations = FXCollections.observableArrayList(allFormations);
        filteredFormations = new FilteredList<>(observableFormations, f -> true);

        // Configuration de la recherche
        setupSearchListener();

        // Configuration de la pagination
        setupPagination();
    }

    private void setupSearchListener() {
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredFormations.setPredicate(formation -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                String lowerCaseFilter = newValue.toLowerCase();
                return formation.getTitre().toLowerCase().contains(lowerCaseFilter) ||
                        formation.getDescription().toLowerCase().contains(lowerCaseFilter);
            });
            updatePagination();
        });
    }

    private void setupPagination() {
        pagination.setPageFactory(this::createPage);
        updatePagination();
    }

    private void updatePagination() {
        int pageCount = (int) Math.ceil((double) filteredFormations.size() / ITEMS_PER_PAGE);
        pagination.setPageCount(pageCount > 0 ? pageCount : 1);
        pagination.setCurrentPageIndex(0);
    }

    private VBox createPage(int pageIndex) {
        int fromIndex = pageIndex * ITEMS_PER_PAGE;
        int toIndex = Math.min(fromIndex + ITEMS_PER_PAGE, filteredFormations.size());
        List<Formation> pageItems = filteredFormations.subList(fromIndex, toIndex);

        displayFormations(pageItems);
        return new VBox(); // Retourne un conteneur vide requis par la pagination
    }

    private void displayFormations(List<Formation> formations) {
        formationGrid.getChildren().clear();

        int row = 0;
        int column = 0;

        for (Formation formation : formations) {
            VBox card = createFormationCard(formation);
            formationGrid.add(card, column, row);

            column++;
            if (column >= COLUMNS) {
                column = 0;
                row++;
            }
        }
    }

    private VBox createFormationCard(Formation formation) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(15));
        card.setStyle("-fx-background-color: #f5f5f5; -fx-border-radius: 10; -fx-background-radius: 10; -fx-border-color: #ccc;");
        card.setMaxWidth(350);

        // Image
        ImageView imageView = new ImageView();
        imageView.setFitHeight(150);
        imageView.setFitWidth(300);
        try {
            String imagePath = formation.getImage_name();
            if (imagePath != null && !imagePath.isEmpty()) {
                imageView.setImage(new Image("file:" + imagePath));
            }
        } catch (Exception e) {
            System.out.println("Erreur de chargement de l'image: " + e.getMessage());
        }

        // Infos
        Label title = new Label(formation.getTitre());
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        Text description = new Text(formation.getDescription());
        description.setWrappingWidth(300);

        // Boutons
        HBox buttons = new HBox(15);
        buttons.setPadding(new Insets(10, 0, 0, 0));

        Button acceptButton = new Button("✔️ Accepter");
        acceptButton.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white;");
        acceptButton.setOnAction(e -> {
            showAlert("Formation acceptée", "Vous avez accepté cette formation.");
            // Logique d'acceptation ici
        });

        Button rejectButton = new Button("❌ Refuser");
        rejectButton.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");
        rejectButton.setOnAction(e -> {
            observableFormations.remove(formation);
            showAlert("Formation refusée", "Cette formation a été retirée de votre liste.");
            updatePagination();
        });

        buttons.getChildren().addAll(acceptButton, rejectButton);
        card.getChildren().addAll(imageView, title, description, buttons);
        return card;
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(title);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @FXML
    private void handleLogout() {
        System.out.println("Déconnexion...");
        // Logique de déconnexion
    }
}
