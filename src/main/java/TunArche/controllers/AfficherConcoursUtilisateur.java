package TunArche.controllers;

import TunArche.entities.*;
import TunArche.services.Concourslmpl;
import TunArche.services.Participationlmpl;
import TunArche.services.VoteImpl;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class AfficherConcoursUtilisateur {

    @FXML
    private StackPane mainContent;
    @FXML
    private Button userbutton;

    private final VoteImpl voteService = new VoteImpl();

    @FXML
    private FlowPane concoursContainer;

    @FXML
    private VBox detailsPane;

    @FXML
    private Label titreLabel;

    @FXML
    private Label descriptionLabel;

    @FXML
    private Label datesLabel;

    @FXML
    private TextField searchField;
    @FXML
    private Button retourButton;

    @FXML
    private FlowPane participationContainer;

    @FXML
    private Button participerButton;

    private Concours selectedConcours;
    private ObservableList<Concours> concoursList;
    private FilteredList<Concours> filteredConcours;

    private final Concourslmpl concoursService = new Concourslmpl();
    private final Participationlmpl participationService = new Participationlmpl();

    private String userRole;
    @FXML
    private MenuItem menuItemUser;
    public void setUserRole(String role) {
        this.userRole = role;
    }

    @FXML
    private VBox artistePane;

    @FXML
    private VBox utilisateurPane;
    @FXML
    private Button btnShowResults;
    @FXML
    private Label jokeLabel; // Label pour afficher la blague


    @FXML
    public void initialize() {
        // Vérifier si l'utilisateur est connecté
        if (UserSession.getCurrentUser() != null) {
            String fullName = UserSession.getCurrentUser().getName() + " " + UserSession.getCurrentUser().getLastName();
            menuItemUser.setText("👤 " + fullName);

            // Récupérer le rôle de l'utilisateur pour afficher la bonne section
            String role = UserSession.getCurrentUser().getRole().toLowerCase();
            this.userRole = role;
            System.out.println("role: " + role);
        } else {
            // Handle the case where no user is logged in
            menuItemUser.setText("👤 Invité");
            System.err.println("⚠️ Aucun utilisateur connecté !");
            redirectToLogin();
        }

        // Charger et filtrer les concours
        List<Concours> concoursFromDb = concoursService.showAll();
        concoursList = FXCollections.observableArrayList(concoursFromDb);
        filteredConcours = new FilteredList<>(concoursList, p -> true);

        // Ajout d'un listener pour filtrer les concours
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

        // Action buttons container
        HBox actionButtons = new HBox(10);
        actionButtons.setAlignment(Pos.CENTER);
        actionButtons.setPadding(new Insets(5, 0, 0, 0));

        // Card click handler (for details)
        card.setOnMouseEntered(e -> card.setStyle("-fx-background-color: #e0ffe0; -fx-border-color: green; -fx-border-radius: 8; -fx-background-radius: 8; -fx-cursor: hand;"));
        card.setOnMouseExited(e -> card.setStyle("-fx-background-color: #ffffff; -fx-border-color: #ccc; -fx-border-radius: 8; -fx-background-radius: 8;"));
        card.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> redirectToDetails(concours));

        // Add "View" button for all users
        Button viewBtn = new Button("Voir");
        viewBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-background-radius: 4; -fx-font-size: 11px; -fx-padding: 5 10;");
        viewBtn.setOnAction(e -> redirectToDetails(concours));
        actionButtons.getChildren().add(viewBtn);

        // Add "Results" button only for users with "user" role
        if (UserSession.getCurrentUser() != null &&
                UserSession.getCurrentUser().getRole().equals("User")) {
            Button resultsBtn = new Button("Résultats");
            resultsBtn.setStyle("-fx-background-color: #9b59b6; -fx-text-fill: white; -fx-background-radius: 4; -fx-font-size: 11px; -fx-padding: 5 10;");
            resultsBtn.setOnAction(e -> showResults(concours));
            actionButtons.getChildren().add(resultsBtn);
        }

        card.getChildren().addAll(title, desc, dates, actionButtons);
        return card;
    }
    // Add this new method to show results for a specific concours
    private void showResults(Concours concours) {
        // Check if user has "user" role before allowing access
        if (!UserSession.getCurrentUser().getRole().equals("User")) {
            showAlert(Alert.AlertType.WARNING, "Accès refusé",
                    "Accès refusé",
                    "Seuls les utilisateurs avec le rôle 'user' peuvent voir les résultats.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/tunarche/results_view.fxml"));
            Parent root = loader.load();

            // Get the controller and pass the selected concours
            ResultsViewController controller = loader.getController();
            controller.setConcours(concours);

            // Show in current window
            Stage stage = (Stage) mainContent.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur",
                    "Impossible d'afficher les résultats",
                    "Une erreur est survenue : " + e.getMessage());
        }
    }
    private void redirectToDetails(Concours concours) {
        this.selectedConcours = concours;

        // Masquer les conteneurs de la vue des cartes
        concoursContainer.setVisible(false);
        concoursContainer.setManaged(false);

        // Configurer l'affichage pour tous les rôles
        titreLabel.setText(concours.getTitre());
        descriptionLabel.setText(concours.getDescription());

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String datesText = "Du " + concours.getDateDebut().format(formatter) + " au " + concours.getDateFin().format(formatter);
        datesLabel.setText(datesText);

        // Vérifier le rôle de l'utilisateur pour l'affichage approprié
        String role = UserSession.getCurrentUser().getRole();

        if (role.equals("artiste")) {
            // Pour les artistes: afficher le détail du concours avec bouton Participer
            detailsPane.setVisible(true);
            detailsPane.setManaged(true);

            // Afficher la section artiste et masquer celle de l'utilisateur
            artistePane.setVisible(true);
            artistePane.setManaged(true);
            utilisateurPane.setVisible(false);
            utilisateurPane.setManaged(false);

            // S'assurer que le bouton Participer est visible
            participerButton.setVisible(true);
            participerButton.setManaged(true);

        } else if (role.equals("User")) {
            // Pour les utilisateurs: afficher directement les œuvres pour voter
            detailsPane.setVisible(false);
            detailsPane.setManaged(false);

            // Afficher la section utilisateur et masquer celle de l'artiste
            utilisateurPane.setVisible(true);
            utilisateurPane.setManaged(true);
            artistePane.setVisible(false);
            artistePane.setManaged(false);

            // Masquer le bouton Participer pour les utilisateurs normaux
            participerButton.setVisible(false);
            participerButton.setManaged(false);

            // Charger les participations pour ce concours
            afficherParticipations(concours.getId());
        }
    }

    // Modify afficherParticipations method to disable vote buttons for participations the user has already voted for
    private void afficherParticipations(int concoursId) {
        participationContainer.getChildren().clear();

        // Get current user ID
        int currentUserId = UserSession.getCurrentUser().getId();

        // Récupérer toutes les participations pour ce concours
        List<Participation> participations = participationService.showByConcours(concoursId);
        System.out.println("Nombre de participations: " + participations.size());

        if (participations.isEmpty()) {
            Label emptyLabel = new Label("Aucune participation pour ce concours pour le moment.");
            emptyLabel.setStyle("-fx-font-style: italic; -fx-text-fill: gray;");
            participationContainer.getChildren().add(emptyLabel);
            return;
        }

        // Afficher chaque participation avec bouton de vote
        for (Participation p : participations) {
            VBox box = new VBox(5);
            box.setPadding(new Insets(10));
            box.setAlignment(Pos.CENTER);
            box.setPrefWidth(200);
            box.setStyle("-fx-border-color: #ccc; -fx-border-radius: 5; -fx-background-radius: 5; -fx-background-color: #f9f9f9;");

            ImageView imgView = new ImageView();
            imgView.setFitWidth(150);
            imgView.setFitHeight(150);
            imgView.setPreserveRatio(true);

            try {
                File file = new File(p.getImagePath());
                if (file.exists()) {
                    imgView.setImage(new Image(new FileInputStream(file)));
                } else {
                    // Image par défaut si l'image n'est pas trouvée
                    imgView.setStyle("-fx-background-color: #eee;");
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            Label nom = new Label("Artiste: " + p.getNom_artiste());
            nom.setStyle("-fx-font-weight: bold;");

            Label votes = new Label("Votes: " + p.getNbr_votes());

            Button voteBtn = new Button("Voter");
            voteBtn.setId("voteBtn_" + p.getId());
            voteBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-background-radius: 4;");
            voteBtn.setOnAction(e -> handleVote(p));

            // Disable the button if the user has already voted for this participation
            if (voteService.hasUserVoted(currentUserId, p.getId())) {
                voteBtn.setDisable(true);
                voteBtn.setText("Voté");
                voteBtn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white;");
            }

            box.getChildren().addAll(imgView, nom, votes, voteBtn);
            participationContainer.getChildren().add(box);
        }
    }

    @FXML
    private void handleVote(Participation participation) {
        // Get the current user id from session
        int currentUserId = UserSession.getCurrentUser().getId();

        // Check if user has already voted for this participation
        if (voteService.hasUserVoted(currentUserId, participation.getId())) {
            showAlert(Alert.AlertType.WARNING, "Vote déjà effectué",
                    "Vous avez déjà voté pour cette participation",
                    "Un utilisateur ne peut voter qu'une seule fois pour chaque participation.");
            return;
        }

        // Create and save the vote
        Vote vote = new Vote();
        vote.setUser_id(currentUserId);
        vote.setParticipation_id(participation.getId());
        vote.setConcours_id(selectedConcours.getId());

        voteService.save(vote);

        // The vote service will automatically update the participation vote count
        // but we still need to update the UI

        // Disable the vote button for this participation
        Button voteBtn = (Button) participationContainer.lookup("#voteBtn_" + participation.getId());
        if (voteBtn != null) {
            voteBtn.setDisable(true);
            voteBtn.setText("Voté");
            voteBtn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white;");
        }

        // Refresh the participation list to show updated vote counts
        afficherParticipations(selectedConcours.getId());
    }

    // Add this helper method to display alerts
    private void showAlert(Alert.AlertType alertType, String title, String headerText, String contentText) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);
        alert.showAndWait();
    }
    @FXML
    private void handleRetour() {
        detailsPane.setVisible(false);
        detailsPane.setManaged(false);

        concoursContainer.setVisible(true);
        concoursContainer.setManaged(true);
    }

    @FXML
    private void handleParticiper() {
        if (selectedConcours != null) {
            // Vérifier que l'utilisateur est un artiste avant de lui permettre de participer
            if (UserSession.getCurrentUser().getRole().equals("artiste")) {
                showParticipationForm(selectedConcours);
            } else {
                System.out.println("Seuls les artistes peuvent participer aux concours!");
                // Vous pourriez ajouter une alerte ici
            }
        } else {
            System.out.println("Aucun concours sélectionné !");
        }
    }

    @FXML
    private void handleconcours() {
        Stage stage = (Stage) retourButton.getScene().getWindow();
        Parent root;
        try {
            root = FXMLLoader.load(getClass().getResource("/com/example/tunarche/afficher_concours_grid.fxml"));
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    private void handleShowResults() {
        // Check if user has "user" role before allowing access
        if (!UserSession.getCurrentUser().getRole().equals("User")) {
            showAlert(Alert.AlertType.WARNING, "Accès refusé",
                    "Accès refusé",
                    "Seuls les utilisateurs avec le rôle 'user' peuvent voir les résultats.");
            return;
        }

        if (selectedConcours == null) {
            // If no contest is selected, show the first contest or display a message
            if (!concoursList.isEmpty()) {
                selectedConcours = concoursList.get(0);
            } else {
                showAlert(Alert.AlertType.INFORMATION, "Aucun concours",
                        "Aucun concours disponible",
                        "Il n'y a pas de concours à afficher.");
                return;
            }
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/tunarche/results_view.fxml"));
            Parent root = loader.load();

            // Get the controller and pass the selected concours
            ResultsViewController controller = loader.getController();
            controller.setConcours(selectedConcours);

            // Show in current window
            Stage stage = (Stage) btnShowResults.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur",
                    "Impossible d'afficher les résultats",
                    "Une erreur est survenue : " + e.getMessage());
        }
    }

    private void showParticipationForm(Concours concours) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/tunarche/FormParticipation.fxml"));
            Parent root = loader.load();

            ParticipationFormController controller = loader.getController();
            controller.setConcours(concours);

            Stage stage = new Stage();
            stage.setTitle("Formulaire de participation");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Erreur lors de l'ouverture du formulaire de participation : " + e.getMessage());
        }
    }

    private String getShortDescription(String text, int maxLength) {
        return text.length() > maxLength ? text.substring(0, maxLength) + "..." : text;
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
            Stage stage = (Stage) userbutton.getScene().getWindow();

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

    @FXML
    public void handelconcours(ActionEvent actionEvent) {
        if (UserSession.getCurrentUser() == null) {
            redirectToLogin();
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/tunarche/afficher_concours_grid.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Concours");
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException("Erreur lors du chargement de la vue des concours", e);
        }
    }

    private void redirectToLogin() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/tunarche/login.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) mainContent.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Connexion");
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException("Erreur lors du chargement de la page de connexion", e);
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