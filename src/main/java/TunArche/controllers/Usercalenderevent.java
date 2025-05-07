package TunArche.controllers;

import TunArche.entities.Event;
import TunArche.entities.PdfGenerator;
import TunArche.entities.UserSession;
import TunArche.services.CalendarService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Usercalenderevent {

    @FXML
    private GridPane calendarGrid;
    @FXML
    private GridPane miniCalendarGrid;
    @FXML
    private Label monthLabel;
    @FXML
    private Label miniMonthLabel;
    private LocalDate currentDate = LocalDate.now();
    private final CalendarService calendarService = new CalendarService();
    @FXML
    private MenuItem menuItemUser;
    @FXML
    public void initialize() {
        if (UserSession.getCurrentUser() != null) {
            String fullName = UserSession.getCurrentUser().getName() + " " + UserSession.getCurrentUser().getLastName();
            menuItemUser.setText("👤 " + fullName);


        } else {
            menuItemUser.setText(UserSession.getCurrentUser().getName() + " " + UserSession.getCurrentUser().getLastName());
            System.err.println("⚠️ Aucun utilisateur connecté !");
        }
        displayCalendar(currentDate);
        displayMiniCalendar(currentDate);
    }

    public void displayCalendar(LocalDate date) {
        calendarGrid.getChildren().clear();
        monthLabel.setText(date.getMonth().toString() + " " + date.getYear());

        LocalDate firstDayOfMonth = LocalDate.of(date.getYear(), date.getMonth(), 1);
        int firstDayOfWeek = firstDayOfMonth.getDayOfWeek().getValue();
        int adjustedFirstDay = (firstDayOfWeek == 7) ? 0 : firstDayOfWeek;
        LocalDate startDate = firstDayOfMonth.minusDays(adjustedFirstDay);
        int totalDaysToShow = 42; // 6x7 grid

        List<Event> events = calendarService.getUserEvents(UserSession.getCurrentUser().getId()); // Replace 40 with dynamic user ID
        LocalDate today = LocalDate.now();

        for (int i = 0; i < totalDaysToShow; i++) {
            LocalDate currentDay = startDate.plusDays(i);
            int row = (i / 7) + 1;
            int col = i % 7;

            VBox dayCell = new VBox();
            dayCell.setAlignment(Pos.TOP_LEFT);
            dayCell.getStyleClass().add("day-cell");

            if (currentDay.equals(today)) {
                dayCell.getStyleClass().add("today");
            }
            if (currentDay.getMonth() != date.getMonth()) {
                dayCell.getStyleClass().add("outside-month");
            }

            Label dayLabel = new Label(String.valueOf(currentDay.getDayOfMonth()));
            dayLabel.getStyleClass().add("day-label");

            VBox eventBox = new VBox();
            eventBox.setSpacing(5);

            // Track events on this day
            Set<String> uniqueEventKeys = new HashSet<>();
            StringBuilder tooltipText = new StringBuilder("Events on " + currentDay + ":\n");
            String highestPriorityType = null;

            for (Event event : events) {
                if (event.getDateStart() != null && event.getDateEnd() != null) {
                    // Use LocalDate directly since event.getDateStart() returns LocalDate
                    LocalDate eventStart = event.getDateStart();
                    LocalDate eventEnd = event.getDateEnd();

                    // Check if currentDay is between eventStart and eventEnd (inclusive)
                    if (!currentDay.isBefore(eventStart) && !currentDay.isAfter(eventEnd)) {
                        String nameEvent = event.getNameEvent() != null ? event.getNameEvent().trim().toLowerCase() : "";
                        String placeEvent = event.getPlaceEvent() != null ? event.getPlaceEvent().trim().toLowerCase() : "";
                        String eventKey = nameEvent + "|" + placeEvent;

                        if (!uniqueEventKeys.contains(eventKey)) {
                            uniqueEventKeys.add(eventKey);

                            Label eventNameLabel = new Label(event.getNameEvent());
                            colorEventLabel(eventNameLabel, "important");
                            eventNameLabel.setTooltip(new Tooltip(event.getNameEvent() + " at " + event.getPlaceEvent()));
                            eventBox.getChildren().add(eventNameLabel);

                            tooltipText.append("- ").append(event.getNameEvent()).append(" at ").append(event.getPlaceEvent()).append("\n");

                            if (highestPriorityType == null || comparePriority("important", highestPriorityType) > 0) {
                                highestPriorityType = "important";
                            }
                        }
                    }
                }
            }

            // Color the dayCell based on the most important event type
            if (highestPriorityType != null) {
                colorDayCell(dayCell, highestPriorityType);
            }

            // Add a tooltip to the dayCell showing all events on hover
            if (uniqueEventKeys.isEmpty()) {
                tooltipText.append("No events.");
            }
            Tooltip tooltip = new Tooltip(tooltipText.toString());
            tooltip.setStyle("-fx-font-size: 12px; -fx-background-color: #ffffff; -fx-text-fill: #333333; -fx-border-color: #cccccc;");
            Tooltip.install(dayCell, tooltip);

            dayCell.getChildren().addAll(dayLabel, eventBox);
            dayCell.setOnMouseClicked(e -> handleDatePicked(currentDay));

            calendarGrid.add(dayCell, col, row);
        }
    }

    private void colorDayCell(VBox dayCell, String eventType) {
        if (eventType == null) eventType = "";
        switch (eventType.trim().toLowerCase()) {
            case "optional":
                dayCell.setStyle("-fx-background-color: #e3f2fd;");
                break;
            case "normal":
                dayCell.setStyle("-fx-background-color: #dcedc8;");
                break;
            case "important":
                dayCell.setStyle("-fx-background-color: #fff59d;");
                break;
            case "urgent":
                dayCell.setStyle("-fx-background-color: #ef9a9a;");
                break;
            case "completed":
                dayCell.setStyle("-fx-background-color: #d7ccc8;");
                break;
            default:
                dayCell.setStyle("-fx-background-color: #f5f5f5;");
                break;
        }
    }

    private int comparePriority(String type1, String type2) {
        return getPriorityScore(type1) - getPriorityScore(type2);
    }

    private int getPriorityScore(String type) {
        if (type == null) return 0;
        switch (type.trim().toLowerCase()) {
            case "urgent": return 5;
            case "important": return 4;
            case "normal": return 3;
            case "optional": return 2;
            case "completed": return 1;
            default: return 0;
        }
    }

    private void colorEventLabel(Label eventLabel, String eventType) {
        if (eventType == null) eventType = "";
        switch (eventType.trim().toLowerCase()) {
            case "optional":
                eventLabel.setStyle("-fx-background-color: #bbdefb; -fx-text-fill: black;");
                break;
            case "normal":
                eventLabel.setStyle("-fx-background-color: #c8e6c9; -fx-text-fill: black;");
                break;
            case "important":
                eventLabel.setStyle("-fx-background-color: #fff9c4; -fx-text-fill: black;");
                break;
            case "urgent":
                eventLabel.setStyle("-fx-background-color: #ffcdd2; -fx-text-fill: black;");
                break;
            case "completed":
                eventLabel.setStyle("-fx-background-color: #d7ccc8; -fx-text-fill: black;");
                break;
            default:
                eventLabel.setStyle("-fx-background-color: #e0e0e0; -fx-text-fill: black;");
                break;
        }
    }

    public void displayMiniCalendar(LocalDate date) {
        miniCalendarGrid.getChildren().clear();
        miniMonthLabel.setText(date.getMonth().toString() + " " + date.getYear());

        LocalDate firstDayOfMonth = LocalDate.of(date.getYear(), date.getMonth(), 1);
        LocalDate lastDayOfMonth = firstDayOfMonth.withDayOfMonth(firstDayOfMonth.lengthOfMonth());
        int firstDayOfWeek = firstDayOfMonth.getDayOfWeek().getValue();
        int daysInMonth = date.lengthOfMonth();

        // Adjust for Monday-first (Mon=0, Sun=6)
        int adjustedFirstDay = (firstDayOfWeek == 7) ? 0 : firstDayOfWeek;

        // Calculate days to show from previous month
        LocalDate startDate = firstDayOfMonth.minusDays(adjustedFirstDay);
        int totalDaysToShow = 42; // 6 rows * 7 columns

        for (int i = 0; i < totalDaysToShow; i++) {
            LocalDate currentDay = startDate.plusDays(i);
            int row = i / 7;
            int col = i % 7;

            Label dayLabel = new Label(String.valueOf(currentDay.getDayOfMonth()));
            dayLabel.getStyleClass().add("mini-day-label");
            if (currentDay.getMonth() != date.getMonth()) {
                dayLabel.getStyleClass().add("outside-month");
            }
            miniCalendarGrid.add(dayLabel, col, row);
        }
    }

    @FXML
    public void showPreviousMonth() {
        currentDate = currentDate.minusMonths(1);
        displayCalendar(currentDate);
        displayMiniCalendar(currentDate);
    }

    @FXML
    public void showNextMonth() {
        currentDate = currentDate.plusMonths(1);
        displayCalendar(currentDate);
        displayMiniCalendar(currentDate);
    }

    @FXML
    public void showToday() {
        currentDate = LocalDate.now();
        displayCalendar(currentDate);
        displayMiniCalendar(currentDate);
    }

    @FXML
    public void handleDatePicked(LocalDate selectedDate) {
        System.out.println("Selected date: " + selectedDate);
        List<Event> eventsOnSelectedDate = calendarService.getUserEventsOnDate(40, selectedDate);
        if (eventsOnSelectedDate.isEmpty()) {
            System.out.println("No events on this date.");
        } else {
            Set<String> displayedEvents = new HashSet<>();
            for (Event event : eventsOnSelectedDate) {
                String eventKey = (event.getNameEvent() != null ? event.getNameEvent().trim().toLowerCase() : "") + "|" +
                        (event.getPlaceEvent() != null ? event.getPlaceEvent().trim().toLowerCase() : "");
                if (!displayedEvents.contains(eventKey)) {
                    displayedEvents.add(eventKey);
                    System.out.println("Event: " + event.getNameEvent() + " at " + event.getPlaceEvent());
                }
            }
        }
    }

    @FXML
    public void switchToMonthView() {
        displayCalendar(currentDate);
    }

    @FXML
    public void switchToWeekView() {
        System.out.println("Switch to Week View (not implemented)");
    }

    @FXML
    public void switchToDayView() {
        System.out.println("Switch to Day View (not implemented)");
    }
    @FXML
    private Button userbutton;


    @FXML
    public void handecalander() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/tunarche/usercalenderevent.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) userbutton.getScene().getWindow();
            stage.setTitle("Event Calendar");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    @FXML
    private void handlecv()
    {

        PdfGenerator.generateUserCV(UserSession.getCurrentUser());
        String f2="C:\\Users\\marwe\\Desktop\\Tun-Arche\\cv_"+UserSession.getCurrentUser().getName()+"_"+UserSession.getCurrentUser().getLastName()+".pdf";
        PdfGenerator.openPdfFile(f2);
    }

    @FXML
    private void handeleventt() {
        try {
            // Load the new FXML file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/tunarche/eventfront.fxml"));
            Parent root = loader.load();

            // Get the current stage from the button (same window)
            Stage stage = (Stage) userbutton.getScene().getWindow();

            // Set new scene
            Scene scene = new Scene(root);
            stage.setTitle("Gestion des événements");
            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleLogout() {
        UserSession.clear(); // Efface l'utilisateur connecté
        Stage stage = (Stage) userbutton.getScene().getWindow();
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
    public void handelblog() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/tunarche/DispayPublication.fxml"));
            Parent root = loader.load();

            // Get the current stage from any UI component, e.g., a button
            Stage stage = (Stage) userbutton.getScene().getWindow(); // Replace `someUiElement` with your actual node, like a button or pane
            Scene scene = new Scene(root);
            stage.setTitle("blog");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    public void handelformation(ActionEvent actionEvent) {
        try {
            if(UserSession.getCurrentUser().getRole().equals("artiste")) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/tunarche/artistFormationView.fxml"));
                Parent root = loader.load();
                Stage stage = (Stage) userbutton.getScene().getWindow(); // Replace `someUiElement` with your actual node, like a button or pane
                Scene scene = new Scene(root);
                stage.setTitle("concours");
                stage.setScene(scene);
                stage.show();
            }
            else{
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/tunarche/formation.fxml"));
                Parent root = loader.load();
                Stage stage = (Stage) userbutton.getScene().getWindow(); // Replace `someUiElement` with your actual node, like a button or pane
                Scene scene = new Scene(root);
                stage.setTitle("formation");
                stage.setScene(scene);
                stage.show();
            }



        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void handelconcours(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/tunarche/afficher_concours_grid.fxml"));
            Parent root = loader.load();

            // Get the current stage from any UI component, e.g., a button
            Stage stage = (Stage) userbutton.getScene().getWindow(); // Replace `someUiElement` with your actual node, like a button or pane

            Scene scene = new Scene(root);
            stage.setTitle("blog");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void handelprofile(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/tunarche/profileparametre.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) userbutton.getScene().getWindow(); // Replace `someUiElement` with your actual node, like a button or pane
            Scene scene = new Scene(root);
            stage.setTitle("profileparametre");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void handelhome(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/tunarche/userfront.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) userbutton.getScene().getWindow(); // Replace `someUiElement` with your actual node, like a button or pane
            Scene scene = new Scene(root);
            stage.setTitle("home");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}