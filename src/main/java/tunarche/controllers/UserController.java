package TunArche.controllers;

import TunArche.entities.user;
import TunArche.services.userimpl;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.time.ZoneId;
import java.util.Date;

public class UserController {

    @FXML private TextField tfName, tfLastName, tfEmail, tfPassword, tfVerificationToken;
    @FXML private ComboBox<String> cbRole;
    @FXML private CheckBox chkVerified;
    @FXML private DatePicker dpCodeSentAt;
    @FXML private ListView<user> listViewUsers;

    private final userimpl userService = new userimpl();
    private ObservableList<user> userList;

    @FXML
    public void initialize() {
        cbRole.setItems(FXCollections.observableArrayList("Admin", "User", "Artist"));
        loadUsers();
        listViewUsers.getSelectionModel().selectedItemProperty().addListener((obs, oldUser, selectedUser) -> populateFields(selectedUser));
    }

    @FXML
    public void loadUsers() {
        userList = FXCollections.observableArrayList(userService.showAll());
        listViewUsers.setItems(userList);
    }

    @FXML
    public void handleAdd() {
        try {
            user newUser = getUserFromForm(null);
            userService.create(newUser);
            loadUsers();
            clearForm();
        } catch (Exception e) {
            showAlert("Erreur", "Erreur lors de l'ajout : " + e.getMessage());
        }
    }

    @FXML
    public void handleUpdate() {
        user selected = listViewUsers.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Erreur", "Aucun utilisateur sélectionné.");
            return;
        }
        try {
            user updated = getUserFromForm(selected.getId());
            userService.update(updated);
            loadUsers();
            clearForm();
        } catch (Exception e) {
            showAlert("Erreur", "Erreur lors de la mise à jour : " + e.getMessage());
        }
    }

    @FXML
    public void handleDelete() {
        user selected = listViewUsers.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Erreur", "Aucun utilisateur sélectionné.");
            return;
        }
        try {
            userService.delete(selected.getId());
            loadUsers();
            clearForm();
        } catch (Exception e) {
            showAlert("Erreur", "Erreur lors de la suppression : " + e.getMessage());
        }
    }

    private void populateFields(user selectedUser) {
        if (selectedUser != null) {
            tfName.setText(selectedUser.getName());
            tfLastName.setText(selectedUser.getLastName());
            tfEmail.setText(selectedUser.getEmail());
            tfPassword.setText(""); // Do not populate password
            cbRole.setValue(selectedUser.getRole());
            chkVerified.setSelected(selectedUser.isVerified());
            tfVerificationToken.setText(selectedUser.getVerificationToken());
            if (selectedUser.getCodeSentAt() != null) {
                dpCodeSentAt.setValue(selectedUser.getCodeSentAt().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
            } else {
                dpCodeSentAt.setValue(null);
            }
        }
    }

    private user getUserFromForm(Integer id) {
        return new user(
                id,
                tfName.getText().trim(),
                tfLastName.getText().trim(),
                tfEmail.getText().trim(),
                tfPassword.getText().trim(),
                cbRole.getValue(),
                chkVerified.isSelected(),
                tfVerificationToken.getText().trim(),
                dpCodeSentAt.getValue() != null ? Date.from(dpCodeSentAt.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant()) : null
        );
    }

    private void clearForm() {
        tfName.clear();
        tfLastName.clear();
        tfEmail.clear();
        tfPassword.clear();
        tfVerificationToken.clear();
        cbRole.setValue(null);
        chkVerified.setSelected(false);
        dpCodeSentAt.setValue(null);
    }

    private void showAlert(String title, String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
