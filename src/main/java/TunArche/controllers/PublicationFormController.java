package TunArche.controllers;

import java.awt.Desktop;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import TunArche.entities.Publication;
import TunArche.entities.UserSession;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import java.awt.Color;


import javafx.animation.ScaleTransition;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.effect.Glow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.stage.FileChooser;



public class PublicationFormController {
    @FXML private TextField titreField;
    @FXML private TextArea descriptionField;
    @FXML private DatePicker datePicker;
    @FXML private ImageView imageView;
    @FXML private Label errorLabel;
    @FXML private TableView<Publication> publicationTable;
    @FXML private TextField searchField;
    @FXML private Button modifyButton;
    @FXML private PieChart statisticsPieChart;
    @FXML private Button btnBillets;

    private File selectedImageFile;
    private Publication publicationToEdit = null;
    private final ObservableList<Publication> publicationList = FXCollections.observableArrayList();
    @FXML
    private MenuItem menuItemUser;
    @FXML
    public void initialize() {
        if (UserSession.getCurrentUser() != null) {
            String fullName = UserSession.getCurrentUser().getName() + " " + UserSession.getCurrentUser().getLastName();
            menuItemUser.setText("👤 " + fullName);
        } else {
            String fullName = UserSession.getCurrentUser().getName() + " " + UserSession.getCurrentUser().getLastName();
            menuItemUser.setText("👤 " + fullName);
        }
        setupTableColumns();
        publicationTable.setItems(publicationList);
        loadPublicationsFromDatabase();
        searchField.setOnKeyReleased(event -> handleSearch(event)); // Lier l'événement
        updateStatistics();
    }
    @FXML
    private void handleLogout() {
        UserSession.clear(); // Efface l'utilisateur connecté
        Stage stage = (Stage) eventbutton.getScene().getWindow();
        Parent root;
        try {
            root = FXMLLoader.load(getClass().getResource("/com/example/tunarche/login.fxml"));
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void applyChartStyle() {
        for (PieChart.Data data : statisticsPieChart.getData()) {
            Tooltip.install(data.getNode(), new Tooltip(
                    String.format("%s\n%.1f%%",
                            data.getName().replaceAll("\\(.*\\)", "").trim(),
                            (data.getPieValue() / statisticsPieChart.getData().stream()
                                    .mapToDouble(PieChart.Data::getPieValue).sum()) * 100)
            ));

            data.getNode().setOnMouseEntered(e -> data.getNode().setEffect(new Glow(0.8)));
            data.getNode().setOnMouseExited(e -> data.getNode().setEffect(null));
        }
    }
    @FXML
    private void updatePublication() {
        if (publicationToEdit != null && validateForm()) {
            String titre = titreField.getText();
            String description = descriptionField.getText();
            LocalDate date = datePicker.getValue();
            String imagePath = selectedImageFile != null ? selectedImageFile.getAbsolutePath() : null;

            try {
                Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3307/tunarche", "root", "");

                String sql = "UPDATE publication SET titre = ?, description = ?, date_act = ?, image = ? WHERE id = ?";
                PreparedStatement preparedStatement = connection.prepareStatement(sql);

                preparedStatement.setString(1, titre);
                preparedStatement.setString(2, description);
                preparedStatement.setDate(3, java.sql.Date.valueOf(date));
                preparedStatement.setString(4, imagePath);
                preparedStatement.setInt(5, publicationToEdit.getId());

                int rowsAffected = preparedStatement.executeUpdate();

                if (rowsAffected > 0) {
                    loadPublicationsFromDatabase(); // Recharge depuis la BD
                    clearForm();
                    errorLabel.setText("Publication mise à jour avec succès !");
                    errorLabel.setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
                } else {
                    errorLabel.setText("Erreur lors de la mise à jour de la publication.");
                    errorLabel.setStyle("-fx-text-fill: red;");
                }

                preparedStatement.close();
                connection.close();
            } catch (Exception e) {
                e.printStackTrace();
                errorLabel.setText("Erreur de connexion à la base de données.");
                errorLabel.setStyle("-fx-text-fill: red;");
            }
        }
    }

    private void loadPublicationsFromDatabase() {
        publicationList.clear();
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3307/tunarche", "root", "")) {
            String sql = "SELECT * FROM publication";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            var resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                int author_id = resultSet.getInt("author_id");
                String titre = resultSet.getString("titre");
                String description = resultSet.getString("description");
                String image = resultSet.getString("image");
                int likes = resultSet.getInt("likes");
                int unlikes = resultSet.getInt("unlikes");
                int rating = resultSet.getInt("rating");
                LocalDate date = resultSet.getDate("date_act").toLocalDate();

                Publication publication = new Publication(id, author_id, titre, description, image, likes, unlikes, rating, date);
                publicationList.add(publication);
            }
            resultSet.close();
            preparedStatement.close();
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
            showError("Erreur lors du chargement des publications.");
            errorLabel.setText("Erreur lors du chargement des publications.");
            errorLabel.setStyle("-fx-text-fill: red;");
        }
    }

    @FXML
    private void handleImageChoose() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choisir une image");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg")
        );
        selectedImageFile = fileChooser.showOpenDialog(null);
        if (selectedImageFile != null) {
            imageView.setImage(new Image(selectedImageFile.toURI().toString()));
            errorLabel.setText("");
        } else {
            errorLabel.setText("Aucune image sélectionnée.");
            errorLabel.setStyle("-fx-text-fill: red;");
        }
    }
    @FXML
    private void addPublication() {
        if (validateForm()) {
            String titre = titreField.getText();
            String description = descriptionField.getText();
            LocalDate date = datePicker.getValue();
            String imagePath = selectedImageFile != null ? selectedImageFile.getAbsolutePath() : null;
            int author_id = UserSession.getCurrentUser().getId();

            Publication publicationToCheck = new Publication(titre, author_id, description, date, imagePath);
            if (publicationExists(publicationToCheck)) {
                showWarning("Une publication identique existe déjà avec :\n- Même titre\n- Même description\n- Même date.");
                errorLabel.setStyle("-fx-text-fill: orange; -fx-font-weight: bold;");

                return;
            }

            try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3307/tunarche", "root", "")) {
                String sql = "INSERT INTO publication (titre,author_id, description, date_act, image,likes,unlikes,rating) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
                PreparedStatement preparedStatement = connection.prepareStatement(sql);

                preparedStatement.setString(1, titre);
                preparedStatement.setInt(2, author_id);
                preparedStatement.setString(3, description);
                preparedStatement.setDate(4, java.sql.Date.valueOf(date));
                preparedStatement.setString(5, imagePath);
                preparedStatement.setInt(6, 0);
                preparedStatement.setInt(7, 0);
                preparedStatement.setInt(8, 0);

                if (preparedStatement.executeUpdate() > 0) {
                    loadPublicationsFromDatabase();
                    clearForm();
                    errorLabel.setText("Publication ajoutée avec succès !");
                    errorLabel.setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
                    updateStatistics();

                } else {
                    showError("Erreur lors de l'ajout de la publication.");
                    errorLabel.setStyle("-fx-text-fill: red;");
                }
                preparedStatement.close();
                connection.close();
            } catch (Exception e) {
                e.printStackTrace();
                showError("Erreur de connexion à la base de données.");
            }
        }
    }
    private boolean publicationExists(Publication publicationToCheck) {
        return publicationList.stream()
                .anyMatch(pub -> pub.getTitre().equalsIgnoreCase(publicationToCheck.getTitre()));
    }
    private boolean validateForm() {
        String titre = titreField.getText();
        String description = descriptionField.getText();
        LocalDate date = datePicker.getValue();

        if (titre.isEmpty() || description.isEmpty() || date == null) {
            errorLabel.setText("Veuillez remplir tous les champs.");
            errorLabel.setStyle("-fx-text-fill: red;");
            return false;
        }

        if (date.isBefore(LocalDate.now())) {
            errorLabel.setText("La date ne peut pas être dans le passé.");
            errorLabel.setStyle("-fx-text-fill: red;");
            return false;
        }

        if (description.length() < 5) {
            errorLabel.setText("La description doit contenir au moins 5 caractères.");
            errorLabel.setStyle("-fx-text-fill: red;");
            return false;
        }

        if (!Character.isUpperCase(titre.charAt(0))) {
            errorLabel.setText("Le titre doit commencer par une majuscule.");
            errorLabel.setStyle("-fx-text-fill: red;");
            return false;
        }

        return true;
    }
    private void clearForm() {
        titreField.clear();
        descriptionField.clear();
        datePicker.setValue(null);
        imageView.setImage(null);
        selectedImageFile = null;
        publicationToEdit = null;
    }
    private void setupTableColumns() {
        TableColumn<Publication, String> titleCol = new TableColumn<>("Titre");
        titleCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getTitre()));
        titleCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setText(null);
                } else {
                    setText(item);
                    setOnMouseClicked(event -> loadPublicationForEdit(getTableRow().getItem()));
                }
            }
        });

        TableColumn<Publication, String> descriptionCol = new TableColumn<>("Description");
        descriptionCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getDescription()));

        TableColumn<Publication, LocalDate> dateCol = new TableColumn<>("Date de Publication");
        dateCol.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getDate_act()));

        TableColumn<Publication, String> imageCol = new TableColumn<>("Image");
        imageCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getImage()));

        TableColumn<Publication, Integer> likeCol = new TableColumn<>("Likes");
        likeCol.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getLikes()).asObject());

        TableColumn<Publication, Integer> unlikeCol = new TableColumn<>("Unlikes");
        unlikeCol.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getUnlikes()).asObject());

        TableColumn<Publication, Integer> ratingCol = new TableColumn<>("Rating");
        ratingCol.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getRating()).asObject());

        // === Bouton Supprimer ===
        TableColumn<Publication, Void> actionCol = new TableColumn<>("Action");

        actionCol.setCellFactory(col -> new TableCell<>() {
            private final Button deleteButton = new Button("Supprimer");

            {
                deleteButton.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");
                deleteButton.setOnAction(event -> {
                    Publication publication = getTableView().getItems().get(getIndex());
                    deletePublication(publication);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(deleteButton);
                }
            }
        });

        publicationTable.getColumns().addAll(titleCol, descriptionCol, dateCol, imageCol, likeCol, unlikeCol, ratingCol, actionCol);
    }
    private void loadPublicationForEdit(Publication publication) {
        publicationToEdit = publication;
        titreField.setText(publication.getTitre());
        descriptionField.setText(publication.getDescription());
        datePicker.setValue(publication.getDate_act());
        // Ajouter logique pour charger l'image si nécessaire
        modifyButton.setVisible(true); // Afficher le bouton "Modifier" dans le formulaire
    }
    private void deletePublication(Publication publication) {
        try {
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3307/tunarche", "root", "");

            String sql = "DELETE FROM publication WHERE id = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, publication.getId());

            int rowsAffected = statement.executeUpdate();

            if (rowsAffected > 0) {
                publicationList.remove(publication);
                errorLabel.setText("Publication supprimée !");
                errorLabel.setStyle("-fx-text-fill: green;");
            } else {
                errorLabel.setText("Échec de la suppression.");
                errorLabel.setStyle("-fx-text-fill: red;");
            }

            statement.close();
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
            errorLabel.setText("Erreur lors de la suppression.");
            errorLabel.setStyle("-fx-text-fill: red;");
        }
    }
    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setStyle("-fx-text-fill: red;");
    }
    private void showSuccess(String message) {
        errorLabel.setText(message);
        errorLabel.setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
    }
    private void showWarning(String message) {
        errorLabel.setText(message);
        errorLabel.setStyle("-fx-text-fill: orange; -fx-font-weight: bold;");
    }
    public void updateStatistics() {
        calculateStatistics();
        animatePieChart();
    }
    private void calculateStatistics() {
        ObservableList<Publication> publications = publicationTable.getItems();
            Map<String, Integer> likesCounts = new HashMap<>();

        // Comptage des likes par titre de publication
        for (Publication publication : publications) {
            if (publication != null && publication.getTitre() != null) {
                String titre = publication.getTitre();
                int likes = publication.getLikes();
                likesCounts.put(titre, likesCounts.getOrDefault(titre, 0) + likes);
            }
        }

        // Préparer les données pour le PieChart
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
        int totalLikes = likesCounts.values().stream().mapToInt(Integer::intValue).sum();

        if (totalLikes > 0) {
            for (Map.Entry<String, Integer> entry : likesCounts.entrySet()) {
                double percentage = (entry.getValue() * 100.0) / totalLikes;
                pieChartData.add(new PieChart.Data(
                        String.format("%s (%.1f%%)", entry.getKey(), percentage),
                        entry.getValue()
                ));
            }
        }

        statisticsPieChart.setData(pieChartData);
        applyChartStyle();
    }
    private void animatePieChart() {
        double total = statisticsPieChart.getData().stream()
                .mapToDouble(PieChart.Data::getPieValue)
                .sum();

        for (PieChart.Data data : statisticsPieChart.getData()) {
            data.getNode().setScaleX(0);
            data.getNode().setScaleY(0);

            ScaleTransition st = new ScaleTransition(Duration.millis(1000), data.getNode());
            st.setByX(1);
            st.setByY(1);
            st.setDelay(Duration.millis(500));
            st.play();
        }
    }
    @FXML
    private void generatePDF() {
        try {
            // 1. Créer un fichier PDF temporaire
            File tempFile = File.createTempFile("Liste_Publications", ".pdf");
            tempFile.deleteOnExit();

            // 2. Générer le PDF
            Document document = new Document();
            PdfWriter.getInstance(document, new FileOutputStream(tempFile));
            document.open();

            // Style pour le titre principal
            Font titleFont = new Font(Font.HELVETICA, 18, Font.BOLD, new Color(70, 130, 180)); // Bleu
            Paragraph title = new Paragraph("Liste des Publications", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(20);
            document.add(title);

            // Style pour les en-têtes de colonnes
            Font headerFont = new Font(Font.HELVETICA, 12, Font.BOLD, Color.WHITE);

            PdfPTable table = new PdfPTable(4);
            table.setWidthPercentage(100);

            // Couleur de fond pour les en-têtes
            Color headerBackground = new Color(70, 130, 180); // Bleu acier

            // Ajout des cellules d'en-tête avec style
            PdfPCell cell;

            cell = new PdfPCell(new Phrase("Titre", headerFont));
            cell.setBackgroundColor(headerBackground);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase("Description", headerFont));
            cell.setBackgroundColor(headerBackground);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase("Date", headerFont));
            cell.setBackgroundColor(headerBackground);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase("Likes", headerFont));
            cell.setBackgroundColor(headerBackground);
            table.addCell(cell);

            // Style pour le contenu des cellules
            Font contentFont = new Font(Font.HELVETICA, 10);

            for (Publication pub : publicationList) {
                table.addCell(new Phrase(pub.getTitre(), contentFont));
                table.addCell(new Phrase(pub.getDescription(), contentFont));
                table.addCell(new Phrase(pub.getDate_act().toString(), contentFont));
                table.addCell(new Phrase(String.valueOf(pub.getLikes()), contentFont));
            }

            document.add(table);
            document.close();

            // 3. Ouvrir le PDF
            Desktop.getDesktop().open(tempFile);

            // 4. Message de succès
            errorLabel.setText("PDF ouvert avec succès !");
            errorLabel.setStyle("-fx-text-fill: green; -fx-font-weight: bold;");

        } catch (Exception e) {
            e.printStackTrace();
            errorLabel.setText("Erreur lors de la génération du PDF.");
            errorLabel.setStyle("-fx-text-fill: red;");
        }
    }
    @FXML
    private void handleSearch(KeyEvent event) {
        String searchText = searchField.getText().toLowerCase();
        ObservableList<Publication> filteredList = FXCollections.observableArrayList();
        for (Publication p : publicationList) {
            if (p.getTitre().toLowerCase().contains(searchText) || p.getDescription().toLowerCase().contains(searchText)) {
                filteredList.add(p);
            }
        }
        publicationTable.setItems(filteredList);
    }
    @FXML
    private Button eventbutton;
    @FXML
    public void handleformation(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/tunarche/addFormation.fxml"));
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
    public void handelevent()
    {
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
    public void handebillet()
    {
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
    public void handlePublication(){
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
    public void handleCommantaire(){
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

    private void showAlert(String title, String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText("Information");
        alert.setContentText(msg);
        alert.showAndWait();
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
