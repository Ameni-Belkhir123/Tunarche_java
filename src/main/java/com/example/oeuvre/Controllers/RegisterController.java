package com.example.oeuvre.Controllers;

import com.example.oeuvre.Entities.User;
import com.example.oeuvre.DAOs.UserDAO;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import java.io.IOException;
import java.sql.SQLException;

public class RegisterController {
    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private TextField emailField;

    @FXML
    private Label usernameErrorLabel;

    @FXML
    private Label passwordErrorLabel;

    @FXML
    private Label emailErrorLabel;

    @FXML
    private javafx.scene.control.Button registerButton;

    private final UserDAO userDAO = new UserDAO();
    private boolean isUsernameValid = false;
    private boolean isPasswordValid = false;
    private boolean isEmailValid = false;

    @FXML
    public void initialize() {
        // Initialize error labels to be invisible
        if (usernameErrorLabel != null) usernameErrorLabel.setVisible(false);
        if (passwordErrorLabel != null) passwordErrorLabel.setVisible(false);
        if (emailErrorLabel != null) emailErrorLabel.setVisible(false);

        // Add listeners to the text fields for real-time validation
        if (usernameField != null) usernameField.textProperty().addListener((observable, oldValue, newValue) -> validateUsername(newValue));
        if (passwordField != null) passwordField.textProperty().addListener((observable, oldValue, newValue) -> validatePassword(newValue));
        if (emailField != null) emailField.textProperty().addListener((observable, oldValue, newValue) -> validateEmail(newValue));

        // Disable the register button initially
        if (registerButton != null) registerButton.setDisable(true);
    }

    @FXML
    public void handleRegister() {
        String username = (usernameField != null) ? usernameField.getText() : "";
        String password = (passwordField != null) ? passwordField.getText() : "";
        String email = (emailField != null) ? emailField.getText() : "";

        if (username.isEmpty() || password.isEmpty() || email.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Veuillez remplir tous les champs.");
            return;
        }

        if (!isUsernameValid || !isPasswordValid || !isEmailValid) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Veuillez corriger les erreurs de saisie.");
            return;
        }

        try {
            if (userDAO.getUserByUsername(username) != null) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Ce nom d'utilisateur est déjà pris.");
                return;
            }

            User newUser = new User();
            newUser.setUsername(username);
            newUser.setPassword(password);
            newUser.setEmail(email);

            userDAO.registerUser(newUser);

            showAlert(Alert.AlertType.INFORMATION, "Succès", "Inscription réussie ! Vous pouvez maintenant vous connecter.");

            // Rediriger vers la page de connexion
            switchToLogin();

        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur SQL", "Erreur de base de données : " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur inattendue : " + e.getMessage());
            e.printStackTrace();
        }

    }

    @FXML
    private void switchToLogin() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/oeuvre/LoginView.fxml"));
            Stage stage = new Stage();
            stage.setScene(new Scene(loader.load()));
            stage.setTitle("Connexion");
            stage.show();
            // Fermer la fenêtre d'inscription actuelle
            Stage currentStage = (Stage) (usernameField != null ? usernameField.getScene().getWindow() : passwordField.getScene().getWindow());
            if (currentStage != null) {
                currentStage.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de charger la page de connexion.");
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void handleLoginFromRegister() {
        switchToLogin();
    }

    @FXML
    private void handleEnterKey(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER && registerButton != null && !registerButton.isDisabled()) {
            handleRegister();
        }
    }

    private void validateUsername(String username) {
        isUsernameValid = !username.trim().isEmpty() && username.length() >= 3;
        if (usernameErrorLabel != null) {
            if (!isUsernameValid) {
                usernameErrorLabel.setText("Le nom d'utilisateur doit contenir au moins 3 caractères.");
                usernameErrorLabel.setVisible(true);
            } else {
                usernameErrorLabel.setVisible(false);
            }
        }
        updateRegisterButtonState();
    }

    private void validatePassword(String password) {
        isPasswordValid = password.length() >= 6;
        if (passwordErrorLabel != null) {
            if (!isPasswordValid) {
                passwordErrorLabel.setText("Le mot de passe doit contenir au moins 6 caractères.");
                passwordErrorLabel.setVisible(true);
            } else {
                passwordErrorLabel.setVisible(false);
            }
        }
        updateRegisterButtonState();
    }

    private void validateEmail(String email) {
        isEmailValid = email.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$");
        if (emailErrorLabel != null) {
            if (!isEmailValid) {
                emailErrorLabel.setText("Veuillez entrer une adresse email valide.");
                emailErrorLabel.setVisible(true);
            } else {
                emailErrorLabel.setVisible(false);
            }
        }
        updateRegisterButtonState();
    }

    private void updateRegisterButtonState() {
        if (registerButton != null) {
            registerButton.setDisable(!(isUsernameValid && isPasswordValid && isEmailValid));
        }
    }
}