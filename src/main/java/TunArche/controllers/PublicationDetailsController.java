package TunArche.controllers;

import TunArche.entities.PdfGenerator;
import TunArche.entities.Publication;
import TunArche.entities.UserSession;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

public class PublicationDetailsController {
    public static final String ACCOUNT_SID = "AC6ad6b5eedb60972714c7a73c62a64dac";
    public static final String AUTH_TOKEN = "00120fbf91aed5f7b009121ecfa3c98b";
    public static final String FROM_PHONE = "+19704064359"; // Numéro Twilio
    public void envoyerSms(String to, String body) {
        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
        Message message = Message.creator(
                new PhoneNumber(to),
                new PhoneNumber(FROM_PHONE),
                body
        ).create();

        System.out.println("📱 SMS envoyé à " + to + " : " + body);
    }
    private Publication publication;

    @FXML private Label titreLabel;
    @FXML private Label descriptionLabel;
    @FXML private ImageView publicationImage;
    @FXML private Label dateLabel;
    @FXML private Label likeLabel;
    @FXML private Label unlikeLabel;

    @FXML private Button likeButton;
    @FXML private Button unlikeButton;
    @FXML
    private MenuItem menuItemUser;
    @FXML private Button commenterBoutton;

    @FXML
    public void initialize() {
        if (UserSession.getCurrentUser() != null) {
            String fullName = UserSession.getCurrentUser().getName() + " " + UserSession.getCurrentUser().getLastName();
            menuItemUser.setText("👤 " + fullName);


        } else {
            menuItemUser.setText(UserSession.getCurrentUser().getName() + " " + UserSession.getCurrentUser().getLastName());
            System.err.println("⚠️ Aucun utilisateur connecté !");
        }}
    public void setPublication(Publication publication) {
        this.publication = publication;
        System.out.println("🧩 Publication reçue : ID = " + publication.getId());

        // Affichage des détails
        titreLabel.setText(publication.getTitre());
        descriptionLabel.setText(publication.getDescription());
        dateLabel.setText("Date : " + publication.getDate_act().toString());
        likeLabel.setText("Likes: " + publication.getLikes());
        unlikeLabel.setText("Unlikes: " + publication.getUnlikes());


        File imageFile = new File(publication.getImage());
        if (imageFile.exists()) {
            publicationImage.setImage(new Image(imageFile.toURI().toString()));
        } else {
            System.out.println("⚠️ Image introuvable : " + publication.getImage());
        }
    }

    @FXML
    private void handleLike() {
        publication.setLikes(publication.getLikes() + 1);
        likeLabel.setText("Likes: " + publication.getLikes());
        System.out.println("👍 Like ajouté !");
        incrementLikes(publication);
        // 👉 Si le nombre de likes atteint 100 : envoyer un SMS
        if (publication.getLikes() == 10) {
            String message = "🎉 Bravo ! Votre publication \"" + publication.getTitre() + "\" a atteint 10 likes !";
            String numeroAuteur = "+21623873156"; // à adapter (tu peux le lier à l'auteur plus tard)
            envoyerSms(numeroAuteur, message);
        }
    }

    @FXML
    private void handleUnlike() {
        publication.setUnlikes(publication.getUnlikes() + 1);
        unlikeLabel.setText("Unlikes: " + publication.getUnlikes());
        System.out.println("👎 Unlike ajouté !");
        incrementUnlikes(publication);

    }

    @FXML
    private void handleReadMore() {
        if (publication == null) {
            System.out.println("❌ Publication est null, impossible de continuer.");
            return;
        }

        try {
            System.out.println("➡️ Navigation vers publication.fxml avec ID = " + publication.getId());

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/tunarche/publication.fxml"));
            Parent root = loader.load();

            PublicationController publicationController = loader.getController();
            publicationController.setSelectedPublicationId(publication.getId());

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Détail complet de la publication");
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public void incrementLikes(Publication publication) {
        String sql = "UPDATE publication SET likes = likes + 1 WHERE id = ?";

        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3307/tunarche", "root", "");
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setInt(1, publication.getId());
            int rows = stmt.executeUpdate();

            if (rows > 0) {
                System.out.println("👍 Likes incrémenté pour la publication ID = " + publication.getId());
            } else {
                System.out.println("❌ Aucun enregistrement mis à jour. Vérifie l'ID.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void incrementUnlikes(Publication publication) {
        String sql = "UPDATE publication SET unlikes = unlikes + 1 WHERE id = ?";

        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3307/tunarche", "root", "");
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setInt(1, publication.getId());
            int rows = stmt.executeUpdate();

            if (rows > 0) {
                System.out.println("👍 Unlikes incrémenté pour la publication ID = " + publication.getId());
            } else {
                System.out.println("❌ Aucun enregistrement mis à jour. Vérifie l'ID.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    @FXML
    private void handlecv()
    {

        PdfGenerator.generateUserCV(UserSession.getCurrentUser());
        String f2="C:\\Users\\marwe\\Desktop\\Tun-Arche\\cv_"+UserSession.getCurrentUser().getName()+"_"+UserSession.getCurrentUser().getLastName()+".pdf";
        PdfGenerator.openPdfFile(f2);
    }
    @FXML
    private Button eventbutton;
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
        Stage stage = (Stage) eventbutton.getScene().getWindow();
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
            Stage stage = (Stage) eventbutton.getScene().getWindow(); // Replace `someUiElement` with your actual node, like a button or pane

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
            Stage stage = (Stage) eventbutton.getScene().getWindow(); // Replace `someUiElement` with your actual node, like a button or pane

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
                Stage stage = (Stage) eventbutton.getScene().getWindow(); // Replace `someUiElement` with your actual node, like a button or pane
                Scene scene = new Scene(root);
                stage.setTitle("concours");
                stage.setScene(scene);
                stage.show();
            }
            else{
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/tunarche/formation.fxml"));
                Parent root = loader.load();
                Stage stage = (Stage) eventbutton.getScene().getWindow(); // Replace `someUiElement` with your actual node, like a button or pane
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
            Stage stage = (Stage) eventbutton.getScene().getWindow(); // Replace `someUiElement` with your actual node, like a button or pane

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

            Stage stage = (Stage) eventbutton.getScene().getWindow(); // Replace `someUiElement` with your actual node, like a button or pane
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

            Stage stage = (Stage) eventbutton.getScene().getWindow(); // Replace `someUiElement` with your actual node, like a button or pane
            Scene scene = new Scene(root);
            stage.setTitle("home");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
