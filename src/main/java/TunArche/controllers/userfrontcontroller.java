package TunArche.controllers;

import TunArche.entities.PdfGenerator;
import TunArche.entities.UserSession;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class userfrontcontroller {
    @FXML
    private MenuButton avatarMenu1;
    @FXML
    private MenuItem menuItemUser;
    @FXML
    private Button eventbutton;
    @FXML
    private Button userbutton;
    @FXML private TextField tfEmail;
    @FXML
    public void initialize() {
        if (UserSession.getCurrentUser() != null) {
            String fullName = UserSession.getCurrentUser().getName() + " " + UserSession.getCurrentUser().getLastName();
            menuItemUser.setText("👤 " + fullName);


        } else {
            menuItemUser.setText(UserSession.getCurrentUser().getName() + " " + UserSession.getCurrentUser().getLastName());
            System.err.println("⚠️ Aucun utilisateur connecté !");
        }}

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
    public void handelconcours() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/tunarche/afficher_concours_grid.fxml"));
            Parent root = loader.load();

            // Get the current stage from any UI component, e.g., a button
            Stage stage = (Stage) userbutton.getScene().getWindow(); // Replace `someUiElement` with your actual node, like a button or pane

            Scene scene = new Scene(root);
            stage.setTitle("concours");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
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
//    @FXML
//    public void handelconcours(ActionEvent actionEvent) {
//        try {
//            // Load the FXML file for the concours view
//            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/tunarche/afficher_concours_grid.fxml"));
//            Parent root = loader.load();
//
//            // Get the current stage from the ActionEvent's source
//            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
//
//            // Set the new scene
//            stage.setScene(new Scene(root));
//            stage.setTitle("Concours");
//            stage.show();
//        } catch (IOException e) {
//            throw new RuntimeException("Erreur lors du chargement de la vue des concours", e);
//        }
//    }
}
