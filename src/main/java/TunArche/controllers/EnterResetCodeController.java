package TunArche.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.sql.*;

public class EnterResetCodeController {

    @FXML
    private TextField tfCode;
    @FXML private Label lblMessage;

    private String userEmail;

    public void setUserEmail(String email) {
        this.userEmail = email;
    }

    @FXML
    private void handleCodeValidation() {
        String code = tfCode.getText();

        if (code.isEmpty()) {
            lblMessage.setText("Veuillez entrer le code.");
            lblMessage.setVisible(true);
            return;
        }

        boolean isValid = checkResetCode(userEmail, code);

        if (isValid) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/tunarche/reset_password.fxml"));
                Parent root = loader.load();

                ResetPasswordController resetController = loader.getController();
                resetController.setUserEmail(userEmail);

                Stage stage = new Stage();
                stage.setTitle("Nouveau mot de passe");
                stage.setScene(new Scene(root));
                stage.show();

                // Fermer cette fenêtre
                ((Stage) tfCode.getScene().getWindow()).close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            lblMessage.setText("Code invalide ou expiré.");
            lblMessage.setVisible(true);
        }
    }

    private boolean checkResetCode(String email, String code) {
        String sql = "SELECT reset_token, reset_token_expiration FROM user WHERE email = ?";
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3307/tunarche", "root", "");
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String storedCode = rs.getString("reset_token");
                java.sql.Timestamp expiresAt = rs.getTimestamp("reset_token_expiration");

                if (storedCode != null && storedCode.equals(code) && expiresAt != null && expiresAt.after(new java.sql.Timestamp(System.currentTimeMillis()))) {
                    // Nettoyer le token après validation réussie
                    String cleanupSql = "UPDATE user SET reset_token = NULL, reset_token_expiration = NULL WHERE email = ?";
                    try (PreparedStatement cleanupStmt = conn.prepareStatement(cleanupSql)) {
                        cleanupStmt.setString(1, email);
                        cleanupStmt.executeUpdate();
                    }
                    return true;
                }
            }
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
