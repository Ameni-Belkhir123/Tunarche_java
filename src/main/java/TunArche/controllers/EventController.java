package TunArche.controllers;

import TunArche.entities.UserSession;
import TunArche.services.EventImpl;
import TunArche.entities.Event;
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
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class EventController {

    @FXML
    private TextField tfName, tfPlace, tfPrice, tfTotalTickets, tfSearch;

    @FXML
    private TextArea taDescription;

    @FXML
    private DatePicker dpStartDate, dpEndDate;

    @FXML
    private TableView<Event> tableEvent;

    @FXML
    private TableColumn<Event, String> colName, colPlace, colDescription, colPrice, colTotalTickets, colSoldTickets;

    @FXML
    private TableColumn<Event, LocalDate> colStartDate, colEndDate;
    @FXML
    private Button btnUtilisateurs;
    @FXML
    private Button btnStats; // Button for statistics
    @FXML
    private Button btnBillets;
    @FXML
    private Button deco;
    private ObservableList<Event> eventList = FXCollections.observableArrayList();
    @FXML
    private MenuItem menuItemUser;
    public void initialize() {
        // Load event cards (if applicable)
        if (UserSession.getCurrentUser() != null) {
            String fullName = UserSession.getCurrentUser().getName() + " " + UserSession.getCurrentUser().getLastName();
            menuItemUser.setText("👤 " + fullName);
        } else {
            String fullName = UserSession.getCurrentUser().getName() + " " + UserSession.getCurrentUser().getLastName();
            menuItemUser.setText("👤 " + fullName);
        }
        loadEventCards();

        // When a table row is selected, fill the form with its data
        tableEvent.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                tfName.setText(newValue.getNameEvent());
                tfPlace.setText(newValue.getPlaceEvent());
                taDescription.setText(newValue.getDescription());
                tfPrice.setText(String.valueOf(newValue.getPrice()));
                tfTotalTickets.setText(String.valueOf(newValue.getTotalTickets()));

                // Directly set LocalDate values to DatePicker
                dpStartDate.setValue(newValue.getDateStart());
                dpEndDate.setValue(newValue.getDateEnd());
            }
        });

        // Set up columns for the table view
        colName.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNameEvent()));
        colPlace.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getPlaceEvent()));
        colDescription.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDescription()));
        colPrice.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getPrice())));
        colTotalTickets.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getTotalTickets())));
        colSoldTickets.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getSoldTickets())));
        colStartDate.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getDateStart()));
        colEndDate.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getDateEnd()));

        // Format the date columns for better display
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        colStartDate.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(formatter.format(item));
                }
            }
        });
        colEndDate.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(formatter.format(item));
                }
            }
        });

        // Load initial data into the table
        loadEventData();

        // Bind event list to the table view
        tableEvent.setItems(eventList);
    }

    @FXML
    private void handleAdd() {
        String name = tfName.getText();
        String place = tfPlace.getText();
        String description = taDescription.getText();
        String price = tfPrice.getText();
        String totalTickets = tfTotalTickets.getText();

        LocalDate startDate = dpStartDate.getValue();
        LocalDate endDate = dpEndDate.getValue();

        if (!validateForm()) return;

        // Create a new Event object (soldTickets initially 0 for new events)
        int tickets = Integer.parseInt(totalTickets);
        Event newEvent = new Event(0, name, startDate, endDate, place, description, Integer.parseInt(price), tickets, 0);

        // Add to the event list and update the table
        eventList.add(newEvent);
        EventImpl eventService = new EventImpl();
        eventService.create(newEvent);

        // Clear fields after adding
        clearFields();
    }

    @FXML
    private void handleUpdate() {
        Event selectedEvent = tableEvent.getSelectionModel().getSelectedItem();

        if (selectedEvent == null) {
            showErrorMessage("Veuillez sélectionner un événement à modifier.");
            return;
        }
        if (!validateForm()) return;

        // Update event details
        selectedEvent.setNameEvent(tfName.getText());
        selectedEvent.setPlaceEvent(tfPlace.getText());
        selectedEvent.setDescription(taDescription.getText());
        selectedEvent.setPrice(Integer.parseInt(tfPrice.getText()));
        selectedEvent.setTotalTickets(Integer.parseInt(tfTotalTickets.getText()));
        // Note: soldTickets is not updated via the form; it should be managed separately (e.g., via billet reservations)
        selectedEvent.setDateStart(dpStartDate.getValue());
        selectedEvent.setDateEnd(dpEndDate.getValue());

        // Refresh the table
        tableEvent.refresh();
        EventImpl eventService = new EventImpl();
        eventService.update(selectedEvent);
        clearFields();
    }

    @FXML
    private void handleDelete() {
        Event selectedEvent = tableEvent.getSelectionModel().getSelectedItem();

        if (selectedEvent == null) {
            showErrorMessage("Veuillez sélectionner un événement à supprimer.");
            return;
        }

        // Confirmer la suppression
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation de suppression");
        alert.setHeaderText(null);
        alert.setContentText("Êtes-vous sûr de vouloir supprimer cet événement ?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            // Supprimer de la base de données
            EventImpl eventService = new EventImpl();
            eventService.delete(selectedEvent.getId());

            // Supprimer de la TableView
            eventList.remove(selectedEvent);

            showSuccessMessage("L'événement a été supprimé avec succès.");
        }
    }

    @FXML
    private void handleStats() {
        // Calculate statistics from the eventList
        int totalTickets = 0;
        int totalSoldTickets = 0;

        for (Event event : eventList) {
            totalTickets += event.getTotalTickets();
            totalSoldTickets += event.getSoldTickets();
        }

        int totalRemainingTickets = totalTickets - totalSoldTickets;

        // Create data for the pie chart
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList(
                new PieChart.Data("Tickets Vendus (" + totalSoldTickets + ")", totalSoldTickets),
                new PieChart.Data("Tickets Restants (" + totalRemainingTickets + ")", totalRemainingTickets)
        );

        // Create the pie chart
        PieChart pieChart = new PieChart(pieChartData);
        pieChart.setTitle("Statistiques des Tickets");
        pieChart.setPrefSize(400, 300);

        // Customize colors for the pie chart slices
        int index = 0;
        for (PieChart.Data data : pieChart.getData()) {
            String color = index == 0 ? "#4CAF50" : "#FF9800"; // Green for sold, Orange for remaining
            data.getNode().setStyle("-fx-pie-color: " + color + ";");
            index++;
        }

        // Add labels to show the percentage
        pieChart.setLabelsVisible(true);
        pieChart.setLabelLineLength(10);

        // Additional statistics as text
        int totalEvents = eventList.size();
        double percentageSold = totalTickets > 0 ? (double) totalSoldTickets / totalTickets * 100 : 0;
        String statsText = String.format(
                "Nombre total d'événements: %d\n" +
                        "Nombre total de tickets: %d\n" +
                        "Pourcentage de tickets vendus: %.2f%%",
                totalEvents, totalTickets, percentageSold
        );

        Label statsLabel = new Label(statsText);
        statsLabel.setStyle("-fx-font-size: 14px; -fx-padding: 10px;");

        // Create a VBox to hold the pie chart and additional stats
        VBox chartContainer = new VBox(10);
        chartContainer.getChildren().addAll(pieChart, statsLabel);
        chartContainer.setStyle("-fx-background-color: #f5f5f5; -fx-padding: 20px; -fx-alignment: center;");

        // Display the chart in a new window
        Stage stage = new Stage();
        stage.setTitle("Statistiques des Événements");
        stage.setScene(new Scene(chartContainer, 450, 400));
        stage.show();
    }

    private void showSuccessMessage(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Succès");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private boolean validateForm() {
        String name = tfName.getText().trim();
        String place = tfPlace.getText().trim();
        String description = taDescription.getText().trim();
        String priceStr = tfPrice.getText().trim();
        String totalTicketsStr = tfTotalTickets.getText().trim();

        LocalDate startDate = dpStartDate.getValue();
        LocalDate endDate = dpEndDate.getValue();

        EventImpl eventService = new EventImpl();
        if (eventService.eventNameExists(name)) {
            showErrorMessage("Un événement avec ce nom existe déjà.");
            return false;
        }

        if (name.isEmpty() || place.isEmpty() || description.isEmpty()
                || priceStr.isEmpty() || totalTicketsStr.isEmpty()
                || startDate == null || endDate == null) {
            showErrorMessage("Tous les champs doivent être remplis.");
            return false;
        }

        if (!place.matches("[a-zA-ZÀ-ÿ0-9\\s\\-]+")) {
            showErrorMessage("Le lieu ne doit contenir que des lettres et chiffres.");
            return false;
        }

        if (description.length() < 10) {
            showErrorMessage("La description doit contenir au moins 10 caractères.");
            return false;
        }

        try {
            int price = Integer.parseInt(priceStr);
            if (price < 0) {
                showErrorMessage("Le prix ne peut pas être négatif.");
                return false;
            }
        } catch (NumberFormatException e) {
            showErrorMessage("Le prix doit être un nombre valide.");
            return false;
        }

        try {
            int totalTickets = Integer.parseInt(totalTicketsStr);
            if (totalTickets <= 0) {
                showErrorMessage("Le nombre total de tickets doit être supérieur à zéro.");
                return false;
            }
        } catch (NumberFormatException e) {
            showErrorMessage("Les tickets doivent être des nombres valides.");
            return false;
        }

        if (endDate.isBefore(startDate)) {
            showErrorMessage("La date de fin ne peut pas être avant la date de début.");
            return false;
        }

        return true;
    }

    @FXML
    private VBox eventContainer; // Assure-toi qu'il est bien lié dans le FXML

    private void loadEventCards() {
        if (eventContainer == null) {
            System.err.println("eventContainer is not initialized. Check FXML binding.");
            return;
        }

        EventImpl eventService = new EventImpl();
        for (Event event : eventService.getAll()) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/tunarche/eventCard.fxml"));
                Parent card = loader.load();

                EventCardController controller = loader.getController();
                controller.setData(event);

                eventContainer.getChildren().add(card);

            } catch (IOException e) {
                System.err.println("Erreur lors du chargement de l'event card:");
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void handleSearch(KeyEvent event) {
        String searchText = tfSearch.getText().toLowerCase();

        // Filter the event list based on the search text
        ObservableList<Event> filteredList = FXCollections.observableArrayList();

        for (Event eventObj : eventList) {
            if (eventObj.getNameEvent().toLowerCase().contains(searchText) ||
                    eventObj.getPlaceEvent().toLowerCase().contains(searchText) ||
                    String.valueOf(eventObj.getPrice()).toLowerCase().contains(searchText)) {
                filteredList.add(eventObj);
            }
        }

        tableEvent.setItems(filteredList);
    }

    private void loadEventData() {
        EventImpl eventService = new EventImpl();
        eventList = FXCollections.observableArrayList(eventService.getAll());
        tableEvent.setItems(eventList);
    }

    private void clearFields() {
        tfName.clear();
        tfPlace.clear();
        taDescription.clear();
        tfPrice.clear();
        tfTotalTickets.clear();
        dpStartDate.setValue(null);
        dpEndDate.setValue(null);
    }

    private void showErrorMessage(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }




    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

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
    private Button eventbutton;
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