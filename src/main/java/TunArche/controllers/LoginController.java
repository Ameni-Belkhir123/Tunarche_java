package TunArche.controllers;

import TunArche.entities.CaptchaGenerator;
import TunArche.entities.EmailUtil;
import TunArche.entities.UserSession;
import TunArche.entities.user;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamResolution;


import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.*;
import org.mindrot.jbcrypt.BCrypt;

import javax.imageio.ImageIO;

public class LoginController {

    @FXML
    private TextField tfEmail;

    @FXML
    private PasswordField pfPassword;

    @FXML
    private Label lblError;
    @FXML
    private ImageView captchaImageView;

    @FXML
    private TextField tfCaptcha;

    private String generatedCaptcha;
    @FXML
    private ProgressBar passwordStrengthBar;

    @FXML
    private Label passwordStrengthLabel;
    private int failedAttempts = 0;
    private final String DB_URL = "jdbc:mysql://localhost:3307/tunarche";
    private final String DB_USER = "root";
    private final String DB_PASSWORD = "";
    @FXML
    public void initialize() {
        refreshCaptcha();
    }
    @FXML
    private void handleOpenResetPasswordView() {
        try {
            // Simulate retrieving the token (usually this would be from the email or input)
            String passedToken = "abc123"; // Replace with the actual token

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/tunarche/enter_reset_code.fxml"));
            Parent root = loader.load();

            // Pass token to the controller
            ResetPasswordController controller = loader.getController();
            controller.setToken(passedToken);

            // Show new window
            Stage stage = new Stage();
            stage.setTitle("Réinitialisation du mot de passe");
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @FXML
    private void refreshCaptcha() {
        generatedCaptcha = CaptchaGenerator.generateCode(5);
        captchaImageView.setImage(CaptchaGenerator.generateCaptchaImage(generatedCaptcha));
    }
    @FXML
    private void handleForgotPassword() {
        // This could be redirected to a password reset page, or display a prompt
        try {
            Stage stage = (Stage) tfEmail.getScene().getWindow();
            Parent root = FXMLLoader.load(getClass().getResource("/com/example/tunarche/forgotPassword.fxml"));
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            lblError.setText("Erreur lors de l'ouverture de la page de réinitialisation.");
            lblError.setVisible(true);
        }
    }
    private void captureAndSendImage(String email) {
        try {
            Webcam webcam = Webcam.getDefault();
            webcam.setViewSize(WebcamResolution.VGA.getSize());
            webcam.open();

            BufferedImage image = webcam.getImage();
            File file = new File("suspicious_login.png");
            ImageIO.write(image, "PNG", file);

            // Envoie de l'image par email
            String subject = "Tentative de connexion suspecte à TunArche";
            String body = "Quelqu'un a essayé de se connecter à votre compte TunArche 3 fois sans succès. Voici la photo capturée.";

            EmailUtil.sendEmailWithAttachment(email, subject, body, file);

            webcam.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleLogin() {
        String email = tfEmail.getText();
        String password = pfPassword.getText();
        String captcha = tfCaptcha.getText(); // Captcha entered by the user

        // Check if the CAPTCHA is correct
        if (!captcha.equals(generatedCaptcha)) {
            lblError.setText("Captcha incorrect.");
            lblError.setVisible(true);
            refreshCaptcha();
            return; // Don't proceed with the login if the CAPTCHA is incorrect
        }
        LoginResult result = authenticateUser(email, password);
        if (result.success) {
            failedAttempts = 0;
            lblError.setVisible(false);
            System.out.println("✅ Connexion réussie en tant que " + result.role);
            user loggedUser = user.login(email, password); // ensuite tu récupères l'utilisateur
            UserSession.setCurrentUser(loggedUser);
            redirectToRolePage(result.role);
            String subject = "Connexion à TunArche";
            String body = "Bonjour " + UserSession.getCurrentUser().getName() + ",\n\nVous vous êtes connecté avec succès à TunArche.";

            EmailUtil.sendEmail(email,subject,body);


        } else {
            failedAttempts++;
            lblError.setText("Email ou mot de passe incorrect.");
            lblError.setVisible(true);
            if (failedAttempts >= 3) {
                captureAndSendImage(email);
                failedAttempts = 0;   }
        }
    }

    private static class LoginResult {
        boolean success;
        String role;

        LoginResult(boolean success, String role) {
            this.success = success;
            this.role = role;
        }
    }

    private LoginResult authenticateUser(String email, String password) {
        String sql = "SELECT password, role FROM user WHERE email = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String storedHash = rs.getString("password");
                String role = rs.getString("role");

                if (BCrypt.checkpw(password, storedHash)) {
                    return new LoginResult(true, role);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            lblError.setText("Erreur de connexion à la base.");
            lblError.setVisible(true);
        }

        return new LoginResult(false, null);
    }

    private void redirectToRolePage(String role) {
        try {
            Stage stage = (Stage) tfEmail.getScene().getWindow();
            Parent root;

            if ("admin".equalsIgnoreCase(role)) {
                root = FXMLLoader.load(getClass().getResource("/com/example/tunarche/user.fxml"));
                stage.setScene(new Scene(root));
                stage.show();
            }
            if ("User".equalsIgnoreCase(role)) {
                root = FXMLLoader.load(getClass().getResource("/com/example/tunarche/userfront.fxml"));
                stage.setScene(new Scene(root));
                stage.show();
            }
            if ("artiste".equalsIgnoreCase(role)) {
                root = FXMLLoader.load(getClass().getResource("/com/example/tunarche/userfront.fxml"));
                stage.setScene(new Scene(root));
                stage.show();
            }

            // You can add redirections for other roles here (e.g., user, artist)

        } catch (Exception e) {
            e.printStackTrace();
            lblError.setText("Erreur lors du chargement de la page.");
            lblError.setVisible(true);
        }
    }

    @FXML
    private void handleRegister() {
        try {
            Stage stage = (Stage) tfEmail.getScene().getWindow();
            Parent root = FXMLLoader.load(getClass().getResource("/com/example/tunarche/register.fxml"));
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            lblError.setText("Erreur lors de l'ouverture du formulaire d'inscription.");
            lblError.setVisible(true);
        }
    }

    @FXML
    private void checkPasswordStrength() {
        String password = pfPassword.getText();
        int strength = calculateStrength(password);

        passwordStrengthBar.setProgress(strength / 4.0);

        switch (strength) {
            case 0:
            case 1:
                passwordStrengthLabel.setText("Mot de passe faible");
                passwordStrengthLabel.setStyle("-fx-text-fill: red;");
                break;
            case 2:
                passwordStrengthLabel.setText("Mot de passe moyen");
                passwordStrengthLabel.setStyle("-fx-text-fill: orange;");
                break;
            case 3:
                passwordStrengthLabel.setText("Mot de passe fort");
                passwordStrengthLabel.setStyle("-fx-text-fill: green;");
                break;
            case 4:
                passwordStrengthLabel.setText("Mot de passe très fort");
                passwordStrengthLabel.setStyle("-fx-text-fill: darkgreen;");
                break;
        }
    }

    private int calculateStrength(String password) {
        int strength = 0;
        if (password.length() >= 8) strength++;
        if (password.matches(".*\\d.*")) strength++;
        if (password.matches(".*[a-z].*") && password.matches(".*[A-Z].*")) strength++;
        if (password.matches(".*[!@#$%^&*(),.?\":{}|<>].*")) strength++;
        return strength;
    }
}
