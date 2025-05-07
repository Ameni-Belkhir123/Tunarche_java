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

    @FXML
    private Pagination pagination;



    private FormationImpl formationService = new FormationImpl();
    private ObservableList<Formation> observableFormations;
    private FilteredList<Formation> filteredFormations;

    private static final int ITEMS_PER_PAGE = 6;

    @FXML
    public void initialize() {
        if (UserSession.getCurrentUser() != null) {
            String fullName = UserSession.getCurrentUser().getName() + " " + UserSession.getCurrentUser().getLastName();
            menuItemUser.setText("👤 " + fullName);


        } else {
            menuItemUser.setText(UserSession.getCurrentUser().getName() + " " + UserSession.getCurrentUser().getLastName());
            System.err.println("⚠️ Aucun utilisateur connecté !");
        }
        List<Formation> allFormations = formationService.showAll();
        observableFormations = FXCollections.observableArrayList(allFormations);
        filteredFormations = new FilteredList<>(observableFormations, f -> true);

        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredFormations.setPredicate(formation -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                String lowerCaseFilter = newValue.toLowerCase();
                return formation.getTitre().toLowerCase().contains(lowerCaseFilter)
                        || formation.getDescription().toLowerCase().contains(lowerCaseFilter);
            });
            updatePagination();
        });

        updatePagination();
    }

    private void updatePagination() {
        int pageCount = (int) Math.ceil((double) filteredFormations.size() / ITEMS_PER_PAGE);
        pagination.setPageCount(pageCount > 0 ? pageCount : 1);
        pagination.setCurrentPageIndex(0);
        pagination.setPageFactory(this::createPage);
    }

    private VBox createPage(int pageIndex) {
        int fromIndex = pageIndex * ITEMS_PER_PAGE;
        int toIndex = Math.min(fromIndex + ITEMS_PER_PAGE, filteredFormations.size());
        List<Formation> pageItems = filteredFormations.subList(fromIndex, toIndex);
        displayFormations(pageItems);
        return new VBox(); // Retourne un nœud vide requis par la pagination
    }

    private void displayFormations(List<Formation> formations) {
        formationGrid.getChildren().clear();
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

        ImageView imageView = new ImageView();
        imageView.setFitWidth(200);
        imageView.setFitHeight(120);

        File imageFile = new File("src/main/resources/images/" + formation.getImage_name());
        if (imageFile.exists()) {
            imageView.setImage(new Image(imageFile.toURI().toString()));
        } else {
            System.out.println("⚠️ Image introuvable : " + formation.getImage_name());
        }

        Label title = new Label(formation.getTitre());
        title.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");
        title.setWrapText(true);

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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/tunarche/displayFormations.fxml"));
            Parent root = loader.load();

            FormationDetailsController controller = loader.getController();
            controller.setFormation(formation);

            Stage stage = new Stage();
            stage.setTitle("Détails de la formation");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getShortDescription(String text, int maxLength) {
        return text.length() > maxLength ? text.substring(0, maxLength) + "..." : text;
    }
    @FXML
    private MenuItem menuItemUser;
    @FXML
    private Button eventbutton;
    @FXML
    private Button userbutton;
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