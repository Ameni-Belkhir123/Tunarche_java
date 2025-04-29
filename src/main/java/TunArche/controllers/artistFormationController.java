package TunArche.controllers;

import TunArche.entities.Formation;
import TunArche.services.FormationImpl;
import TunArche.services.NtfySender;
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

import java.time.LocalDate;
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
        Formation f1 = new Formation();
        f1.setTitre("Atelier de Sculpture");
        f1.setDescription("Initiation aux techniques de sculpture sur pierre.");
        f1.setDatedebut(LocalDate.of(2025, 5, 5));
        f1.setDatefin(LocalDate.of(2025, 5, 10));

        Formation f2 = new Formation();
        f2.setTitre("Peinture Contemporaine");
        f2.setDescription("Exploration de la peinture moderne et expérimentale.");
        f2.setDatedebut(LocalDate.of(2025, 6, 1));
        f2.setDatefin(LocalDate.of(2025, 6, 7));

        Formation f3 = new Formation();
        f3.setTitre("Photographie Mobile");
        f3.setDescription("Apprendre à capturer l’instant avec son smartphone.");
        f3.setDatedebut(LocalDate.of(2025, 7, 10));
        f3.setDatefin(LocalDate.of(2025, 7, 12));

        Formation f4 = new Formation();
        f4.setTitre("Dessin au Fusain");
        f4.setDescription("Maîtrise du fusain pour créer des œuvres réalistes.");
        f4.setDatedebut(LocalDate.of(2025, 5, 15));
        f4.setDatefin(LocalDate.of(2025, 5, 20));

        Formation f5 = new Formation();
        f5.setTitre("Céramique Artistique");
        f5.setDescription("Techniques de base et avancées en poterie et céramique.");
        f5.setDatedebut(LocalDate.of(2025, 6, 12));
        f5.setDatefin(LocalDate.of(2025, 6, 18));

        Formation f6 = new Formation();
        f6.setTitre("Calligraphie Arabe");
        f6.setDescription("Art de l'écriture arabe, de la tradition à la création.");
        f6.setDatedebut(LocalDate.of(2025, 7, 1));
        f6.setDatefin(LocalDate.of(2025, 7, 5));

        List<Formation> allFormations = List.of(f1, f2, f3, f4, f5, f6);
        observableFormations = FXCollections.observableArrayList(allFormations);
        filteredFormations = new FilteredList<>(observableFormations, f -> true);
        setupSearchListener();
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
        pagination.setPageCount(Math.max(pageCount, 1));
        pagination.setCurrentPageIndex(0);
        createPage(0);
    }

    private VBox createPage(int pageIndex) {
        int fromIndex = pageIndex * ITEMS_PER_PAGE;
        int toIndex = Math.min(fromIndex + ITEMS_PER_PAGE, filteredFormations.size());
        List<Formation> pageItems = filteredFormations.subList(fromIndex, toIndex);
        displayFormations(pageItems);
        return new VBox(); // Pagination exige un noeud de retour
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

        Label title = new Label(formation.getTitre());
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        Text description = new Text(formation.getDescription());
        description.setWrappingWidth(300);

        HBox buttons = new HBox(15);
        buttons.setPadding(new Insets(10, 0, 0, 0));

        Button acceptButton = new Button("✔️ Accepter");
        acceptButton.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white;");
        acceptButton.setOnAction(e -> {
            showAlert("Formation acceptée", "Vous avez accepté cette formation.");
            buttons.setVisible(false); // cache les boutons
            NtfySender.sendNotification("TunArche", "✔️ Formation acceptée : " + formation.getTitre());
        });

        Button rejectButton = new Button("❌ Refuser");
        rejectButton.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");
        rejectButton.setOnAction(e -> {
            observableFormations.remove(formation); // Ne supprime PAS de la base
            showAlert("Formation refusée", "Cette formation a été retirée de votre liste.");
            updatePagination(); // Actualise pagination et affichage
            NtfySender.sendNotification("TunArche", "❌ Formation rejetée : " + formation.getTitre());
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
        // À implémenter : retour à la page login ou fermeture
    }
}
