package TunArche.controllers;

import TunArche.controllers.PublicationDetailsController;
import TunArche.entities.PdfGenerator;
import TunArche.entities.Publication;
import TunArche.entities.UserSession;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class showPublicationController {
    @FXML
    private MenuItem menuItemUser;
    @FXML
    private Button eventbutton;
    @FXML
    private Button userbutton;
    @FXML
    private GridPane publicationGrid;

    @FXML
    private TextField searchField;

    @FXML
    private Pagination pagination;

    private List<Publication> publicationList = new ArrayList<>();
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
        loadPublications();
        updatePagination(publicationList);

        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            List<Publication> filteredList = filterPublications(newValue);
            updatePagination(filteredList);
        });
    }

    private void loadPublications() {
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3307/tunarche", "root", "");
             PreparedStatement stmt = connection.prepareStatement("SELECT * FROM publication");
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Publication publication = new Publication(
                        rs.getInt("id"),
                        rs.getInt("author_id"),
                        rs.getString("titre"),
                        rs.getString("description"),
                        rs.getString("image"),
                        rs.getInt("likes"),
                        rs.getInt("unlikes"),
                        rs.getInt("rating"),
                        rs.getDate("date_act").toLocalDate()
                );
                publicationList.add(publication);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    private List<Publication> getItemsForPage(int pageIndex, List<Publication> list) {
        int fromIndex = pageIndex * ITEMS_PER_PAGE;
        int toIndex = Math.min(fromIndex + ITEMS_PER_PAGE, list.size());
        return list.subList(fromIndex, toIndex);
    }

    private void displayPublications(List<Publication> publications) {
        publicationGrid.getChildren().clear();
        int col = 0;
        int row = 0;

        for (Publication publication : publications) {
            VBox card = createPublicationCard(publication);
            publicationGrid.add(card, col, row);
            col++;
            if (col > 2) {
                col = 0;
                row++;
            }
        }
    }
    private void updatePagination(List<Publication> list) {
        int pageCount = (int) Math.ceil((double) list.size() / ITEMS_PER_PAGE);
        pagination.setPageCount(pageCount > 0 ? pageCount : 1);
        pagination.setPageFactory(pageIndex -> {
            List<Publication> pageItems = getItemsForPage(pageIndex, list);
            displayPublications(pageItems);
            return new VBox(); // Pagination a besoin d’un nœud de retour
        });
    }

    private VBox createPublicationCard(Publication publication) {
        VBox card = new VBox(8);
        card.setPadding(new Insets(10));
        card.setAlignment(Pos.CENTER);
        card.setPrefWidth(220);
        card.setStyle("-fx-background-color: #ffffff; -fx-border-color: #ccc; -fx-border-radius: 8; -fx-background-radius: 8;");

        ImageView imageView = new ImageView();
        imageView.setFitWidth(200);
        imageView.setFitHeight(120);

        File imageFile = new File(publication.getImage());
        if (!imageFile.exists()) {
            imageFile = new File("src/main/resources/images/" + publication.getImage());
        }

        if (imageFile.exists()) {
            imageView.setImage(new Image(imageFile.toURI().toString()));
        } else {
            System.out.println("⚠️ Image introuvable : " + publication.getImage());
        }

        Label title = new Label(publication.getTitre());
        title.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");
        title.setWrapText(true);

        card.setOnMouseEntered(e -> card.setStyle("-fx-background-color: #e0ffe0; -fx-border-color: green; -fx-border-radius: 8; -fx-background-radius: 8; -fx-cursor: hand;"));
        card.setOnMouseExited(e -> card.setStyle("-fx-background-color: #ffffff; -fx-border-color: #ccc; -fx-border-radius: 8; -fx-background-radius: 8;"));
        card.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> showPublicationDetails(publication));

        card.getChildren().addAll(imageView, title);
        return card;
    }

    private void showPublicationDetails(Publication publication) {
        System.out.println("🟢 Détails de la publication : ID " + publication.getId());
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/tunarche/PublicationDetails.fxml"));
            Parent root = loader.load();

            PublicationDetailsController controller = loader.getController();
            controller.setPublication(publication);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Détails de la publication");
            stage.show();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private List<Publication> filterPublications(String query) {
        if (query == null || query.isEmpty()) {
            return publicationList;
        }
        String lowerCaseQuery = query.toLowerCase();
        List<Publication> filteredList = new ArrayList<>();
        for (Publication publication : publicationList) {
            if (publication.getTitre().toLowerCase().contains(lowerCaseQuery) ||
                    publication.getDescription().toLowerCase().contains(lowerCaseQuery)) {
                filteredList.add(publication);
            }
        }
        return filteredList;
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