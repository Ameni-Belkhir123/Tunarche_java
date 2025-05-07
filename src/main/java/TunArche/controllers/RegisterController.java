package TunArche.controllers;

import TunArche.entities.EmailUtil;
import TunArche.entities.UserSession;
import javafx.animation.KeyValue;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.Scene;
import javafx.scene.Parent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.json.JSONObject;
import org.mindrot.jbcrypt.BCrypt;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.layout.Region;
import javafx.util.Duration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Locale;

public class RegisterController {

    @FXML
    private TextField tfFirstName;

    @FXML
    private TextField tfLastName;

    @FXML
    private TextField tfEmail;

    @FXML
    private PasswordField pfPassword;

    @FXML
    private TextField tfPhone;

    @FXML
    private VBox formContainer;

    @FXML
    private Label lblError;

    @FXML
    private Region backgroundRegion;

    @FXML
    private ProgressBar passwordStrengthBar; // Progress bar for password strength

    @FXML
    private Label passwordStrengthLabel; // Label for password strength text

    private final String DB_URL = "jdbc:mysql://localhost:3307/tunarche";
    private final String DB_USER = "root";
    private final String DB_PASSWORD = "";

    @FXML
    public void initialize() {
        animateBackground();  // Call the background animation
        passwordStrengthLabel.setText("");
        pfPassword.textProperty().addListener((observable, oldValue, newValue) -> updatePasswordStrength(newValue)); // Add listener for password field

        setPhoneCountryCode(); // <-- Ajouté pour pré-remplir le téléphone
    }

    private void setPhoneCountryCode() {

        String countryCode =getCountryDialCode();  // Exemple: "TN", "FR", etc.
System.out.println(countryCode);

        if (countryCode != null) {
            tfPhone.setText(countryCode + " "); // Pré-remplit le champ téléphone
        }
    }
    private String getCountryDialCode() {
        try {
            URL url = new URL("http://ip-api.com/json/");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            JSONObject json = new JSONObject(response.toString());
            String countryCode = json.getString("countryCode");

            // Map some common country codes to their dialing codes
            switch (countryCode) {
                case "TN": return "+216"; // Tunisia
                case "FR": return "+33";  // France
                case "US": return "+1";   // United States
                case "DE": return "+49";  // Germany
                // add more as needed
                default: return "+1";     // fallback if country not mapped
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return "+1"; // fallback in case of error
    }



    @FXML
    private void animateBackground() {
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.ZERO,
                        new KeyValue(backgroundRegion.styleProperty(), "-fx-background-color: linear-gradient(to right, #74ebd5, #ACB6E5);")),
                new KeyFrame(Duration.seconds(5),
                        new KeyValue(backgroundRegion.styleProperty(), "-fx-background-color: linear-gradient(to right, #f2709c, #ff9472;")),
        new KeyFrame(Duration.seconds(10),
                new KeyValue(backgroundRegion.styleProperty(), "-fx-background-color: linear-gradient(to right, #74ebd5, #ACB6E5);"))
    );
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.setAutoReverse(true);
        timeline.play();
    }


    private void updatePasswordStrength(String password) {
        double strength = calculatePasswordStrength(password);

        passwordStrengthBar.setProgress(strength);

        if (strength < 0.3) {
            passwordStrengthBar.setStyle("-fx-accent: red;");
            passwordStrengthLabel.setText("Mot de passe faible");
            passwordStrengthLabel.setStyle("-fx-text-fill: red;");
        } else if (strength < 0.6) {
            passwordStrengthBar.setStyle("-fx-accent: orange;");
            passwordStrengthLabel.setText("Mot de passe moyen");
            passwordStrengthLabel.setStyle("-fx-text-fill: orange;");
        } else {
            passwordStrengthBar.setStyle("-fx-accent: green;");
            passwordStrengthLabel.setText("Mot de passe fort");
            passwordStrengthLabel.setStyle("-fx-text-fill: green;");
        }
    }

    private double calculatePasswordStrength(String password) {
        int length = password.length();
        int score = 0;

        if (length >= 8) score++;
        if (password.matches(".*[A-Z].*")) score++;
        if (password.matches(".*[a-z].*")) score++;
        if (password.matches(".*\\d.*")) score++;
        if (password.matches(".*[!@#\\$%\\^&\\*].*")) score++;

        return score / 5.0;
    }

    @FXML
    private void handleRegister() {
        String firstName = tfFirstName.getText();
        String lastName = tfLastName.getText();
        String email = tfEmail.getText();
        String password = pfPassword.getText();
        String phone = tfPhone.getText();

        if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || password.isEmpty() || phone.isEmpty()) {
            lblError.setText("Veuillez remplir tous les champs.");
            lblError.setVisible(true);
            return;
        }

        if (!isValidEmail(email)) {
            lblError.setText("Veuillez entrer un email valide.");
            lblError.setVisible(true);
            return;
        }

        if (!isValidPhone(phone)) {
            lblError.setText("Veuillez entrer un numéro de téléphone valide.");
            lblError.setVisible(true);
            return;
        }

        if (isEmailTaken(email)) {
            lblError.setText("Cet email est déjà utilisé.");
            lblError.setVisible(true);
            return;
        }

        double strength = calculatePasswordStrength(password);
        if (strength < 0.6) {
            lblError.setText("Le mot de passe est trop faible.");
            lblError.setVisible(true);
            return;
        }

        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());

        String sql = "INSERT INTO user (name, last_name, email, password, phone, role, is_verified) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, firstName);
            stmt.setString(2, lastName);
            stmt.setString(3, email);
            stmt.setString(4, hashedPassword);
            stmt.setString(5, phone);
            stmt.setString(6, "User");
            stmt.setBoolean(7, false);

            int rowsInserted = stmt.executeUpdate();
            if (rowsInserted > 0) {
                lblError.setText("✅ Inscription réussie !");
                String subject = "Bienvenue sur TunArche !";
                String body = "Bonjour " + firstName + ",\n\n" +
                        "Votre compte a été créé avec succès sur TunArche.\n\n" +
                        "Nous sommes ravis de vous compter parmi nous !\n\n" +
                        "Cordialement,\nL’équipe TunArche";
                System.out.println("Envoi de mail à " + email);
                EmailUtil.sendEmail(email, subject, body);
                System.out.println("Mail envoyé (ou tenté)");
                lblError.setStyle("-fx-text-fill: green;");
                lblError.setVisible(true);

                redirectToLogin();
            }

        } catch (SQLException e) {
            lblError.setText("Erreur lors de l'inscription.");
            lblError.setVisible(true);
            e.printStackTrace();
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

    private boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        Pattern pattern = Pattern.compile(emailRegex);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    private boolean isValidPhone(String phone) {
        String phoneRegex = "^\\+\\d{1,3}\\s\\d{8}$"; // Exemple: +216 12345678
        Pattern pattern = Pattern.compile(phoneRegex);
        Matcher matcher = pattern.matcher(phone);
        return matcher.matches();
    }

    private boolean isEmailTaken(String email) {
        String sql = "SELECT COUNT(*) FROM user WHERE email = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email);
            ResultSet resultSet = stmt.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
