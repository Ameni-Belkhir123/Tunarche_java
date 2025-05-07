package TunArche.controllers;

import TunArche.entities.user;
import TunArche.services.userimpl;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import org.mindrot.jbcrypt.BCrypt;

public class ResetPasswordController {

    @FXML private PasswordField tfNewPassword;
    @FXML private PasswordField tfConfirmPassword;
    @FXML private Label lblMessage;
    @FXML
    private Label lblError;

    private String userEmail;

    public void setUserEmail(String email) {
        this.userEmail = email;
    }
    private String token;

    public void setToken(String token) {
        this.token = token;
    }


    @FXML
    private void handlePasswordChange() {
        String newPassword = tfNewPassword.getText();
        String confirmPassword = tfConfirmPassword.getText();

        if (newPassword.isEmpty() || confirmPassword.isEmpty()) {
            lblMessage.setText("Veuillez remplir les deux champs.");
            lblMessage.setVisible(true);
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            lblMessage.setText("Les mots de passe ne correspondent pas.");
            lblMessage.setVisible(true);
            return;
        }

        // Optionnel : vérifier la force du mot de passe ici

        boolean success = updateUserPassword(userEmail, newPassword);

        if (success) {
            lblMessage.setText("Mot de passe réinitialisé avec succès.");
            lblMessage.setStyle("-fx-text-fill: green;");
            lblMessage.setVisible(true);
            redirectToLogin();

            // Fermer la fenêtre après un petit délai ou message
            Stage stage = (Stage) tfNewPassword.getScene().getWindow();
            stage.close();
        } else {
            lblMessage.setText("Erreur lors de la mise à jour.");
            lblMessage.setVisible(true);
        }
    }
    private void redirectToLogin() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/tunarche/login.fxml"));
            Parent loginRoot = loader.load();

            Scene loginScene = new Scene(loginRoot);
            Stage stage = (Stage) lblError.getScene().getWindow();
            stage.setScene(loginScene);
            stage.setTitle("Login - TunArche");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean updateUserPassword(String email, String newPassword) {
        try {
            userimpl userDAO = new userimpl();
            String hashedPassword = BCrypt.hashpw(newPassword, BCrypt.gensalt());
            return userDAO.updatePasswordByEmail(email, hashedPassword);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

}
