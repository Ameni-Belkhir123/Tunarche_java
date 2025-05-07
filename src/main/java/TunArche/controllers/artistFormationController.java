package TunArche.controllers;

import TunArche.entities.Formation;
import TunArche.entities.PdfGenerator;
import TunArche.entities.UserSession;
import TunArche.services.FormationImpl;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class artistFormationController {
    @FXML private GridPane formationGrid;
    @FXML private MenuItem btnLogout;
    @FXML private MenuItem menuItemUser;
    @FXML private Button userbutton;
    @FXML private TextField searchField;
    @FXML private Pagination pagination;
    @FXML
    private Button eventbutton;

    private final FormationImpl formationService = new FormationImpl();
    private final int COLUMNS = 2;
    private final int ITEMS_PER_PAGE = 6;

    private ObservableList<Formation> observableFormations;
    private FilteredList<Formation> filteredFormations;

    @FXML
    public void initialize() {
        if (UserSession.getCurrentUser() != null) {
            String fullName = UserSession.getCurrentUser().getName() + " " + UserSession.getCurrentUser().getLastName();
            menuItemUser.setText("👤 " + fullName);


        } else {
            menuItemUser.setText(UserSession.getCurrentUser().getName() + " " + UserSession.getCurrentUser().getLastName());
            System.err.println("⚠️ Aucun utilisateur connecté !");
        }
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
    private void handlecv()
    {

        PdfGenerator.generateUserCV(UserSession.getCurrentUser());
        String f2="C:\\Users\\marwe\\Desktop\\Tun-Arche\\cv_"+UserSession.getCurrentUser().getName()+"_"+UserSession.getCurrentUser().getLastName()+".pdf";
        PdfGenerator.openPdfFile(f2);
    }

    @FXML
    private void handeleventt() {
        try {
            // Load the new FXML file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/tunarche/eventfront.fxml"));
            Parent root = loader.load();

            // Get the current stage from the button (same window)
            Stage stage = (Stage) eventbutton.getScene().getWindow();

            // Set new scene
            Scene scene = new Scene(root);
            stage.setTitle("Gestion des événements");
            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleLogout() {
        UserSession.clear(); // Efface l'utilisateur connecté
        Stage stage = (Stage) userbutton.getScene().getWindow();
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
    public void handecalander() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/tunarche/usercalenderevent.fxml"));
            Parent root = loader.load();

            // Get the current stage from any UI component, e.g., a button
            Stage stage = (Stage) userbutton.getScene().getWindow(); // Replace `someUiElement` with your actual node, like a button or pane

            Scene scene = new Scene(root);
            stage.setTitle("Event Calendar");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    @FXML
    public void handelblog() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/tunarche/DispayPublication.fxml"));
            Parent root = loader.load();

            // Get the current stage from any UI component, e.g., a button
            Stage stage = (Stage) userbutton.getScene().getWindow(); // Replace `someUiElement` with your actual node, like a button or pane

            Scene scene = new Scene(root);
            stage.setTitle("blog");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    public void handelformation(ActionEvent actionEvent) {
        try {
            if(UserSession.getCurrentUser().getRole().equals("artiste")) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/tunarche/artistFormationView.fxml"));
                Parent root = loader.load();
                Stage stage = (Stage) userbutton.getScene().getWindow(); // Replace `someUiElement` with your actual node, like a button or pane
                Scene scene = new Scene(root);
                stage.setTitle("concours");
                stage.setScene(scene);
                stage.show();
            }
            else{
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/tunarche/formation.fxml"));
                Parent root = loader.load();
                Stage stage = (Stage) userbutton.getScene().getWindow(); // Replace `someUiElement` with your actual node, like a button or pane
                Scene scene = new Scene(root);
                stage.setTitle("formation");
                stage.setScene(scene);
                stage.show();
            }



        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void handelconcours(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/tunarche/afficher_concours_grid.fxml"));
            Parent root = loader.load();

            // Get the current stage from any UI component, e.g., a button
            Stage stage = (Stage) userbutton.getScene().getWindow(); // Replace `someUiElement` with your actual node, like a button or pane

            Scene scene = new Scene(root);
            stage.setTitle("blog");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void handelprofile(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/tunarche/profileparametre.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) userbutton.getScene().getWindow(); // Replace `someUiElement` with your actual node, like a button or pane
            Scene scene = new Scene(root);
            stage.setTitle("profileparametre");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void handelhome(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/tunarche/userfront.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) userbutton.getScene().getWindow(); // Replace `someUiElement` with your actual node, like a button or pane
            Scene scene = new Scene(root);
            stage.setTitle("home");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
