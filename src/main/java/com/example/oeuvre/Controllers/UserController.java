package com.example.oeuvre.Controllers;

import com.example.oeuvre.Entities.Galerie;
import com.example.oeuvre.Entities.Oeuvre;
import com.example.oeuvre.Services.AssignationRequestService;
import com.example.oeuvre.Services.GalerieService;
import com.example.oeuvre.Services.OeuvreService;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class UserController {

    @FXML
    private TableView<Oeuvre> oeuvreTable;
    @FXML
    private TableColumn<Oeuvre, String> titleColumn;
    @FXML
    private TableColumn<Oeuvre, String> descriptionColumn;
    @FXML
    private TableColumn<Oeuvre, Boolean> approvedColumn;
    @FXML
    private TableColumn<Oeuvre, String> imageColumn;
    @FXML
    private TableColumn<Oeuvre, String> creatorColumn;
    @FXML
    private TableColumn<Oeuvre, String> galleryNameColumn;
    @FXML
    private TableColumn<Oeuvre, Void> actionColumn;
    @FXML
    private TextField titleField;
    @FXML
    private TextField descriptionField;
    @FXML
    private ImageView oeuvreImageView;
    @FXML
    private TextField searchField;
    @FXML
    private Pagination pagination;
    @FXML
    private Button addOeuvreButton;

    // Popup components
    @FXML
    private Dialog<Void> oeuvreDetailsDialog;
    @FXML
    private ImageView detailImageView;
    @FXML
    private Label detailTitleLabel;
    @FXML
    private Label detailGalleryLabel;
    @FXML
    private Label detailDescriptionLabel;

    // Sidebar navigation
    @FXML
    private VBox oeuvresContent;
    @FXML
    private VBox addOeuvreFormContent;
    @FXML
    private VBox dashboardContent;
    @FXML
    private VBox settingsContent;
    @FXML
    private javafx.scene.layout.TilePane oeuvreCardContainer; // Inject TilePane

    private final OeuvreService oeuvreService = new OeuvreService();
    private final GalerieService galerieService = new GalerieService();
    private final AssignationRequestService assignationRequestService = new AssignationRequestService();
    private ObservableList<Oeuvre> oeuvreList = FXCollections.observableArrayList();
    private FilteredList<Oeuvre> filteredList;
    private static final int ROWS_PER_PAGE = 10;
    private String selectedImagePath = null;
    private int currentUserId;
    private final String IMAGES_DIRECTORY = "src/main/resources/com/example/oeuvre/images/"; // Corrected path
    private final ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    private Oeuvre currentlyEditingOeuvre = null;

    @FXML
    private void initialize() {
        // First setup all table configurations (will not be visible now)
        // setupTableColumns();
        setupSearchFunctionalityForCards(); // Use the card-specific search
        setupImageSelectionListener();
        // setupActionColumn(); // Action column might need a different approach for cards
        // setupTableClickHandler(); // Click handler might be on the card itself

        // Initialize the dialog owner after the scene is set
        Platform.runLater(() -> {
            Stage stage = (Stage) (oeuvreCardContainer != null ? oeuvreCardContainer.getScene().getWindow() : oeuvreTable.getScene().getWindow());
            oeuvreDetailsDialog.initOwner(stage);

            // Load initial data after UI is fully initialized
            loadInitialOeuvresForCards(); // Load data for cards
        });

        // Show Oeuvres content by default
        showOeuvresContent();
    }

    @FXML
    public void showDashboardContent() {
        oeuvresContent.setVisible(false);
        addOeuvreFormContent.setVisible(false);
        dashboardContent.setVisible(true);
        settingsContent.setVisible(false);
    }

    @FXML
    public void showOeuvresContent() {
        oeuvresContent.setVisible(true);
        addOeuvreFormContent.setVisible(false);
        dashboardContent.setVisible(false);
        settingsContent.setVisible(false);
        // Ensure data is loaded when showing the Oeuvres content
        loadInitialOeuvresForCards();
    }

    @FXML
    public void showAddOeuvreForm() {
        oeuvresContent.setVisible(false);
        addOeuvreFormContent.setVisible(true);
        dashboardContent.setVisible(false);
        settingsContent.setVisible(false);
    }

    @FXML
    public void showSettingsContent() {
        oeuvresContent.setVisible(false);
        addOeuvreFormContent.setVisible(false);
        dashboardContent.setVisible(false);
        settingsContent.setVisible(true);
    }

    private void setupCardClickHandler(VBox card, Oeuvre oeuvre) {
        card.setOnMouseClicked(event -> {
            if (event.getClickCount() == 1) {
                showOeuvreDetails(oeuvre);
            }
        });
    }

    private void showOeuvreDetails(Oeuvre oeuvre) {
        detailTitleLabel.setText(oeuvre.getTitle());
        detailGalleryLabel.setText("Galerie: " + (oeuvre.getGalleryName() != null ? oeuvre.getGalleryName() : "Non assignée"));
        detailDescriptionLabel.setText(oeuvre.getDescription());

        if (oeuvre.getImagePath() != null && !oeuvre.getImagePath().isEmpty()) {
            try {
                Image image = new Image("file:" + oeuvre.getImagePath());
                detailImageView.setImage(image);
            } catch (Exception e) {
                detailImageView.setImage(null);
            }
        } else {
            detailImageView.setImage(null);
        }

        // Just show the dialog - owner is already set
        oeuvreDetailsDialog.showAndWait();
    }

    public void setCurrentUserId(int currentUserId) {
        this.currentUserId = currentUserId;
    }

    // No longer used for the card layout
    /*
    private void setupTableColumns() {
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));

        // Image column with actual images
        imageColumn.setCellFactory(column -> new TableCell<>() {
            private final ImageView imageView = new ImageView();
            {
                imageView.setFitHeight(50);
                imageView.setFitWidth(50);
                imageView.setPreserveRatio(true);
            }

            @Override
            protected void updateItem(String imagePath, boolean empty) {
                super.updateItem(imagePath, empty);
                if (empty || imagePath == null || imagePath.isEmpty()) {
                    setGraphic(null);
                } else {
                    try {
                        Image image = new Image("file:" + imagePath);
                        imageView.setImage(image);
                        setGraphic(imageView);
                    } catch (Exception e) {
                        setGraphic(null);
                    }
                }
            }
        });

        creatorColumn.setCellValueFactory(new PropertyValueFactory<>("creator"));
        galleryNameColumn.setCellValueFactory(new PropertyValueFactory<>("galleryName"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        approvedColumn.setCellValueFactory(new PropertyValueFactory<>("approved"));
    }

    private void setupActionColumn() {
        actionColumn.setCellFactory(param -> new TableCell<>() {
            private final Button editButton = new Button("Modifier");
            private final Button proposeButton = new Button("Proposer");
            private final HBox hbox = new HBox(5, editButton, proposeButton);

            {
                hbox.setStyle("-fx-alignment: CENTER;");
                editButton.getStyleClass().add("edit-button");
                proposeButton.getStyleClass().add("propose-button");

                editButton.setOnAction(event -> {
                    Oeuvre oeuvre = getTableView().getItems().get(getIndex());
                    handleEditOeuvre(oeuvre);
                });
                proposeButton.setOnAction(event -> {
                    Oeuvre oeuvre = getTableView().getItems().get(getIndex());
                    showProposeToGalleryDialog(oeuvre);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : hbox);
            }
        });
    }
    */

    private void setupSearchFunctionalityForCards() {
        filteredList = new FilteredList<>(oeuvreList, p -> true);
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredList.setPredicate(oeuvre -> {
                if (newValue == null || newValue.isEmpty()) return true;
                String lower = newValue.toLowerCase();
                return oeuvre.getTitle().toLowerCase().contains(lower)
                        || oeuvre.getDescription().toLowerCase().contains(lower)
                        || (oeuvre.getCreator() != null && oeuvre.getCreator().toLowerCase().contains(lower))
                        || (oeuvre.getGalleryName() != null && oeuvre.getGalleryName().toLowerCase().contains(lower));
            });
            updateCardDisplay();
        });
    }

    private void setupImageSelectionListener() {
        // Listener might not be directly on the table anymore
    }

    private void loadInitialOeuvresForCards() {
        Task<List<Oeuvre>> loadTask = new Task<>() {
            @Override
            protected List<Oeuvre> call() throws Exception {
                return oeuvreService.getAllOeuvresByUserId(currentUserId);
            }
        };

        loadTask.setOnSucceeded(event -> {
            oeuvreList.setAll(loadTask.getValue());
            updateCardDisplay();
        });

        loadTask.setOnFailed(event -> {
            showError("Erreur lors du chargement des œuvres: " + loadTask.getException().getMessage());
        });

        executorService.execute(loadTask);
    }

    private void updateCardDisplay() {
        int pageCount = (int) Math.ceil((double) filteredList.size() / ROWS_PER_PAGE);
        pagination.setPageCount(Math.max(pageCount, 1));
        pagination.setPageFactory(this::createCardPage);
    }

    private Node createCardPage(int pageIndex) {
        int fromIndex = pageIndex * ROWS_PER_PAGE;
        int toIndex = Math.min(fromIndex + ROWS_PER_PAGE, filteredList.size());
        ObservableList<Oeuvre> currentPageOeuvres = FXCollections.observableArrayList(filteredList.subList(fromIndex, toIndex));
        oeuvreCardContainer.getChildren().clear();
        for (Oeuvre oeuvre : currentPageOeuvres) {
            VBox card = createOeuvreCard(oeuvre);
            oeuvreCardContainer.getChildren().add(card);
        }
        return oeuvreCardContainer;
    }

    private VBox createOeuvreCard(Oeuvre oeuvre) {
        VBox card = new VBox(10);
        card.setStyle("-fx-background-color: white; -fx-border-color: #ddd; -fx-border-radius: 5; -fx-padding: 10; -fx-pref-width: 200;");
        card.setAlignment(javafx.geometry.Pos.CENTER);
        card.getStyleClass().add("oeuvre-card");
        setupCardClickHandler(card, oeuvre);

        ImageView imageView = new ImageView();
        imageView.setFitWidth(180);
        imageView.setFitHeight(150);
        imageView.setPreserveRatio(true);
        imageView.getStyleClass().add("image-view");
        if (oeuvre.getImagePath() != null) {
            try {
                Image image = new Image("file:" + oeuvre.getImagePath());
                imageView.setImage(image);
            } catch (Exception e) {
                imageView.setImage(new Image(getClass().getResourceAsStream("/com/example/oeuvre/images/placeholder.png")));
            }
        } else {
            imageView.setImage(new Image(getClass().getResourceAsStream("/com/example/oeuvre/images/placeholder.png")));
        }

        Label titleLabel = new Label(oeuvre.getTitle());
        titleLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 1.1em;");
        titleLabel.setWrapText(true);
        titleLabel.setAlignment(javafx.geometry.Pos.CENTER);
        titleLabel.getStyleClass().add("title-label");

        Button detailsButton = new Button("Voir Détails");
        detailsButton.setOnAction(event -> showOeuvreDetails(oeuvre));
        detailsButton.getStyleClass().add("details-button");

        HBox actions = new HBox(5);
        actions.setAlignment(javafx.geometry.Pos.CENTER);
        Button editButton = new Button("Modifier");
        editButton.getStyleClass().add("edit-button");
        editButton.setOnAction(event -> handleEditOeuvre(oeuvre));
        Button proposeButton = new Button("Proposer");
        proposeButton.getStyleClass().add("propose-button");
        proposeButton.setOnAction(event -> showProposeToGalleryDialog(oeuvre));
        actions.getChildren().addAll(editButton, proposeButton);

        card.getChildren().addAll(imageView, titleLabel, detailsButton, actions);

        return card;
    }


    @FXML
    private void handleChooseImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choisir une image");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg", "*.gif"));
        Stage stage = (Stage) oeuvreImageView.getScene().getWindow();
        File selectedFile = fileChooser.showOpenDialog(stage);

        if (selectedFile != null) {
            copyImage(selectedFile);
        }
    }

    private void copyImage(File selectedFile) {
        Path sourcePath = selectedFile.toPath();
        Path imagesDir = Paths.get(IMAGES_DIRECTORY);
        if (!Files.exists(imagesDir)) {
            try {
                Files.createDirectories(imagesDir);
            } catch (IOException e) {
                showError("Erreur création dossier images: " + e.getMessage());
                return;
            }
        }
        String newFileName = System.currentTimeMillis() + "_" + selectedFile.getName();
        Path destPath = imagesDir.resolve(newFileName);

        Task<Void> copyTask = new Task<>() {
            @Override
            protected Void call() throws Exception {
                Files.copy(sourcePath, destPath, StandardCopyOption.REPLACE_EXISTING);
                selectedImagePath = destPath.toAbsolutePath().toString();
                return null;
            }
        };

        copyTask.setOnSucceeded(event -> {
            try {
                Image image = new Image("file:" + selectedImagePath);
                oeuvreImageView.setImage(image);
                // No need to refresh table here, image is in the form
            } catch (Exception e) {
                showError("Erreur de chargement de l'image: " + e.getMessage());
            }
        });

        copyTask.setOnFailed(event -> {
            showError("Erreur copie image: " + copyTask.getException().getMessage());
        });

        executorService.execute(copyTask);
    }

    @FXML
    private void handleAddOeuvre() {
        String title = titleField.getText().trim();
        String description = descriptionField.getText().trim();

        if (title.isEmpty() || description.isEmpty()) {
            showError("Veuillez remplir tous les champs.");
            return;
        }

        Task<Void> saveTask;

        if (currentlyEditingOeuvre != null) {
            // Update existing oeuvre
            currentlyEditingOeuvre.setTitle(title);
            currentlyEditingOeuvre.setDescription(description);
            currentlyEditingOeuvre.setImagePath(selectedImagePath);

            saveTask = new Task<>() {
                @Override
                protected Void call() throws Exception {
                    oeuvreService.updateOeuvre(currentlyEditingOeuvre);
                    return null;
                }
            };
        } else {
            // Create new oeuvre
            Oeuvre newOeuvre = new Oeuvre(0, title, description, false, selectedImagePath, " " + currentUserId, null, currentUserId, null, null);

            saveTask = new Task<>() {
                @Override
                protected Void call() throws Exception {
                    oeuvreService.createOeuvre(newOeuvre, currentUserId);
                    return null;
                }
            };
        }

        saveTask.setOnSucceeded(event -> {
            String message = currentlyEditingOeuvre != null ?
                    "Oeuvre '" + title + "' modifiée !" :
                    "Oeuvre '" + title + "' ajoutée !";

            showInfo(message);
            clearForm();
            loadInitialOeuvresForCards(); // Reload for cards

            // Reset editing state
            currentlyEditingOeuvre = null;
            addOeuvreButton.setText("Ajouter Oeuvre");
            showOeuvresContent(); // Switch back to Oeuvres view after adding/editing
        });

        saveTask.setOnFailed(event -> {
            showError("Erreur:" + saveTask.getException().getMessage());
        });

        executorService.execute(saveTask);
    }

    private void handleEditOeuvre(Oeuvre oeuvre) {
        currentlyEditingOeuvre = oeuvre; // Store the oeuvre being edited
        titleField.setText(oeuvre.getTitle());
        descriptionField.setText(oeuvre.getDescription());
        selectedImagePath = oeuvre.getImagePath();

        // Change the button text to "Modifier" when in edit mode
        addOeuvreButton.setText("Modifier Oeuvre");
        showAddOeuvreForm(); // Switch to the Add Oeuvre form

        if (selectedImagePath != null) {
            try {
                oeuvreImageView.setImage(new Image("file:" + selectedImagePath, 100, 100, true, true));
            } catch (Exception e) {
                oeuvreImageView.setImage(null);
            }
        } else {
            oeuvreImageView.setImage(null);
        }
    }

    private void showProposeToGalleryDialog(Oeuvre oeuvre) {
        Task<List<Galerie>> loadGalleriesTask = new Task<>() {
            @Override
            protected List<Galerie> call() throws Exception {
                return galerieService.getAllGaleries();
            }
        };

        loadGalleriesTask.setOnSucceeded(event -> {
            List<Galerie> galleries = loadGalleriesTask.getValue();
            Dialog<Galerie> dialog = new Dialog<>();
            dialog.setTitle("Proposer à une galerie");
            dialog.setHeaderText("Choisissez une galerie pour '" + oeuvre.getTitle() + "'");

            ListView<Galerie> galleryListView = new ListView<>();
            galleryListView.setItems(FXCollections.observableArrayList(galleries));
            galleryListView.setCellFactory(param -> new ListCell<>() {
                @Override
                protected void updateItem(Galerie galerie, boolean empty) {
                    super.updateItem(galerie, empty);
                    if (empty || galerie == null) {
                        setText(null);
                    } else {
                        setText(galerie.getName());
                    }
                }
            });

            VBox content = new VBox(10, new Label("Sélectionnez une galerie :"), galleryListView);
            dialog.getDialogPane().setContent(content);

            ButtonType proposeButtonType = new ButtonType("Proposer", ButtonBar.ButtonData.OK_DONE);
            dialog.getDialogPane().getButtonTypes().addAll(proposeButtonType, ButtonType.CANCEL);

            dialog.setResultConverter(button -> {
                if (button == proposeButtonType) {
                    return galleryListView.getSelectionModel().getSelectedItem();
                }
                return null;
            });

            Optional<Galerie> result = dialog.showAndWait();
            result.ifPresent(selectedGalerie -> {
                assignationRequestService.createRequest(oeuvre.getId(), selectedGalerie.getId(), currentUserId);
                showInfo("Assignation envoyée à '" + selectedGalerie.getName() + "' !");
            });
        });

        loadGalleriesTask.setOnFailed(event -> {
            showError("Erreur chargement galeries: " + loadGalleriesTask.getException().getMessage());
        });

        executorService.execute(loadGalleriesTask);
    }

    private void clearForm() {
        titleField.clear();
        descriptionField.clear();
        oeuvreImageView.setImage(null);
        selectedImagePath = null;
        currentlyEditingOeuvre = null;
        addOeuvreButton.setText("Ajouter Oeuvre");
    }

    private void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void shutdown() {
        executorService.shutdown();
    }
}