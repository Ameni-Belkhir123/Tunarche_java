package com.example.oeuvre.Controllers;

import com.example.oeuvre.Entities.AssignationRequest;
import com.example.oeuvre.Entities.Galerie;
import com.example.oeuvre.Entities.Oeuvre;
import com.example.oeuvre.Services.AssignationRequestService;
import com.example.oeuvre.Services.GalerieService;
import com.example.oeuvre.Services.OeuvreService;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.io.File;
import java.util.List;
import java.util.Optional;

public class AdminController {

    @FXML
    private TextField galerieNameField, galerieDescriptionField, galerieAddressField;
    @FXML
    private ListView<Galerie> galerieListView;
    @FXML
    private ListView<Oeuvre> assignedOeuvresListView;
    @FXML
    private TableView<AssignationRequest> requestedOeuvresTableView;
    @FXML
    private TableColumn<AssignationRequest, String> oeuvreColumn;
    @FXML
    private TableColumn<AssignationRequest, String> galleryColumn;
    @FXML
    private TableColumn<AssignationRequest, Integer> userColumn;
    @FXML
    private TableColumn<AssignationRequest, ImageView> imageColumn;
    @FXML
    private Button updateButton, deleteButton, removeButton, approveRequestButton, rejectRequestButton;
    @FXML private StackPane contentArea;
    @FXML private VBox addGalerieSection;
    @FXML private VBox listGaleriesSection;
    @FXML private VBox requestsAssignSection;
    @FXML private VBox oeuvreManagementView;
    @FXML private Hyperlink addGalerieLink;
    @FXML private Hyperlink listGaleriesLink;
    @FXML private Hyperlink requestsAssignLink;

    private final GalerieService galerieService = new GalerieService();
    private final OeuvreService oeuvreService = new OeuvreService();
    private final AssignationRequestService assignationRequestService = new AssignationRequestService();

    private Galerie selectedGalerie;
    private AssignationRequest selectedRequest;

    @FXML
    public void initialize() {
        System.out.println("Initialisation du contrôleur...");

        refreshGalerieList();
        refreshAssignedOeuvresList();
        setupTableColumns();
        refreshRequestedOeuvresList();

        setupListeners();
        setupBindings();
        setupCellFactories();
        showAddGalerieSection(null); // Show Add Galerie as default
    }

    @FXML
    private void showAddGalerieSection(ActionEvent event) {
        addGalerieSection.setVisible(true);
        listGaleriesSection.setVisible(false);
        requestsAssignSection.setVisible(false);
        oeuvreManagementView.setVisible(false);
    }

    @FXML
    private void showListGaleriesSection(ActionEvent event) {
        addGalerieSection.setVisible(false);
        listGaleriesSection.setVisible(true);
        requestsAssignSection.setVisible(false);
        oeuvreManagementView.setVisible(false);
    }

    @FXML
    private void showRequestsAssignSection(ActionEvent event) {
        addGalerieSection.setVisible(false);
        listGaleriesSection.setVisible(false);
        requestsAssignSection.setVisible(true);
        oeuvreManagementView.setVisible(false);
        refreshRequestedOeuvresList(); // Ensure requests are loaded when this view is shown
    }

    @FXML
    private void showOeuvreManagement(ActionEvent event) {
        addGalerieSection.setVisible(false);
        listGaleriesSection.setVisible(false);
        requestsAssignSection.setVisible(false);
        oeuvreManagementView.setVisible(true);
    }

    private void setupTableColumns() {
        oeuvreColumn.setCellValueFactory(cellData -> {
            Oeuvre oeuvre = oeuvreService.getOeuvreById(cellData.getValue().getOeuvreId());
            return new SimpleStringProperty(oeuvre != null ? oeuvre.getTitle() : "Unknown");
        });

        galleryColumn.setCellValueFactory(cellData -> {
            Galerie galerie = galerieService.getGalerieById(cellData.getValue().getGalerieId());
            return new SimpleStringProperty(galerie != null ? galerie.getName() : "Unknown");
        });

        userColumn.setCellValueFactory(cellData ->
                new SimpleIntegerProperty(cellData.getValue().getUserId()).asObject());

        imageColumn.setCellFactory(param -> new TableCell<>() {
            private final ImageView imageView = new ImageView();

            {
                imageView.setFitWidth(100);
                imageView.setFitHeight(100);
                imageView.setPreserveRatio(true);
            }

            @Override
            protected void updateItem(ImageView item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    AssignationRequest request = getTableView().getItems().get(getIndex());
                    Oeuvre oeuvre = oeuvreService.getOeuvreById(request.getOeuvreId());
                    if (oeuvre != null && oeuvre.getImagePath() != null) {
                        try {
                            Image image = new Image(new File(oeuvre.getImagePath()).toURI().toString());
                            imageView.setImage(image);
                        } catch (Exception e) {
                            try {
                                imageView.setImage(new Image(getClass().getResourceAsStream("/images/placeholder.png")));
                            } catch (Exception ex) {
                                imageView.setImage(null);
                            }
                        }
                    }
                    setGraphic(imageView);
                }
            }
        });
    }

    private void setupListeners() {
        galerieListView.setOnMouseClicked(this::handleGalerieSelection);
        galerieListView.setOnMouseClicked(this::showOeuvresPopupOnDoubleClick); // Add listener for double-click
        requestedOeuvresTableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            selectedRequest = newSelection;
        });
    }

    private void setupBindings() {
        updateButton.disableProperty().bind(galerieListView.getSelectionModel().selectedItemProperty().isNull());
        deleteButton.disableProperty().bind(galerieListView.getSelectionModel().selectedItemProperty().isNull());

        removeButton.disableProperty().bind(
                assignedOeuvresListView.getSelectionModel().selectedItemProperty().isNull()
                        .or(galerieListView.getSelectionModel().selectedItemProperty().isNull())
        );

        approveRequestButton.disableProperty().bind(requestedOeuvresTableView.getSelectionModel().selectedItemProperty().isNull());
        rejectRequestButton.disableProperty().bind(requestedOeuvresTableView.getSelectionModel().selectedItemProperty().isNull());
    }

    private void setupCellFactories() {
        galerieListView.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Galerie galerie, boolean empty) {
                super.updateItem(galerie, empty);
                setText(empty || galerie == null ? null : galerie.getName());
            }
        });

        assignedOeuvresListView.setCellFactory(param -> new ListCell<>() {
            private final ImageView imageView = new ImageView();
            private final Label titleLabel = new Label();
            private final VBox container = new VBox(5); // Vertical layout for image and title

            {
                imageView.setFitWidth(50);
                imageView.setFitHeight(50);
                imageView.setPreserveRatio(true);
                container.getChildren().addAll(imageView, titleLabel);
            }

            @Override
            protected void updateItem(Oeuvre oeuvre, boolean empty) {
                super.updateItem(oeuvre, empty);
                if (empty || oeuvre == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    titleLabel.setText(oeuvre.getTitle());
                    if (oeuvre.getImagePath() != null) {
                        try {
                            Image image = new Image(new File(oeuvre.getImagePath()).toURI().toString());
                            imageView.setImage(image);
                        } catch (Exception e) {
                            try {
                                imageView.setImage(new Image(getClass().getResourceAsStream("/images/placeholder_small.png"))); // Small placeholder
                            } catch (Exception ex) {
                                imageView.setImage(null);
                            }
                        }
                    } else {
                        try {
                            imageView.setImage(new Image(getClass().getResourceAsStream("/images/placeholder_small.png"))); // Small placeholder if no path
                        } catch (Exception ex) {
                            imageView.setImage(null);
                        }
                    }
                    setGraphic(container);
                }
            }
        });
    }

    private void refreshGalerieList() {
        galerieListView.setItems(FXCollections.observableList(galerieService.getAllGaleries()));
    }

    private void refreshAssignedOeuvresList() {
        if (selectedGalerie != null) {
            List<Oeuvre> assignedOeuvres = oeuvreService.getOeuvresByGalerie(selectedGalerie.getId());
            assignedOeuvresListView.setItems(FXCollections.observableList(assignedOeuvres));
        } else {
            assignedOeuvresListView.setItems(FXCollections.emptyObservableList());
        }
    }

    private void refreshRequestedOeuvresList() {
        List<AssignationRequest> pendingRequests = assignationRequestService.getPendingRequests();
        requestedOeuvresTableView.setItems(FXCollections.observableList(pendingRequests));
    }

    @FXML
    private void handleGalerieSelection(MouseEvent event) {
        selectedGalerie = galerieListView.getSelectionModel().getSelectedItem();
        if (selectedGalerie != null) {
            galerieNameField.setText(selectedGalerie.getName());
            galerieDescriptionField.setText(selectedGalerie.getDescription());
            galerieAddressField.setText(selectedGalerie.getAddress());
            refreshAssignedOeuvresList();
        } else {
            clearGalerieDetails();
        }
    }

    @FXML
    private void handleApproveRequest() {
        if (selectedRequest != null) {
            Oeuvre oeuvre = oeuvreService.getOeuvreById(selectedRequest.getOeuvreId());
            if (oeuvre != null) {
                oeuvre.setGalerieId(selectedRequest.getGalerieId());
                oeuvre.setGalleryName(galerieService.getGalerieById(selectedRequest.getGalerieId()).getName()); // Set gallery name
                oeuvre.setProposedGalerieId(null);
                oeuvreService.updateOeuvre(oeuvre);

                assignationRequestService.approveRequest(selectedRequest.getId(), 1);
                refreshRequestedOeuvresList();
                refreshAssignedOeuvresList();
                showAlert("Succès", "Demande approuvée.");
            }
        } else {
            showAlert("Erreur", "Veuillez sélectionner une demande à approuver.");
        }
    }

    @FXML
    private void handleRejectRequest() {
        if (selectedRequest != null) {
            Oeuvre oeuvre = oeuvreService.getOeuvreById(selectedRequest.getOeuvreId());
            if (oeuvre != null) {
                oeuvre.setProposedGalerieId(null);
                oeuvreService.updateOeuvre(oeuvre);

                assignationRequestService.rejectRequest(selectedRequest.getId(), 1);
                refreshRequestedOeuvresList();
                showAlert("Succès", "Demande rejetée.");
            }
        } else {
            showAlert("Erreur", "Veuillez sélectionner une demande à rejeter.");
        }
    }

    @FXML
    private void handleAddGalerie() {
        if (galerieNameField.getText().isEmpty()) {
            showAlert("Erreur", "Le nom de la galerie ne peut pas être vide.");
            return;
        }
        Galerie galerie = new Galerie(0, galerieNameField.getText(), galerieDescriptionField.getText(), galerieAddressField.getText(), null);
        galerieService.addGalerie(galerie);
        refreshGalerieList();
        clearFields();
        showAlert("Succès", "Galerie ajoutée avec succès.");
    }

    @FXML
    private void handleUpdateGalerie() {
        if (selectedGalerie == null || galerieNameField.getText().isEmpty()) {
            showAlert("Erreur", "Veuillez sélectionner une galerie et renseigner son nom.");
            return;
        }
        selectedGalerie.setName(galerieNameField.getText());
        selectedGalerie.setDescription(galerieDescriptionField.getText());
        selectedGalerie.setAddress(galerieAddressField.getText());
        galerieService.updateGalerie(selectedGalerie);
        refreshGalerieList();
        clearFields();
        showAlert("Succès", "Galerie mise à jour avec succès.");
    }

    @FXML
    private void handleDeleteGalerie() {
        if (selectedGalerie == null) {
            showAlert("Erreur", "Aucune galerie sélectionnée.");
            return;
        }
        galerieService.deleteGalerie(selectedGalerie.getId());
        refreshGalerieList();
        clearFields();
        showAlert("Succès", "Galerie supprimée avec succès.");
    }

    @FXML
    private void handleRemoveOeuvreFromGalerie() {
        Oeuvre selectedOeuvre = assignedOeuvresListView.getSelectionModel().getSelectedItem();
        if (selectedOeuvre == null) {
            showAlert("Erreur", "Veuillez sélectionner une œuvre à retirer.");
            return;
        }

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirmation de retrait");
        confirmation.setHeaderText("Retirer l'œuvre de la galerie");
        confirmation.setContentText("Êtes-vous sûr de vouloir retirer '" + selectedOeuvre.getTitle() +
                "' de la galerie '" + selectedGalerie.getName() + "' ?");

        Optional<ButtonType> result = confirmation.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                selectedOeuvre.setGalerieId(null);
                selectedOeuvre.setGalleryName(null);
                oeuvreService.updateOeuvre(selectedOeuvre);
                refreshAssignedOeuvresList();
                showAlert("Succès", "L'œuvre a été retirée de la galerie avec succès.");
            } catch (Exception e) {
                showAlert("Erreur", "Une erreur s'est produite lors du retrait de l'œuvre: " + e.getMessage());
            }
        }
    }

    @FXML
    private void showOeuvresPopupOnDoubleClick(MouseEvent event) {
        if (event.getClickCount() == 2) {
            Galerie galerie = galerieListView.getSelectionModel().getSelectedItem();
            if (galerie != null) {
                showOeuvresPopup(galerie);
            }
        }
    }

    private void showOeuvresPopup(Galerie galerie) {
        Stage popupStage = new Stage();
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.setTitle("Oeuvres de la galerie: " + galerie.getName());

        VBox popupContent = new VBox(10);
        popupContent.setPadding(new Insets(15));

        List<Oeuvre> oeuvresInGalerie = oeuvreService.getOeuvresByGalerie(galerie.getId());
        if (oeuvresInGalerie.isEmpty()) {
            popupContent.getChildren().add(new Label("Aucune œuvre n'est actuellement assignée à cette galerie."));
        } else {
            for (Oeuvre oeuvre : oeuvresInGalerie) {
                HBox oeuvreInfo = new HBox(15);
                oeuvreInfo.setAlignment(Pos.CENTER_LEFT);

                ImageView imageView = new ImageView();
                imageView.setFitWidth(80);
                imageView.setFitHeight(80);
                imageView.setPreserveRatio(true);

                if (oeuvre.getImagePath() != null) {
                    try {
                        Image image = new Image(new File(oeuvre.getImagePath()).toURI().toString());
                        imageView.setImage(image);
                    } catch (Exception e) {
                        try {
                            imageView.setImage(new Image(getClass().getResourceAsStream("/images/placeholder_small.png")));
                        } catch (Exception ex) {
                            imageView.setImage(null);
                        }
                    }
                } else {
                    try {
                        imageView.setImage(new Image(getClass().getResourceAsStream("/images/placeholder_small.png")));
                    } catch (Exception ex) {
                        imageView.setImage(null);
                    }
                }

                VBox textInfo = new VBox(5);
                Label nameLabel = new Label("Nom: " + oeuvre.getTitle());
                Label descriptionLabel = new Label("Description: " + oeuvre.getDescription());
                descriptionLabel.setWrapText(true);
                descriptionLabel.setMaxWidth(300);

                textInfo.getChildren().addAll(nameLabel, descriptionLabel);
                oeuvreInfo.getChildren().addAll(imageView, textInfo);
                popupContent.getChildren().add(oeuvreInfo);
            }
        }

        ScrollPane scrollPane = new ScrollPane(popupContent);
        scrollPane.setFitToWidth(true);

        Scene popupScene = new Scene(scrollPane, 600, 400);
        popupStage.setScene(popupScene);
        popupStage.showAndWait();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void clearFields() {
        galerieNameField.clear();
        galerieDescriptionField.clear();
        galerieAddressField.clear();
    }

    private void clearGalerieDetails() {
        clearFields();
        assignedOeuvresListView.setItems(FXCollections.emptyObservableList());
    }
}