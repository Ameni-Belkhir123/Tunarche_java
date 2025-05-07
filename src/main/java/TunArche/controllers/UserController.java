package TunArche.controllers;

import TunArche.entities.PdfGenerator;
import TunArche.entities.UserSession;
import TunArche.entities.user;
import TunArche.services.userimpl;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.element.Paragraph;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.shape.Rectangle;
import javafx.animation.FillTransition;
import javafx.util.Duration;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;

import com.itextpdf.layout.Document;
import org.json.JSONObject;


import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.*;


public class UserController {
    private final String DB_URL = "jdbc:mysql://localhost:3307/tunarche";
    private final String DB_USER = "root";
    private final String DB_PASSWORD = "";
    @FXML
    private BorderPane rootPane;

    @FXML
    private TextField tfSearch; // 🔍 champ de recherche

    private FilteredList<user> filteredData;
    @FXML
    private Button eventbutton;
    @FXML
    private Button btnBillets;
    @FXML
    private TableView<user> tableUser;

    @FXML
    private Label strengthLabel;

    @FXML
    private TableColumn<user, Void> colActions;

    @FXML
    private TextField tfName, tfLastName, tfEmail, tfPassword, tfPhone;
    @FXML
    private CheckBox chkVerified;

    private final userimpl userService = new userimpl();
    private ObservableList<user> userList;

    @FXML
    private ProgressBar strengthBar;
    @FXML
    private MenuButton avatarMenu;
    @FXML
    private MenuItem menuItemUser;

    @FXML
    public void initialize() {
        setPhoneCountryCode();
        if (UserSession.getCurrentUser() != null) {
            String fullName = UserSession.getCurrentUser().getName() + " " + UserSession.getCurrentUser().getLastName();
            menuItemUser.setText("👤 " + fullName);
        } else {
            String fullName = UserSession.getCurrentUser().getName() + " " + UserSession.getCurrentUser().getLastName();
            menuItemUser.setText("👤 " + fullName);
        }
        setupTableColumns();
        loadUsers();
        addActionButtonsToTable();
        setupPasswordStrengthListener();
        strengthLabel.setText("");
        tfPassword.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                updatePasswordStrength(newValue);
            }
        });
        tableUser.getSelectionModel().selectedItemProperty().addListener((obs, oldUser, selectedUser) -> populateFields(selectedUser));

        // Optional: Background Color Animation for Aesthetic
        Rectangle background = new Rectangle();
        background.widthProperty().bind(rootPane.widthProperty());
        background.heightProperty().bind(rootPane.heightProperty());
        background.setFill(Color.LIGHTBLUE);

        FillTransition ft = new FillTransition(Duration.seconds(5), background, Color.LIGHTBLUE, Color.LIGHTPINK);
        ft.setCycleCount(FillTransition.INDEFINITE);
        ft.setAutoReverse(true);
        ft.play();

        rootPane.getChildren().add(0, background); // Add background behind all content
        //  setupAnimatedBackground();

    }

    @FXML
    private void handleLogout() {
        UserSession.clear(); // Efface l'utilisateur connecté
        Stage stage = (Stage) tfEmail.getScene().getWindow();
        Parent root;
        try {
            root = FXMLLoader.load(getClass().getResource("/com/example/tunarche/login.fxml"));
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setupAnimatedBackground() {
        // Correct file path for your MP4 video
        URL videoUrl = getClass().getResource("/videos/background.mp4"); // Adjust the path accordingly

        if (videoUrl == null) {
            System.out.println("Video file not found.");
            return;
        }

        // Create the media and media player
        Media media = new Media(videoUrl.toExternalForm());
        MediaPlayer mediaPlayer = new MediaPlayer(media);

        // Set the media player to play looped
        mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
        mediaPlayer.setAutoPlay(true);

        // Create a MediaView to display the video
        MediaView mediaView = new MediaView(mediaPlayer);

        // Add the MediaView directly to the rootPane (the main container of the scene)
        rootPane.setCenter(mediaView);  // Add MediaView to the center of the BorderPane

        // Adjust size if necessary
        mediaView.setFitWidth(rootPane.getWidth());  // Fit to the width of the rootPane
        mediaView.setFitHeight(rootPane.getHeight()); // Fit to the height of the rootPane

        // Optional: Set a listener to adjust the video size dynamically if the window is resized
        rootPane.widthProperty().addListener((obs, oldWidth, newWidth) -> {
            mediaView.setFitWidth(newWidth.doubleValue());
        });

        rootPane.heightProperty().addListener((obs, oldHeight, newHeight) -> {
            mediaView.setFitHeight(newHeight.doubleValue());
        });
    }


    private void setupPasswordStrengthListener() {
        tfPassword.textProperty().addListener((observable, oldValue, newValue) -> {
            updatePasswordStrength(newValue);
        });
    }

    private double calculatePasswordStrength(String password) {
        int strength = 0;

        if (password.length() > 8) strength += 2;
        if (password.length() > 12) strength += 2;
        if (password.matches(".*[A-Z].*")) strength += 1;
        if (password.matches(".*[a-z].*")) strength += 1;
        if (password.matches(".*\\d.*")) strength += 1;
        if (password.matches(".*[!@#$%^&*].*")) strength += 1;

        return strength / 8.0;
    }

    private void updatePasswordStrength(String password) {
        double strength = calculatePasswordStrength(password);
        strengthLabel.setText("");
        strengthBar.setProgress(strength);

        if (strength < 0.3) {
            strengthBar.setStyle("-fx-accent: red;");
            strengthLabel.setText("Mot de passe faible");
            strengthLabel.setStyle("-fx-text-fill: red;");
        } else if (strength < 0.6) {
            strengthBar.setStyle("-fx-accent: orange;");
            strengthLabel.setText("Mot de passe moyen");
            strengthLabel.setStyle("-fx-text-fill: orange;");
        } else {
            strengthBar.setStyle("-fx-accent: green;");
            strengthLabel.setText("Mot de passe fort");
            strengthLabel.setStyle("-fx-text-fill: green;");
        }
    }

    private boolean isStrongPassword(String password) {
        return password.length() >= 8 && password.matches(".*[A-Z].*") && password.matches(".*[0-9].*");
    }

    private boolean isMediumPassword(String password) {
        return password.length() >= 6 && password.matches(".*[a-z].*") && password.matches(".*[A-Z].*");
    }

    private void setupTableColumns() {
        TableColumn<user, String> nameCol = new TableColumn<>("Nom");
        nameCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getName()));

        TableColumn<user, String> lastNameCol = new TableColumn<>("Prénom");
        lastNameCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getLastName()));

        TableColumn<user, String> emailCol = new TableColumn<>("Email");
        emailCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getEmail()));

        TableColumn<user, String> phoneCol = new TableColumn<>("Téléphone");
        phoneCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getPhone()));

        TableColumn<user, String> roleCol = new TableColumn<>("Role");
        roleCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getRole()));

        TableColumn<user, String> is_verifiedCol = new TableColumn<>("Vérifié");
        is_verifiedCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().isVerified() ? "Oui" : "Non"));

        colActions = new TableColumn<>("Actions");

        tableUser.getColumns().addAll(nameCol, lastNameCol, emailCol, phoneCol, roleCol, is_verifiedCol, colActions);
    }

    private boolean isEmailExists(String email) {
        return userService.isEmailExists(email);
    }

    @FXML
    public void handleSearch() {
        String searchText = tfSearch.getText().toLowerCase();

        filteredData.setPredicate(user -> {
            if (searchText == null || searchText.isEmpty()) return true;

            return user.getName().toLowerCase().contains(searchText)
                    || user.getEmail().toLowerCase().contains(searchText)
                    || user.getPhone().toLowerCase().contains(searchText);
        });
    }

    @FXML
    public void loadUsers() {
        userList = FXCollections.observableArrayList(userService.showAll());
        tableUser.setItems(userList);
        filteredData = new FilteredList<>(userList, p -> true);
        tableUser.setItems(filteredData);
    }

    private void addActionButtonsToTable() {
        Callback<TableColumn<user, Void>, TableCell<user, Void>> cellFactory = new Callback<>() {
            @Override
            public TableCell<user, Void> call(final TableColumn<user, Void> param) {
                return new TableCell<>() {
                    private final Button btnDelete = new Button("🗑️");
                    private final Button btnMakeAdmin = new Button("👑");
                    private final Button btnGeneratePDF = new Button("📄");

                    {
                        btnDelete.setStyle("-fx-background-color: red; -fx-text-fill: white;");
                        btnMakeAdmin.setStyle("-fx-background-color: green; -fx-text-fill: white;");
                        btnGeneratePDF.setStyle("-fx-background-color: #007bff; -fx-text-fill: white;");

                        btnDelete.setOnAction(e -> {
                            user u = getTableView().getItems().get(getIndex());
                            deleteUser(u);
                        });

                        btnMakeAdmin.setOnAction(e -> {
                            user u = getTableView().getItems().get(getIndex());
                            promoteToAdmin(u);
                        });
                        btnGeneratePDF.setOnAction(e -> {
                            user u = getTableView().getItems().get(getIndex());
                            PdfGenerator.generateUserCV(u); // 👈 Méthode à créer
                        });
                    }

                    @Override
                    protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            user u = getTableView().getItems().get(getIndex());
                            HBox buttons = new HBox(10, btnDelete, btnGeneratePDF);
                            if (!"Admin".equalsIgnoreCase(u.getRole())) {
                                buttons.getChildren().add(btnMakeAdmin);
                            }
                            setGraphic(buttons);
                        }
                    }
                };
            }
        };
        colActions.setCellFactory(cellFactory);
    }

    private void generateUserPDF(user u) {
        try {
            File pdfFile = new File("CV_" + u.getName() + "_" + u.getLastName() + ".pdf");
            PdfWriter writer = new PdfWriter(pdfFile);
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document document = new Document(pdfDoc);

            document.add(new Paragraph("CV de " + u.getName() + " " + u.getLastName()).setBold().setFontSize(18));
            document.add(new Paragraph("Email : " + u.getEmail()));
            document.add(new Paragraph("Téléphone : " + u.getPhone()));
            document.add(new Paragraph("Rôle : " + u.getRole()));
            document.add(new Paragraph("Statut vérifié : " + (u.isVerified() ? "Oui" : "Non")));

            document.close();
            System.out.println("✅ PDF généré : " + pdfFile.getAbsolutePath());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private boolean showConfirmation(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText("Confirmation requise");
        alert.setContentText(message);

        ButtonType buttonYes = new ButtonType("Oui", ButtonBar.ButtonData.YES);
        ButtonType buttonNo = new ButtonType("Non", ButtonBar.ButtonData.NO);
        alert.getButtonTypes().setAll(buttonYes, buttonNo);

        return alert.showAndWait().filter(response -> response == buttonYes).isPresent();
    }

    private void showAlert(String title, String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText("Information");
        alert.setContentText(msg);
        alert.showAndWait();
    }

    private void showError(String title, String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText("Une erreur est survenue");
        alert.setContentText(msg);
        alert.showAndWait();
    }

    private void deleteUser(user u) {
        if (!showConfirmation("Confirmation", "Voulez-vous vraiment supprimer cet utilisateur ?")) return;
        userService.delete(u.getId());
        loadUsers();
        clearForm();
    }

    private void promoteToAdmin(user u) {
        if (!showConfirmation("Confirmation", "Voulez-vous promouvoir cet utilisateur en tant qu'admin ?")) return;
        u.setRole("Admin");
        userService.update(u);
        loadUsers();
    }

    private void setPhoneCountryCode() {

        String countryCode = getCountryDialCode();  // Exemple: "TN", "FR", etc.
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
                case "TN":
                    return "+216"; // Tunisia
                case "FR":
                    return "+33";  // France
                case "US":
                    return "+1";   // United States
                case "DE":
                    return "+49";  // Germany
                // add more as needed
                default:
                    return "+1";     // fallback if country not mapped
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return "+1"; // fallback in case of error
    }

    @FXML
    public void handleAdd() {
        if (!validateForm()) return;

        String email = tfEmail.getText().trim();

        if (isEmailExists(email)) {
            showAlert("Erreur", "Cet email est déjà utilisé.");
            return;
        }

        try {
            user newUser = getUserFromForm(null);
            userService.create(newUser);
            loadUsers();
            clearForm();
        } catch (Exception e) {
            showError("Erreur", "Erreur lors de l'ajout : " + e.getMessage());
        }
    }

    @FXML
    public void handleUpdate() {
        user selected = tableUser.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Erreur", "Aucun utilisateur sélectionné.");
            return;
        }
        if (!validateFormapp()) return;
        try {
            user updated = getUserFromForm(selected.getId());
            userService.update(updated);
            loadUsers();
            clearForm();
        } catch (Exception e) {
            showError("Erreur", "Erreur lors de la mise à jour : " + e.getMessage());
        }
    }

    @FXML
    public void handleDelete() {
        user selected = tableUser.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Erreur", "Aucun utilisateur sélectionné.");
            return;
        }

        deleteUser(selected);
    }

    @FXML
    public void handleMakeAdmin() {
        user selected = tableUser.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Erreur", "Aucun utilisateur sélectionné.");
            return;
        }

        if ("Admin".equalsIgnoreCase(selected.getRole())) {
            showAlert("Info", "L'utilisateur est déjà un admin.");
            return;
        }

        promoteToAdmin(selected);
        showAlert("Succès", "L'utilisateur est maintenant un admin.");
    }

    private user getUserFromForm(Integer id) {
        return new user(
                id,
                tfName.getText().trim(),
                tfLastName.getText().trim(),
                tfEmail.getText().trim(),
                tfPhone.getText().trim(),
                tfPassword.getText().trim(),
                "User",
                false,
                null,
                null
        );
    }

    private void populateFields(user selectedUser) {
        if (selectedUser != null) {
            tfName.setText(selectedUser.getName());
            tfLastName.setText(selectedUser.getLastName());
            tfEmail.setText(selectedUser.getEmail());
            tfPhone.setText(selectedUser.getPhone());
            tfPassword.clear();
        }
    }

    private void clearForm() {
        tfName.clear();
        tfLastName.clear();
        tfEmail.clear();
        tfPhone.clear();
        tfPassword.clear();
        strengthLabel.setText("");
    }

    private boolean validateFormapp() {
        String name = tfName.getText().trim();
        String lastName = tfLastName.getText().trim();
        String email = tfEmail.getText().trim();
        String phone = tfPhone.getText().trim();

        if (name.isEmpty() || lastName.isEmpty() || email.isEmpty() || phone.isEmpty()) {
            showAlert("Erreur de saisie", "Tous les champs doivent être remplis.");
            return false;
        }

        if (!name.matches("[a-zA-ZÀ-ÿ\\s\\-]+")) {
            showAlert("Erreur de saisie", "Le nom ne doit contenir que des lettres.");
            return false;
        }

        if (!lastName.matches("[a-zA-ZÀ-ÿ\\s\\-]+")) {
            showAlert("Erreur de saisie", "Le prénom ne doit contenir que des lettres.");
            return false;
        }

        if (!email.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            showAlert("Erreur de saisie", "Email invalide.");
            return false;
        }

        if (!phone.matches("\\d{8,8}")) {
            showAlert("Erreur de saisie", "Le numéro de téléphone est invalide.");
            return false;
        }

        return true;
    }

    private boolean validateForm() {
        String name = tfName.getText().trim();
        String lastName = tfLastName.getText().trim();
        String email = tfEmail.getText().trim();
        String phone = tfPhone.getText().trim();

        if (name.isEmpty() || lastName.isEmpty() || email.isEmpty() || phone.isEmpty()) {
            showAlert("Erreur de saisie", "Tous les champs doivent être remplis.");
            return false;
        }

        if (!name.matches("[a-zA-ZÀ-ÿ\\s\\-]+")) {
            showAlert("Erreur de saisie", "Le nom ne doit contenir que des lettres.");
            return false;
        }

        if (!lastName.matches("[a-zA-ZÀ-ÿ\\s\\-]+")) {
            showAlert("Erreur de saisie", "Le prénom ne doit contenir que des lettres.");
            return false;
        }

        if (!email.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            showAlert("Erreur de saisie", "Email invalide.");
            return false;
        }

        if (isEmailTaken(email)) {
            showAlert("Erreur de saisie", "Email exists.");
            return false;
        }

        if (!phone.matches("\\d{8,8}")) {
            showAlert("Erreur de saisie", "Le numéro de téléphone est invalide.");
            return false;
        }

        return true;
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

    @FXML
    public void handleformation(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/tunarche/addFormation.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) tableUser.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            showAlert("Navigation Error", "Failed to load Event Management interface: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    public void handelevent() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/tunarche/event.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) eventbutton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            showAlert("Navigation Error", "Failed to load Event Management interface: " + e.getMessage());
            e.printStackTrace();
        }

    }

    @FXML
    public void handebillet() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/tunarche/billet.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) btnBillets.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            showAlert("Navigation Error", "Failed to load Event Management interface: " + e.getMessage());
            e.printStackTrace();
        }

    }

    @FXML
    public void handlePublication() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/tunarche/PublicationForm.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) btnBillets.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            showAlert("Navigation Error", "Failed to load Event Management interface: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    public void handleCommantaire() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/tunarche/commantaire.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) btnBillets.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            showAlert("Navigation Error", "Failed to load Event Management interface: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void handeluser(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/tunarche/user.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) btnBillets.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            showAlert("Navigation Error", "Failed to load Event Management interface: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void handleevaluation(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/tunarche/evaluations.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) btnBillets.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            showAlert("Navigation Error", "Failed to load Event Management interface: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void handelconcours(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/tunarche/AfficherConcoursAdmin.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) btnBillets.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            showAlert("Navigation Error", "Failed to load Event Management interface: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void handelparticipation(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/tunarche/AfficherParticipationAdmin.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) btnBillets.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            showAlert("Navigation Error", "Failed to load Event Management interface: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

