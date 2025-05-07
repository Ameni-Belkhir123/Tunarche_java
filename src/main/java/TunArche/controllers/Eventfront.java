package TunArche.controllers;

import TunArche.entities.Event;
import TunArche.entities.PdfGenerator;
import TunArche.entities.UserSession;
import TunArche.services.EventImpl;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.control.Button;
import javafx.scene.Cursor;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import javafx.geometry.Insets;
import javafx.stage.Stage;

public class Eventfront {

    @FXML
    private VBox eventContainer;
    @FXML
    private Button btn;
    @FXML
    private MenuItem menuItemUser;
    @FXML
    private Button eventbutton;
    @FXML
    private Button userbutton;
    public void initialize() {
        if (UserSession.getCurrentUser() != null) {
            String fullName = UserSession.getCurrentUser().getName() + " " + UserSession.getCurrentUser().getLastName();
            menuItemUser.setText("👤 " + fullName);


        } else {
            menuItemUser.setText(UserSession.getCurrentUser().getName() + " " + UserSession.getCurrentUser().getLastName());
            System.err.println("⚠️ Aucun utilisateur connecté !");
        }
        EventImpl eventService = new EventImpl();
        List<Event> events = eventService.getAll();

        for (Event event : events) {
            StackPane card = createEventCard(event);
            eventContainer.getChildren().add(card);
        }
    }

    private StackPane createEventCard(Event event) {
        StackPane cardContainer = new StackPane();
        cardContainer.setPrefWidth(750);

        // Chargement sécurisé de l'image depuis les ressources
        URL imageUrl = getClass().getResource("/images/affiche.jpg");
        if (imageUrl == null) {
            System.out.println("Error: Image not found at /images/affiche.jpg");
        } else {
            Image image = new Image(imageUrl.toExternalForm(), 750, 200, false, true);
            BackgroundImage bgImage = new BackgroundImage(
                    image,
                    BackgroundRepeat.NO_REPEAT,
                    BackgroundRepeat.NO_REPEAT,
                    BackgroundPosition.CENTER,
                    new BackgroundSize(750, 200, false, false, false, false)
            );
            cardContainer.setBackground(new Background(bgImage));
        }

        cardContainer.setStyle("-fx-background-radius: 15; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 10, 0, 0, 4);");

        VBox overlay = new VBox(10);
        overlay.setPadding(new Insets(15));
        overlay.setStyle(
                "-fx-background-color: rgba(0,0,0,0.5);" +
                        "-fx-background-radius: 15;"
        );

        Label nameLabel = new Label("🎤 " + event.getNameEvent());
        nameLabel.setFont(new Font("Arial Black", 20));
        nameLabel.setTextFill(Color.WHITE);

        Label placeLabel = new Label("📍 Lieu : " + event.getPlaceEvent());
        placeLabel.setTextFill(Color.WHITE);

        Label dateLabel = new Label("📅 Du " + formatDate(event.getDateStart()) + " au " + formatDate(event.getDateEnd()));
        dateLabel.setTextFill(Color.WHITE);

        Label priceLabel = new Label("💸 Prix : " + event.getPrice() + " DT");
        priceLabel.setTextFill(Color.WHITE);

        Label ticketsLabel = new Label("🎟️ Tickets : " + event.getSoldTickets() + "/" + event.getTotalTickets());
        ticketsLabel.setTextFill(Color.WHITE);

        Label descLabel = new Label("📝 " + event.getDescription());
        descLabel.setWrapText(true);
        descLabel.setTextFill(Color.WHITE);

        Button participateBtn = new Button("🎫 Participer");
        participateBtn.setStyle("-fx-background-color: #ffffff; -fx-text-fill: #0083b0; -fx-font-weight: bold; -fx-background-radius: 10;");
        participateBtn.setCursor(Cursor.HAND);
        participateBtn.setOnAction(e -> openBilletForm(event));
        if (event.getSoldTickets() >= event.getTotalTickets()) { // Fixed condition
            participateBtn.setDisable(true);
            participateBtn.setText("❌ Épuisé");
        }

        overlay.getChildren().addAll(nameLabel, placeLabel, dateLabel, priceLabel, ticketsLabel, descLabel, participateBtn);
        cardContainer.getChildren().add(overlay);

        return cardContainer;
    }

    private void openBilletForm(Event event) {
        if (event.getSoldTickets() >= event.getTotalTickets()) { // Fixed condition
            showAlert("Tous les billets sont vendus !");
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/tunarche/billetForm.fxml"));
            Parent root = loader.load();

            // Passer l’événement au contrôleur du formulaire
            BilletFormController controller = loader.getController();
            controller.setEvent(event);

            Stage stage = new Stage();
            stage.setTitle("Réserver un Billet");
            stage.setScene(new Scene(root));

            stage.setOnHidden(e -> {
                event.setSoldTickets(event.getSoldTickets() + 1); // Increment sold tickets
                EventImpl eventService = new EventImpl();
                eventService.updateSoldTickets(event.getId(), event.getSoldTickets());

                refreshEvents(); // Recharger d'abord

                if (event.getSoldTickets() >= event.getTotalTickets()) { // Fixed condition
                    // ✅ Important : Utiliser Platform.runLater pour éviter l'exception
                    Platform.runLater(() -> {
                        showAlert("✅ Félicitations, dernier billet réservé !");
                    });
                }
            });

            stage.show();
        } catch (IOException ex) {
            ex.printStackTrace();
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

    private void refreshEvents() {
        eventContainer.getChildren().clear(); // Vider les anciennes cartes
        EventImpl eventService = new EventImpl();
        List<Event> events = eventService.getAll(); // Recharger les events

        for (Event event : events) {
            StackPane card = createEventCard(event);
            eventContainer.getChildren().add(card);
        }
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
    private void showAlert(String message) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private String formatDate(LocalDate date) {
        if (date == null) {
            return "N/A";
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return date.format(formatter);
    }
    @FXML
    private void handlecv()
    {

        PdfGenerator.generateUserCV(UserSession.getCurrentUser());
        String f2="C:\\Users\\marwe\\Desktop\\Tun-Arche\\cv_"+UserSession.getCurrentUser().getName()+"_"+UserSession.getCurrentUser().getLastName()+".pdf";
        PdfGenerator.openPdfFile(f2);
    }
    @FXML
    private void handleLogout() {
        UserSession.clear(); // Efface l'utilisateur connecté
        Stage stage = (Stage) btn.getScene().getWindow();
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