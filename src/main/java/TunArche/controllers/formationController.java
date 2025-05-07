package TunArche.controllers;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.*;

import TunArche.entities.UserSession;
import TunArche.services.GeminiChatbot;


import TunArche.entities.Evaluation;
import TunArche.services.EvaluationImpl;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;

import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import TunArche.entities.Formation;
import TunArche.services.FormationImpl;

import java.io.File;
import java.util.regex.Pattern;


import javafx.scene.control.*;
import javafx.stage.Stage;


public class formationController {

    @FXML private ResourceBundle resources;
    @FXML private URL location;

    // Form fields
    @FXML private DatePicker datedebid;
    @FXML private DatePicker datefinid;
    @FXML private TextArea descriptionid;
    @FXML private TextField imgnamesho;
    @FXML private TextField nbrplacesid;
    @FXML private Button imgselect;
    @FXML private TextField linkid;
    @FXML private TextField titreid;
    @FXML
    private MenuItem menuItemUser;
    // Buttons
    @FXML private Button btnUpdate;
    @FXML private Button btnDelete;

    // Table components
    @FXML private TableView<Formation> tableFormations;
    @FXML private TableColumn<Formation, Integer> colId;
    @FXML private TableColumn<Formation, String> colTitre;
    @FXML private TableColumn<Formation, LocalDate> colDateDebut; // Changé en LocalDate
    @FXML private TableColumn<Formation, LocalDate> colDateFin;   // Changé en LocalDate
    @FXML private TableColumn<Formation, Integer> colPlaces;
    @FXML private TableColumn<Formation, String> colImage;

    private File selectedImageFile;
    private Formation selectedFormation;
    private FormationImpl formationImpl = new FormationImpl();
    private EvaluationImpl evaluationImpl = new EvaluationImpl();
    private ObservableList<Formation> formationsList = FXCollections.observableArrayList();

    @FXML
    void initialize() { if (UserSession.getCurrentUser() != null) {
        String fullName = UserSession.getCurrentUser().getName() + " " + UserSession.getCurrentUser().getLastName();
        menuItemUser.setText("👤 " + fullName);
    } else {
        String fullName = UserSession.getCurrentUser().getName() + " " + UserSession.getCurrentUser().getLastName();
        menuItemUser.setText("👤 " + fullName);
    }
        // Initialize table columns
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colTitre.setCellValueFactory(new PropertyValueFactory<>("titre"));
        colDateDebut.setCellValueFactory(new PropertyValueFactory<>("datedebut")); // Utilise LocalDate directement
        colDateFin.setCellValueFactory(new PropertyValueFactory<>("datefin"));     // Utilise LocalDate directement
        colPlaces.setCellValueFactory(new PropertyValueFactory<>("nbrplaces"));
        colImage.setCellValueFactory(new PropertyValueFactory<>("image_name"));
        // Load data into table
        loadFormationsData();

        // Add listener for table selection
        tableFormations.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> {
                    if (newSelection != null) {
                        selectedFormation = newSelection;
                        btnUpdate.setDisable(false);
                        btnDelete.setDisable(false);
                        populateFormFields(selectedFormation);
                    } else {
                        selectedFormation = null;
                        btnUpdate.setDisable(true);
                        btnDelete.setDisable(true);
                    }
                }
        );
    }

    private void loadFormationsData() {
        formationsList.clear();
        formationsList.addAll(formationImpl.showAll());
        tableFormations.setItems(formationsList);
    }

    private void populateFormFields(Formation formation) {
        titreid.setText(formation.getTitre());
        descriptionid.setText(formation.getDescription());

        // Gestion des dates nulles
        datedebid.setValue(formation.getDatedebut() != null ? formation.getDatedebut() : null);
        datefinid.setValue(formation.getDatefin() != null ? formation.getDatefin() : null);

        linkid.setText(formation.getLink());
        nbrplacesid.setText(String.valueOf(formation.getNbrplaces()));
        imgnamesho.setText(formation.getImage_name());
    }

    @FXML
    void saveFormation(ActionEvent event) {
        if (!validateInputs()) {
            return;
        }

        try {
            // Création de l'objet Formation pour vérification
            Formation nouvelleFormation = new Formation();
            nouvelleFormation.setTitre(titreid.getText().trim());
            nouvelleFormation.setDescription(descriptionid.getText().trim());
            nouvelleFormation.setDatedebut(datedebid.getValue()); // LocalDate directement
            nouvelleFormation.setDatefin(datefinid.getValue());   // LocalDate directement
            nouvelleFormation.setLink(linkid.getText().trim());
            nouvelleFormation.setNbrplaces(Integer.parseInt(nbrplacesid.getText().trim()));

            // Vérification des doublons
            if (formationExists(nouvelleFormation)) {
                showAlert(Alert.AlertType.WARNING, "Doublon détecté",
                        "Une formation identique existe déjà avec :\n" +
                                "- Même titre\n" +
                                "- Même description\n" +
                                "- Mêmes dates\n" +
                                "- Même lien");
                return;
            }

            // Si pas de doublon, procéder à l'ajout
            nouvelleFormation.setImage_name(selectedImageFile.getName());
            nouvelleFormation.setImage_size((int) selectedImageFile.length());
            nouvelleFormation.setUpdated_at(LocalDate.now()); // LocalDate.now() au lieu de new Date()

            formationImpl.create(nouvelleFormation);

            showAlert(Alert.AlertType.INFORMATION, "✅ Succès", "Formation ajoutée avec succès !");
            resetForm();
            loadFormationsData();

        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "❌ Erreur", "Erreur lors de l'ajout : " + e.getMessage());
        }
    }

    private boolean formationExists(Formation formation) {
        for (Formation f : formationImpl.showAll()) {
            if (f.getTitre().equalsIgnoreCase(formation.getTitre()) &&
                    f.getDescription().equalsIgnoreCase(formation.getDescription()) &&
                    f.getDatedebut().equals(formation.getDatedebut()) &&
                    f.getDatefin().equals(formation.getDatefin()) &&
                    f.getLink().equalsIgnoreCase(formation.getLink())) {
                return true;
            }
        }
        return false;
    }

    @FXML
    void handleUpdate(ActionEvent event) {
        if (selectedFormation == null) {
            showAlert(Alert.AlertType.WARNING, "Aucune sélection", "Veuillez sélectionner une formation à modifier.");
            return;
        }

        if (!validateInputs()) {
            return;
        }

        try {
            // Conversion des dates à partir des DatePicker (déjà bien gérée)
            // Utilisation directe de LocalDate
            LocalDate newDebut = datedebid.getValue();
            LocalDate newFin = datefinid.getValue();


            // Vérification des doublons (en excluant la formation en cours)
            Formation updatedFormation = new Formation();
            updatedFormation.setTitre(titreid.getText().trim());
            updatedFormation.setDescription(descriptionid.getText().trim());
            updatedFormation.setDatedebut(newDebut);
            updatedFormation.setDatefin(newFin);
            updatedFormation.setLink(linkid.getText().trim());

            for (Formation f : formationImpl.showAll()) {
                if (f.getId() != selectedFormation.getId() &&
                        f.getTitre().equalsIgnoreCase(updatedFormation.getTitre()) &&
                        f.getDescription().equalsIgnoreCase(updatedFormation.getDescription()) &&
                        f.getDatedebut().equals(updatedFormation.getDatedebut()) &&
                        f.getDatefin().equals(updatedFormation.getDatefin()) &&
                        f.getLink().equalsIgnoreCase(updatedFormation.getLink())) {

                    showAlert(Alert.AlertType.WARNING, "Doublon détecté",
                            "Une formation identique existe déjà avec ces informations.");
                    return;
                }
            }

            // Si confirmé par l'utilisateur
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmation de modification");
            alert.setHeaderText("Modifier la formation");
            alert.setContentText("Êtes-vous sûr de vouloir modifier cette formation ?");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                // Mettre à jour la formation sélectionnée
                selectedFormation.setTitre(updatedFormation.getTitre());
                selectedFormation.setDescription(updatedFormation.getDescription());
                selectedFormation.setDatedebut(newDebut);
                selectedFormation.setDatefin(newFin);
                selectedFormation.setLink(updatedFormation.getLink());
                selectedFormation.setNbrplaces(Integer.parseInt(nbrplacesid.getText().trim()));

                if (selectedImageFile != null) {
                    selectedFormation.setImage_name(selectedImageFile.getName());
                    selectedFormation.setImage_size((int) selectedImageFile.length());
                }

                selectedFormation.setUpdated_at(LocalDate.now()); // LocalDate.now()

                formationImpl.update(selectedFormation);

                showAlert(Alert.AlertType.INFORMATION, "✅ Succès", "Formation modifiée avec succès !");
                resetForm();
                loadFormationsData();
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "❌ Erreur", "Erreur lors de la modification : " + e.getMessage());
        }
    }

    @FXML
    void handleDelete(ActionEvent event) {
        if (selectedFormation == null) {
            showAlert(Alert.AlertType.WARNING, "Aucune sélection", "Veuillez sélectionner une formation à supprimer.");
            return;
        }

        // Vérifier si la formation a des commentaires
        List<Evaluation> evaluations = evaluationImpl.showByFormation(selectedFormation.getId());
        boolean hasComments = !evaluations.isEmpty();

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation de suppression");
        alert.setHeaderText("Supprimer la formation");

        if (hasComments) {
            alert.setContentText("⚠ Attention ⚠\n\n" +
                    "Cette formation contient " + evaluations.size() + " commentaire(s).\n\n" +
                    "En confirmant, vous supprimerez également :\n" +
                    "- Tous les commentaires associés\n" +
                    "- Toutes les notes d'évaluation\n\n" +
                    "Voulez-vous vraiment continuer ?");
        } else {
            alert.setContentText("Êtes-vous sûr de vouloir supprimer cette formation ?\n\n" +
                    "Titre: " + selectedFormation.getTitre());
        }

        ButtonType buttonTypeYes = new ButtonType("Oui, supprimer", ButtonBar.ButtonData.YES);
        ButtonType buttonTypeNo = new ButtonType("Non, annuler", ButtonBar.ButtonData.NO);
        alert.getButtonTypes().setAll(buttonTypeYes, buttonTypeNo);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == buttonTypeYes) {
            try {
                if (hasComments) {
                    // Supprimer d'abord les commentaires
                    evaluationImpl.deleteByFormation(selectedFormation.getId());
                }
                // Puis supprimer la formation
                formationImpl.delete(selectedFormation.getId());

                showAlert(Alert.AlertType.INFORMATION, "✅ Succès",
                        hasComments ? "Formation et ses commentaires supprimés avec succès !"
                                : "Formation supprimée avec succès !");
                resetForm();
                loadFormationsData();
            } catch (Exception e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "❌ Erreur",
                        "Erreur lors de la suppression : " + e.getMessage());
            }
        }
    }

    @FXML
    void selectImage(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Sélectionner une image");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg")
        );
        File file = fileChooser.showOpenDialog(((Button)event.getSource()).getScene().getWindow());
        if (file != null) {
            selectedImageFile = file;
            imgnamesho.setText(file.getName());
        }
    }

    @FXML
    void resetForm(ActionEvent event) {
        resetForm();
    }

    private boolean validateInputs() {
        // Validation des champs obligatoires
        if (titreid.getText().trim().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Champ requis", "Le titre est obligatoire.");
            return false;
        }
        if (descriptionid.getText().trim().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Champ requis", "La description est obligatoire.");
            return false;
        }
        if (datedebid.getValue() == null) {
            showAlert(Alert.AlertType.WARNING, "Champ requis", "La date de début est obligatoire.");
            return false;
        }
        if (datefinid.getValue() == null) {
            showAlert(Alert.AlertType.WARNING, "Champ requis", "La date de fin est obligatoire.");
            return false;
        }
        if (linkid.getText().trim().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Champ requis", "Le lien est obligatoire.");
            return false;
        }
        if (nbrplacesid.getText().trim().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Champ requis", "Le nombre de places est obligatoire.");
            return false;
        }

        // Validation des dates
        if (datedebid.getValue().isAfter(datefinid.getValue())) {
            showAlert(Alert.AlertType.WARNING, "Dates incorrectes", "La date de début doit être avant la date de fin.");
            return false;
        }

        // Validation du lien URL
        if (!isValidURL(linkid.getText().trim())) {
            showAlert(Alert.AlertType.WARNING, "Lien invalide", "Veuillez entrer un lien valide (commençant par http:// ou https://).");
            return false;
        }

        // Validation du nombre de places
        // Validation du nombre de places
        try {
            int nbrPlaces = Integer.parseInt(nbrplacesid.getText().trim());
            if (nbrPlaces <= 0) {
                showAlert(Alert.AlertType.WARNING, "Valeur invalide", "Le nombre de places doit être un entier positif.");
                return false;
            }
            if (nbrPlaces > 30) {
                showAlert(Alert.AlertType.WARNING, "Valeur invalide", "Le nombre de places ne peut pas dépasser 30.");
                return false;
            }
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.WARNING, "Format invalide", "Le nombre de places doit être un nombre entier.");
            return false;
        }

        // Validation de l'image (sauf pour modification sans changement d'image)
        if (selectedImageFile == null && selectedFormation == null) {
            showAlert(Alert.AlertType.WARNING, "Image manquante", "Veuillez sélectionner une image.");
            return false;
        }

        return true;
    }

    //valider le link
    private boolean isValidURL(String url) {
        String regex = "^(https?://|www\\.).+";
        return Pattern.matches(regex, url);
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void resetForm() {
        titreid.clear();
        descriptionid.clear();
        datedebid.setValue(null);
        datefinid.setValue(null);
        linkid.clear();
        nbrplacesid.clear();
        imgnamesho.clear();
        selectedImageFile = null;
        selectedFormation = null;
        tableFormations.getSelectionModel().clearSelection();
        btnUpdate.setDisable(true);
        btnDelete.setDisable(true);
    }

    @FXML
    private void goToFront(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/tunarche/formation.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Formations - Vue Front");
            stage.setScene(new Scene(root));
            stage.show();

            // Fermer l'ancienne fenêtre (optionnel)
            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            currentStage.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Méthode pour générer une description à partir du titre
    @FXML
    private void generateDescription(ActionEvent event) {
        String titre = titreid.getText().trim();
        if (!titre.isEmpty()) {
            String prompt = "Donne-moi une description professionnelle pour une formation intitulée : " + titre;
            String result = GeminiChatbot.askGemini(prompt);
            descriptionid.setText(result);
        } else {
            System.out.println("Titre vide !");
        }
    }
    @FXML private TextField userInput;
    @FXML
    private VBox chatBox;
    @FXML private AnchorPane chatbotContainer;
    @FXML private ScrollPane chatScrollPane;
    @FXML



    private String lastGeneratedTitle = "";
    private String lastGeneratedDescription = "";


    // Ouvrir le chatbot lorsqu'on clique sur Exemple
    @FXML
    private void openChatbot(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/tunarche/chatbot.fxml"));
            Parent root = loader.load();

            ChatbotController chatbotController = loader.getController();
            chatbotController.setFormationController(this); // ⬅ Lien avec le formulaire !

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Assistant Chatbot");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setChatbotData(String title, String description) {
        this.lastGeneratedTitle = title;
        this.lastGeneratedDescription = description;
        insertToMainForm(); // met à jour les champs
    }




    // Insérer les données dans le formulaire
    private void insertToMainForm() {
        titreid.setText(lastGeneratedTitle);
        descriptionid.setText(lastGeneratedDescription);
    }


    public void goToEvaluations(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/tunarche/evaluations.fxml"));
            Parent root = loader.load();


            // Obtenir la scène actuelle et remplacer la racine
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    private Button eventbutton;
    private void showAlert(String title, String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText("Information");
        alert.setContentText(msg);
        alert.showAndWait();
    }
    public void handelevent(ActionEvent actionEvent) {
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
    private Button btnBillets;
    @FXML
    public void handebillet(ActionEvent actionEvent) {
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
    public void handleCommantaire(ActionEvent actionEvent) {
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
    @FXML
    public void handlePublication(ActionEvent actionEvent) {
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

    public void handleLogout(ActionEvent actionEvent) {
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

