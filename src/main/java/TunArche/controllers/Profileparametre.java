package TunArche.controllers;

import TunArche.entities.PdfGenerator;
import TunArche.entities.UserSession;
import TunArche.entities.user;
import TunArche.tools.MyConnection;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class Profileparametre {

    @FXML private Button userbutton;
    @FXML private Button eventbutton;
    @FXML private MenuButton avatarMenu1;
    @FXML private MenuItem menuItemUser;
    @FXML private MenuItem btnLogout;
    @FXML private TextField nameField;
    @FXML private TextField lastNameField;
    @FXML private TextField emailField;
    @FXML private TextField phoneField;
    @FXML private Button updateButton;
    @FXML private Label updateMessage;
    @FXML private TextField tfEmail;
    private user currentUser;
    @FXML
    public void initialize() {
        if (UserSession.getCurrentUser() != null) {
            String fullName = UserSession.getCurrentUser().getName() + " " + UserSession.getCurrentUser().getLastName();
            menuItemUser.setText("👤 " + fullName);


        } else {
            menuItemUser.setText(UserSession.getCurrentUser().getName() + " " + UserSession.getCurrentUser().getLastName());
            System.err.println("⚠️ Aucun utilisateur connecté !");
        }
        loadCurrentUser();
        if (currentUser != null) {
            nameField.setText(currentUser.getName() != null ? currentUser.getName() : "");
            lastNameField.setText(currentUser.getLastName() != null ? currentUser.getLastName() : "");
            emailField.setText(currentUser.getEmail() != null ? currentUser.getEmail() : "");
            phoneField.setText(currentUser.getPhone() != null ? currentUser.getPhone() : "");
        } else {
            updateMessage.setText("Erreur : Aucun utilisateur connecté.");
            updateMessage.setStyle("-fx-text-fill: red;");
            updateMessage.setVisible(true);
        }
    }

    private void loadCurrentUser() {
        // Retrieve the current user from UserSession
        currentUser = UserSession.getCurrentUser();
        if (currentUser == null) {
            updateMessage.setText("Erreur : Aucun utilisateur connecté. Veuillez vous connecter.");
            updateMessage.setStyle("-fx-text-fill: red;");
            updateMessage.setVisible(true);
            // Optionally redirect to login page
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/tunarche/login.fxml"));
                Parent root = loader.load();
                Stage stage = (Stage) nameField.getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
                updateMessage.setText("Erreur lors de la redirection vers la page de connexion.");
                updateMessage.setStyle("-fx-text-fill: red;");
                updateMessage.setVisible(true);
            }
        }
    }

    @FXML
    private void handleUpdate() {
        if (currentUser == null) {
            updateMessage.setText("Erreur : Aucun utilisateur connecté.");
            updateMessage.setStyle("-fx-text-fill: red;");
            updateMessage.setVisible(true);
            return;
        }

        String newName = nameField.getText().trim();
        String newLastName = lastNameField.getText().trim();
        String newEmail = emailField.getText().trim();
        String newPhone = phoneField.getText().trim();

        // Basic validation
        if (newName.isEmpty() || newLastName.isEmpty() || newEmail.isEmpty() || newPhone.isEmpty()) {
            updateMessage.setText("Veuillez remplir tous les champs.");
            updateMessage.setStyle("-fx-text-fill: red;");
            updateMessage.setVisible(true);
            return;
        }

        // Validate email format (basic regex)
        if (!newEmail.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            updateMessage.setText("Format d'email invalide.");
            updateMessage.setStyle("-fx-text-fill: red;");
            updateMessage.setVisible(true);
            return;
        }

        // Check if the new email already exists in the database (excluding the current user)
        if (isEmailAlreadyExists(newEmail)) {
            updateMessage.setText("Cet email est déjà utilisé par un autre utilisateur.");
            updateMessage.setStyle("-fx-text-fill: red;");
            updateMessage.setVisible(true);
            return;
        }

        // Validate phone (basic check for digits and length, adjust as needed)
        if (!newPhone.matches("\\d{8,15}")) {
            updateMessage.setText("Format de téléphone invalide (8-15 chiffres).");
            updateMessage.setStyle("-fx-text-fill: red;");
            updateMessage.setVisible(true);
            return;
        }

        // Update only the shown fields in the database
        Connection conn = MyConnection.getInstance().getCnx();
        String sql = "UPDATE user SET name = ?, last_name = ?, email = ?, phone = ? WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, newName);
            stmt.setString(2, newLastName);
            stmt.setString(3, newEmail);
            stmt.setString(4, newPhone);
            stmt.setInt(5, currentUser.getId());

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                // Reload the full user object to preserve unchanged fields
                reloadUser();
                // Update the UserSession with the updated user
                UserSession.setCurrentUser(currentUser);
                updateMessage.setText("Profil mis à jour avec succès !");
                updateMessage.setStyle("-fx-text-fill: green;");
                updateMessage.setVisible(true);
            } else {
                updateMessage.setText("Erreur lors de la mise à jour du profil.");
                updateMessage.setStyle("-fx-text-fill: red;");
                updateMessage.setVisible(true);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            updateMessage.setText("Erreur lors de la mise à jour : " + e.getMessage());
            updateMessage.setStyle("-fx-text-fill: red;");
            updateMessage.setVisible(true);
        }
    }

    private boolean isEmailAlreadyExists(String newEmail) {
        Connection conn = MyConnection.getInstance().getCnx();
        String sql = "SELECT COUNT(*) FROM user WHERE email = ? AND id != ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, newEmail);
            stmt.setInt(2, currentUser.getId());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0; // Returns true if the email exists for another user
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false; // Default to false if there’s an error
    }
    private void reloadUser() {
        // Reload the full user object from the database to preserve unchanged fields
        Connection conn = MyConnection.getInstance().getCnx();
        String sql = "SELECT id, name, last_name, email, phone, password, role, is_verified, verification_token, code_sent_at FROM user WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, currentUser.getId());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                currentUser = new user(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("last_name"),
                        rs.getString("email"),
                        rs.getString("phone"),
                        rs.getString("password"),
                        rs.getString("role"),
                        rs.getBoolean("is_verified"),
                        rs.getString("verification_token"),
                        rs.getTimestamp("code_sent_at")
                );
                // Update form fields to reflect any changes
                nameField.setText(currentUser.getName() != null ? currentUser.getName() : "");
                lastNameField.setText(currentUser.getLastName() != null ? currentUser.getLastName() : "");
                emailField.setText(currentUser.getEmail() != null ? currentUser.getEmail() : "");
                phoneField.setText(currentUser.getPhone() != null ? currentUser.getPhone() : "");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            updateMessage.setText("Erreur lors du rechargement des données utilisateur.");
            updateMessage.setStyle("-fx-text-fill: red;");
            updateMessage.setVisible(true);
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

            Stage stage = (Stage) userbutton.getScene().getWindow(); // Replace `someUiElement` with your actual node, like a button or pane
            Scene scene = new Scene(root);
            stage.setTitle("concours");
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
