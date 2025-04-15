package TunArche.controllers;

import TunArche.entities.Concours;
import TunArche.services.Concourslmpl;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

public class AdminConcoursController {

    @FXML private BorderPane rootPane;
    @FXML private TableView<Concours> concoursTable;
    @FXML private TableColumn<Concours, String> colTitre;
    @FXML private TableColumn<Concours, String> colDescription;
    @FXML private TableColumn<Concours, String> colDateDebut;
    @FXML private TableColumn<Concours, String> colDateFin;
    @FXML private TextField searchField;
    @FXML private Label statusLabel;



    private final ObservableList<Concours> concoursList = FXCollections.observableArrayList();
    private final Concourslmpl concoursService = new Concourslmpl();
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @FXML
    public void initialize() {
        setupTableColumns();
        loadConcoursData();
        concoursTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    private void setupTableColumns() {
        colTitre.setCellValueFactory(new PropertyValueFactory<>("titre"));
        colDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        colDateDebut.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getDateDebut().format(dateFormatter)));
        colDateFin.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getDateFin().format(dateFormatter)));
    }

    private void loadConcoursData() {
        List<Concours> concoursFromDB = concoursService.showAll();
        concoursList.setAll(concoursFromDB);
        concoursTable.setItems(concoursList);
        statusLabel.setText(concoursList.isEmpty() ? "Aucun concours disponible." :
                concoursList.size() + " concours chargés.");
    }

    @FXML
    private void addConcours(ActionEvent event) {
        loadAjouterConcoursView(null);
    }

    @FXML
    private void editConcours(ActionEvent event) {
        Concours selected = concoursTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Aucune sélection", "Veuillez sélectionner un concours à modifier.", Alert.AlertType.WARNING);
            return;
        }
        loadAjouterConcoursView(selected);
    }



    private void loadAjouterConcoursView(Concours concoursToEdit) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/tunarche/AjouterConcours.fxml"));
            Parent formView = loader.load();
            AjouterConcours controller = loader.getController();
            if (concoursToEdit != null) {
                controller.setConcoursPourEdition(concoursToEdit);
            }
            controller.setAdminController(this); // pour revenir ici après ajout
            rootPane.setCenter(formView); // Remplacer la vue centrale
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de charger le formulaire.", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void deleteConcours(ActionEvent event) {
        Concours selected = concoursTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmationAlert.setTitle("Confirmation de suppression");
            confirmationAlert.setHeaderText("Supprimer ce concours ?");
            confirmationAlert.setContentText("Titre : " + selected.getTitre());

            Optional<ButtonType> result = confirmationAlert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                if (concoursService.delete(selected.getId())) {
                    concoursList.remove(selected);
                    showAlert("Succès", "Concours supprimé.", Alert.AlertType.INFORMATION);
                } else {
                    showAlert("Erreur", "Erreur lors de la suppression.", Alert.AlertType.ERROR);
                }
            }
        } else {
            showAlert("Aucune sélection", "Sélectionnez un concours à supprimer.", Alert.AlertType.WARNING);
        }
    }

    @FXML
    private void filterConcours(ActionEvent event) {
        String keyword = searchField.getText().toLowerCase().trim();
        if (keyword.isEmpty()) {
            concoursTable.setItems(concoursList);
            statusLabel.setText(concoursList.size() + " concours affichés.");
        } else {
            ObservableList<Concours> filtered = concoursList.filtered(c ->
                    c.getTitre().toLowerCase().contains(keyword) ||
                            c.getDescription().toLowerCase().contains(keyword));
            concoursTable.setItems(filtered);
            statusLabel.setText(filtered.isEmpty() ? "Aucun résultat." : filtered.size() + " trouvé(s)");
        }
    }

    @FXML
    private void showEspaceUtilisateur(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/tunarche/afficher_concours_grid.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Espace Utilisateur");
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void reloadConcours() {
        loadConcoursData();
    }

    private void showAlert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
