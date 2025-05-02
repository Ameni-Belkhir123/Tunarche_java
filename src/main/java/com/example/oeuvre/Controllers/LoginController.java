package com.example.oeuvre.Controllers;

import com.example.oeuvre.DAOs.UserDAO;
import com.example.oeuvre.Entities.User;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    private final UserDAO userDAO = new UserDAO();

    public static int loggedInUserId;

    @FXML
    public void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            showAlert("Erreur", "Veuillez entrer votre nom d'utilisateur et votre mot de passe.");
            return;
        }

        try {
            User user = userDAO.getUserByUsername(username);
            if (user != null && user.getPassword().equals(password)) {
                loggedInUserId = user.getId(); // Store user ID globally if needed

                // Load UserView.fxml
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/oeuvre/UserView.fxml"));
                Stage stage = new Stage();
                stage.setScene(new Scene(loader.load()));

                // Pass the user ID to UserController
                UserController userController = loader.getController();
                userController.setCurrentUserId(user.getId());

                stage.setTitle("Oeuvre Application");
                stage.show();

                // Close the login window
                ((Stage) usernameField.getScene().getWindow()).close();
            } else {
                showAlert("Erreur", "Nom d'utilisateur ou mot de passe incorrect.");
            }
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de charger la vue principale.");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur", "Une erreur est survenue lors de la tentative de connexion.");
        }
    }

    @FXML
    private void handleRegister() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/oeuvre/RegisterView.fxml"));
            Stage stage = new Stage();
            stage.setScene(new Scene(loader.load()));
            stage.setTitle("Créer un compte");
            stage.show();

            // Close the login window
            ((Stage) usernameField.getScene().getWindow()).close();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de charger la page d'inscription.");
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}