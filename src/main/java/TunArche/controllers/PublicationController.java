package TunArche.controllers;

import TunArche.entities.PdfGenerator;
import TunArche.entities.UserSession;
import javafx.animation.FadeTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.sql.*;

public class PublicationController {

    @FXML private Pane sentimentCard;
    @FXML private Label sentimentLabel;
    @FXML private Label errorLabel;
    @FXML private TextArea commentField;
    @FXML private ListView<String> commentaireListView;

    private ObservableList<String> commentaireList = FXCollections.observableArrayList();
    private int selectedPublicationId = -1;

    public void setSelectedPublicationId(int id) {
        this.selectedPublicationId = id;
        loadCommentaires();
    }

    private Connection connect() {
        try {
            return DriverManager.getConnection("jdbc:mysql://localhost:3307/tunarche", "root", "");
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void loadCommentaires() {
        commentaireList.clear();
        Connection conn = connect();
        if (conn == null) {
            errorLabel.setText("Erreur de connexion à la base de données.");
            return;
        }

        try (PreparedStatement ps = conn.prepareStatement("SELECT contenu FROM commantaire WHERE id_pub_id = ?")) {
            ps.setInt(1, selectedPublicationId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                commentaireList.add(rs.getString("contenu"));
            }
            commentaireListView.setItems(commentaireList);
        } catch (SQLException e) {
            e.printStackTrace();
            errorLabel.setText("Erreur lors du chargement des commentaires.");
        }
    }

    @FXML
    private void initialize() {
        if (UserSession.getCurrentUser() != null) {
            String fullName = UserSession.getCurrentUser().getName() + " " + UserSession.getCurrentUser().getLastName();
            menuItemUser.setText("👤 " + fullName);


        } else {
            menuItemUser.setText(UserSession.getCurrentUser().getName() + " " + UserSession.getCurrentUser().getLastName());
            System.err.println("⚠️ Aucun utilisateur connecté !");
        }
        commentaireListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                commentField.setText(newVal);
            }
        });
    }

    private boolean validerCommentaire(String contenu) {
        if (contenu.isEmpty()) {
            errorLabel.setText("Le champ commentaire est vide.");
            return false;
        }
        if (Character.isDigit(contenu.charAt(0))) {
            errorLabel.setText("Le commentaire ne doit pas commencer par un chiffre.");
            return false;
        }
        if (contenu.length() < 4) {
            errorLabel.setText("Le commentaire doit contenir au moins 4 caractères.");
            return false;
        }
        return true;
    }

    @FXML
    private void addCommentaire() {
        String contenu = commentField.getText();

        if (!validerCommentaire(contenu)) return;

        try (Connection conn = connect();
             PreparedStatement ps = conn.prepareStatement("INSERT INTO commantaire (id_pub_id,user_id,contenu,likes,date) VALUES (?, ?,?,?,?)")) {
            ps.setInt(1, selectedPublicationId);
            ps.setInt(2, 1); // utilisateur fictif
            ps.setString(3, contenu);
            ps.setInt(4, 0);
            ps.setDate(5, new java.sql.Date(System.currentTimeMillis()));
            int rows = ps.executeUpdate();

            if (rows > 0) {
                loadCommentaires();
                commentField.clear();
                errorLabel.setText("Commentaire ajouté avec succès !");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            errorLabel.setText("Erreur lors de l'ajout du commentaire.");
        }
    }

    @FXML
    private void updateCommentaire() {
        String contenu = commentField.getText();
        String selectedComment = commentaireListView.getSelectionModel().getSelectedItem();

        if (selectedComment == null) {
            errorLabel.setText("Veuillez sélectionner un commentaire à modifier.");
            return;
        }
        if (!validerCommentaire(contenu)) return;

        try (Connection conn = connect();
             PreparedStatement ps = conn.prepareStatement("UPDATE commantaire SET contenu = ? WHERE contenu = ? AND id_pub_id = ?")) {
            ps.setString(1, contenu);
            ps.setString(2, selectedComment);
            ps.setInt(3, selectedPublicationId);
            ps.executeUpdate();
            loadCommentaires();
            commentField.clear();
            errorLabel.setText("Commentaire modifié avec succès !");
        } catch (SQLException e) {
            e.printStackTrace();
            errorLabel.setText("Erreur lors de la modification.");
        }
    }

    @FXML
    private void deleteCommentaire() {
        String selectedComment = commentaireListView.getSelectionModel().getSelectedItem();

        if (selectedComment == null) {
            errorLabel.setText("Veuillez sélectionner un commentaire à supprimer.");
            return;
        }

        try (Connection conn = connect();
             PreparedStatement ps = conn.prepareStatement("DELETE FROM commantaire WHERE contenu = ? AND id_pub_id = ?")) {
            ps.setString(1, selectedComment);
            ps.setInt(2, selectedPublicationId);
            ps.executeUpdate();
            loadCommentaires();
            commentField.clear();
            errorLabel.setText("Commentaire supprimé avec succès !");
        } catch (SQLException e) {
            e.printStackTrace();
            errorLabel.setText("Erreur lors de la suppression.");
        }
    }

    @FXML
    private void readComment() {
        String selectedComment = commentaireListView.getSelectionModel().getSelectedItem();

        if (selectedComment == null || selectedComment.trim().isEmpty()) {
            errorLabel.setText("Veuillez sélectionner un commentaire à lire.");
            return;
        }

        try {
            String apiKey = "257b2e9922af4c6188587483a2faa219";
            String encodedComment = java.net.URLEncoder.encode(selectedComment, "UTF-8");
            String url = "https://api.voicerss.org/?key=" + apiKey + "&hl=fr-fr&src=" + encodedComment;

            java.awt.Desktop.getDesktop().browse(new java.net.URI(url));
        } catch (Exception e) {
            e.printStackTrace();
            errorLabel.setText("Erreur lors de la lecture du commentaire.");
        }
    }

    @FXML
    private void displaySentiment() {
        String commentaire = commentField.getText().trim();
        if (commentaire.isEmpty()) {
            errorLabel.setText("Veuillez saisir un commentaire pour l'analyse.");
            return;
        }

        String sentiment = analyserSentiment(commentaire);
        afficherSentimentStylisé(sentiment);
    }

    private String analyserSentiment(String texte) {
        texte = texte.toLowerCase();
        if (texte.contains("bien") || texte.contains("super") || texte.contains("merci")) return "positif 😊";
        if (texte.contains("nul") || texte.contains("déteste") || texte.contains("horrible")) return "négatif 😠";
        return "neutre 😐";
    }

    private void afficherSentimentStylisé(String sentiment) {
        sentimentCard.setVisible(true);

        FadeTransition ft = new FadeTransition(Duration.millis(600), sentimentCard);
        ft.setFromValue(0.0);
        ft.setToValue(1.0);
        ft.play();

        String cardStyle;
        String labelText;

        switch (sentiment) {
            case "positif 😊":
                cardStyle = "-fx-background-color: linear-gradient(to right, #00b09b, #96c93d);" +
                        "-fx-background-radius: 20; -fx-padding: 20;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 10, 0, 0, 5);";
                labelText = "Sentiment positif 😊";
                break;
            case "négatif 😠":
                cardStyle = "-fx-background-color: linear-gradient(to right, #ff416c, #ff4b2b);" +
                        "-fx-background-radius: 20; -fx-padding: 20;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 10, 0, 0, 5);";
                labelText = "Sentiment négatif 😠";
                break;
            default:
                cardStyle = "-fx-background-color: linear-gradient(to right, #bdc3c7, #2c3e50);" +
                        "-fx-background-radius: 20; -fx-padding: 20;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 10, 0, 0, 5);";
                labelText = "Sentiment neutre 😐";
                break;
        }

        sentimentCard.setStyle(cardStyle);
        sentimentLabel.setText(labelText);
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
    private MenuItem menuItemUser;
    @FXML
    private Button eventbutton;
    @FXML
    private Button userbutton;

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
