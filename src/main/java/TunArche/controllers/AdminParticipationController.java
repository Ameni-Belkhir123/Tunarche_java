package TunArche.controllers;

import TunArche.entities.Participation;
import TunArche.entities.UserSession;
import TunArche.services.Participationlmpl;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;

public class AdminParticipationController {

    @FXML private TextField nomArtisteField;
    @FXML private TextField emailArtisteField;
    @FXML private TextField dateInscriptionField;
    @FXML private TextField oeuvreIdField;
    @FXML private TextField concoursIdField;
    @FXML private TextField nbrVotesField;
    @FXML private TextField imagePathField;
    @FXML private TextField searchField;

    @FXML private TableView<Participation> participationTable;
    @FXML private TableColumn<Participation, Integer> idCol;
    @FXML private TableColumn<Participation, String> nomArtisteCol;
    @FXML private TableColumn<Participation, String> emailArtisteCol;
    @FXML private TableColumn<Participation, String> dateInscriptionCol;
    @FXML private TableColumn<Participation, Integer> oeuvreIdCol;
    @FXML private TableColumn<Participation, Integer> concoursIdCol;
    @FXML private TableColumn<Participation, Integer> nbrVotesCol;
    @FXML private TableColumn<Participation, String> imagePathCol;

    @FXML private Button ajouterBtn;
    @FXML private Button modifierBtn;
    @FXML private Button supprimerBtn;
    @FXML private Button clearBtn;

    private final Participationlmpl participationService = new Participationlmpl();
    private final ObservableList<Participation> participationList = FXCollections.observableArrayList();
    private Participation selectedParticipation = null;

    @FXML
    public void initialize() {
        // Colonnes
        idCol.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getId()).asObject());

        emailArtisteCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getEmail_artiste()));
        dateInscriptionCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getDate_inscription()));
        oeuvreIdCol.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getOeuvre_id()).asObject());
        concoursIdCol.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getConcours_id()).asObject());
        nbrVotesCol.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getNbr_votes()).asObject());
        imagePathCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getImagePath()));
        nomArtisteField.setEditable(false);
        emailArtisteField.setEditable(false);
        imagePathField.setEditable(false);
        oeuvreIdField.setEditable(false);
        loadParticipations();
        setupSearch();
    }
@FXML
    private void loadParticipations() {
        participationList.setAll(participationService.showAll());
        participationTable.setItems(participationList);
    }
@FXML
    private void setupSearch() {
        FilteredList<Participation> filteredData = new FilteredList<>(participationList, p -> true);

        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(participation -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                String filter = newValue.toLowerCase();

                return participation.getEmail_artiste().toLowerCase().contains(filter)
                        || participation.getDate_inscription().toLowerCase().contains(filter)
                        || String.valueOf(participation.getId()).contains(filter)
                        || String.valueOf(participation.getOeuvre_id()).contains(filter)
                        || String.valueOf(participation.getConcours_id()).contains(filter);
            });
        });

        SortedList<Participation> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(participationTable.comparatorProperty());
        participationTable.setItems(sortedData);
    }

    @FXML
    void ajouterParticipation() {
        Participation p = new Participation();
        p.setNom_artiste(Integer.parseInt(nomArtisteField.getText()));

        p.setDate_inscription(dateInscriptionField.getText());
        p.setOeuvre_id(Integer.parseInt(oeuvreIdField.getText()));
        p.setConcours_id(Integer.parseInt(concoursIdField.getText()));
        p.setNbr_votes(Integer.parseInt(nbrVotesField.getText()));
        p.setImagePath(imagePathField.getText());

        participationService.create(p);
        clearFields();
        loadParticipations();
    }

    @FXML
    void modifierParticipation() {
        if (selectedParticipation != null) {
            selectedParticipation.setNom_artiste(Integer.parseInt(nomArtisteField.getText()));
            selectedParticipation.setEmail_artiste(emailArtisteField.getText());
            selectedParticipation.setDate_inscription(dateInscriptionField.getText());
            selectedParticipation.setOeuvre_id(Integer.parseInt(oeuvreIdField.getText()));
            selectedParticipation.setConcours_id(Integer.parseInt(concoursIdField.getText()));
            selectedParticipation.setNbr_votes(Integer.parseInt(nbrVotesField.getText()));
            selectedParticipation.setImagePath(imagePathField.getText());

            participationService.update(selectedParticipation);
            clearFields();
            loadParticipations();
        }
    }

    @FXML
    void supprimerParticipation() {
        if (selectedParticipation != null) {
            participationService.delete(selectedParticipation.getId());
            clearFields();
            loadParticipations();
        }
    }

    @FXML
    void onTableClick(MouseEvent event) {
        selectedParticipation = participationTable.getSelectionModel().getSelectedItem();
        if (selectedParticipation != null) {

            emailArtisteField.setText(selectedParticipation.getEmail_artiste());
            dateInscriptionField.setText(selectedParticipation.getDate_inscription());
            oeuvreIdField.setText(String.valueOf(selectedParticipation.getOeuvre_id()));
            concoursIdField.setText(String.valueOf(selectedParticipation.getConcours_id()));
            nbrVotesField.setText(String.valueOf(selectedParticipation.getNbr_votes()));
            imagePathField.setText(selectedParticipation.getImagePath());
        }
    }

    @FXML
    void clearFields() {
        nomArtisteField.clear();
        emailArtisteField.clear();
        dateInscriptionField.clear();
        oeuvreIdField.clear();
        concoursIdField.clear();
        nbrVotesField.clear();
        imagePathField.clear();
        searchField.clear();
        selectedParticipation = null;
        participationTable.getSelectionModel().clearSelection();
    }
    @FXML
    private Button eventbutton;
    @FXML
    private Button btnBillets;
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
    private void showAlert(String title, String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText("Information");
        alert.setContentText(msg);
        alert.showAndWait();
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

    @FXML
    private void handleLogout() {
        UserSession.clear(); // Efface l'utilisateur connecté
        Stage stage = (Stage) btnBillets.getScene().getWindow();
        Parent root;
        try {
            root = FXMLLoader.load(getClass().getResource("/com/example/tunarche/login.fxml"));
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
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
