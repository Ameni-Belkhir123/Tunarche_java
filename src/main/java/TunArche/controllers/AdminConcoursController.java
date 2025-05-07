package TunArche.controllers;

import TunArche.entities.Concours;
import TunArche.entities.UserSession;
import TunArche.services.Concourslmpl;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

public class AdminConcoursController {

    @FXML private TextField titreField;
    @FXML private TextArea descriptionField;
    @FXML private DatePicker dateDebutPicker;
    @FXML private DatePicker dateFinPicker;
    @FXML private TextField searchField;
    @FXML private TableView<Concours> concoursTable;
    @FXML private TableColumn<Concours, Integer> idCol;
    @FXML private TableColumn<Concours, String> titreCol;
    @FXML private TableColumn<Concours, String> descriptionCol;
    @FXML private TableColumn<Concours, LocalDate> dateDebutCol;
    @FXML private TableColumn<Concours, LocalDate> dateFinCol;
    @FXML private Button saveBtn;
    @FXML private Button clearBtn;
    @FXML private Button deleteBtn;
    @FXML private Button deco;

    private final Concourslmpl concoursService = new Concourslmpl();
    private Concours selectedConcours = null;
    private ObservableList<Concours> concoursList = FXCollections.observableArrayList();
    @FXML
    private MenuItem menuItemUser
            ;
    @FXML
    public void initialize() {
        if (UserSession.getCurrentUser() != null) {
            String fullName = UserSession.getCurrentUser().getName() + " " + UserSession.getCurrentUser().getLastName();
            menuItemUser.setText("👤 " + fullName);
        } else {
            menuItemUser.setText("👤 Utilisateur inconnu");
            System.err.println("⚠️ Aucun utilisateur connecté !");
        }
        setupTableColumns();
        loadConcours();
        setupSearch();
        concoursTable.setOnMouseClicked(this::handleRowClick);
    }

    @FXML
    private void setupTableColumns() {
        idCol.setCellValueFactory(data -> new javafx.beans.property.SimpleIntegerProperty(data.getValue().getId()).asObject());
        titreCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getTitre()));
        descriptionCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getDescription()));
        dateDebutCol.setCellValueFactory(data -> new javafx.beans.property.SimpleObjectProperty<>(data.getValue().getDateDebut()));
        dateFinCol.setCellValueFactory(data -> new javafx.beans.property.SimpleObjectProperty<>(data.getValue().getDateFin()));
    }
    @FXML
    private void handleLogout() {
        UserSession.clear(); // Efface l'utilisateur connecté
        Stage stage = (Stage) deco.getScene().getWindow();
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
    private void loadConcours() {
        List<Concours> concours = concoursService.showAll();
        concoursList.setAll(concours);
        concoursTable.setItems(concoursList);
    }

    @FXML
    private void setupSearch() {
        searchField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal == null || newVal.isEmpty()) {
                concoursTable.setItems(concoursList);
            } else {
                ObservableList<Concours> filtered = concoursList.filtered(c ->
                        c.getTitre().toLowerCase().contains(newVal.toLowerCase()) ||
                                c.getDescription().toLowerCase().contains(newVal.toLowerCase())
                );
                concoursTable.setItems(filtered);
            }
        });
    }

    @FXML
    private void handleSave() {
        String titre = titreField.getText();
        String description = descriptionField.getText();
        LocalDate dateDebut = dateDebutPicker.getValue();
        LocalDate dateFin = dateFinPicker.getValue();


        // Contrôle de saisie des dates
        if (dateDebut != null && dateFin != null && !dateDebut.isBefore(dateFin)) {
            showAlert(Alert.AlertType.WARNING, "Date invalide", "La date de début doit être antérieure à la date de fin.");
            return;
        }

        // Contrôle de l'unicité du titre
        if (concoursService.existsWithTitle(titre)) {
            showAlert(Alert.AlertType.WARNING, "Titre déjà existant", "Un concours avec ce titre existe déjà.");
            return;
        }
        if (titre.isEmpty() || description.isEmpty() || dateDebut == null || dateFin == null) {
            showAlert(Alert.AlertType.WARNING, "Champs manquants", "Merci de remplir tous les champs.");
            return;
        }

        if (selectedConcours == null) {
            // Ajout
            Concours newConcours = new Concours(titre, description, dateDebut, dateFin);
            concoursService.create(newConcours);
            showAlert(Alert.AlertType.INFORMATION, "Ajouté", "Concours ajouté avec succès.");
        } else {
            // Modification
            selectedConcours.setTitre(titre);
            selectedConcours.setDescription(description);
            selectedConcours.setDateDebut(dateDebut);
            selectedConcours.setDateFin(dateFin);
            concoursService.update(selectedConcours);
            showAlert(Alert.AlertType.INFORMATION, "Modifié", "Concours modifié avec succès.");
        }

        clearForm();
        loadConcours();
    }

    @FXML
    private void handleDelete() {
        if (selectedConcours != null) {
            concoursService.delete(selectedConcours.getId());
            showAlert(Alert.AlertType.INFORMATION, "Supprimé", "Concours supprimé.");
            clearForm();
            loadConcours();
        } else {
            showAlert(Alert.AlertType.WARNING, "Sélection requise", "Veuillez sélectionner un concours à supprimer.");
        }
    }

    @FXML
    private void handleRowClick(MouseEvent event) {
        Concours c = concoursTable.getSelectionModel().getSelectedItem();
        if (c != null) {
            selectedConcours = c;
            titreField.setText(c.getTitre());
            descriptionField.setText(c.getDescription());
            dateDebutPicker.setValue(c.getDateDebut());
            dateFinPicker.setValue(c.getDateFin());
        }
    }

    @FXML
    private void clearForm() {
        titreField.clear();
        descriptionField.clear();
        dateDebutPicker.setValue(null);
        dateFinPicker.setValue(null);
        selectedConcours = null;
        concoursTable.getSelectionModel().clearSelection();
    }
    @FXML
    private Button eventbutton;
    @FXML
    private Button btnBillets;
    @FXML
    void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.show();
    }
    private void showAlert(String title, String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText("Information");
        alert.setContentText(msg);
        alert.showAndWait();
    }
    @FXML
    public void handleformation(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/tunarche/addFormation.fxml"));
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
