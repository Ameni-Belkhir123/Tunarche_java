package TunArche.controllers;

import TunArche.entities.EmailUtil;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;
import java.util.Random;
import java.util.UUID;

public class ForgotPasswordController {

    @FXML
    private TextField tfEmail;

    @FXML
    private Label lblError;

    private final String DB_URL = "jdbc:mysql://localhost:3307/tunarche";
    private final String DB_USER = "root";
    private final String DB_PASSWORD = "";

    @FXML
    private void handlePasswordReset() {
        String email = tfEmail.getText();

        if (email.isEmpty()) {
            lblError.setText("Veuillez entrer votre adresse email.");
            lblError.setVisible(true);
            return;
        }

        boolean emailExists = checkEmailExists(email);

        if (emailExists) {
            String resetCode = String.format("%06d", new Random().nextInt(999999));

            storeResetCodeInDatabase(email, resetCode);

            String subject = "Code de réinitialisation du mot de passe";
            String body = "Bonjour,\n\nVoici votre code de réinitialisation : " + resetCode +
                    "\n\nCe code est valable pendant 1 heure.";

            EmailUtil.sendEmail(email, subject, body);

            lblError.setText("Un code de réinitialisation a été envoyé à votre email.");
            lblError.setVisible(true);

            // Ouvre l'interface de saisie du code
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/tunarche/enter_reset_code.fxml"));
                Parent root = loader.load();

                // Passe l'email au contrôleur suivant
                EnterResetCodeController controller = loader.getController();
                controller.setUserEmail(email);

                Stage stage = new Stage();
                stage.setTitle("Vérification du code");
                stage.setScene(new Scene(root));
                stage.show();

                // (Optionnel) Fermer la fenêtre actuelle
                ((Stage) tfEmail.getScene().getWindow()).close();

            } catch (IOException e) {
                e.printStackTrace();
            }

        } else {
            lblError.setText("Aucun utilisateur trouvé avec cette adresse email.");
            lblError.setVisible(true);
        }
    }

    private void storeResetCodeInDatabase(String email, String resetCode) {
        String sql = "UPDATE user SET reset_token = ?, reset_token_expiration = DATE_ADD(NOW(), INTERVAL 1 HOUR) WHERE email = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, resetCode);
            stmt.setString(2, email);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }



    private boolean checkEmailExists(String email) {
        String sql = "SELECT email FROM user WHERE email = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }


}
