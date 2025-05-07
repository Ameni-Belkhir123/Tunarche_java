package TunArche.controllers;

import TunArche.entities.Concours;
import TunArche.entities.Participation;
import TunArche.entities.UserSession;
import TunArche.services.Participationlmpl;
import TunArche.services.userimpl;
import javafx.application.Platform;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.stage.Stage;

import java.awt.Desktop;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

// For QR Code generation
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

public class ResultsViewController implements Initializable {

    @FXML
    private Label concoursNameLabel;

    @FXML
    private ImageView winnerImageView;

    @FXML
    private Label artistNameLabel;

    @FXML
    private Label artistEmailLabel;

    @FXML
    private Label votesCountLabel;

    @FXML
    private Label participationDateLabel;

    @FXML
    private ImageView qrCodeImageView;

    @FXML
    private Button btnShareResults;

    @FXML
    private TableView<Participation> topParticipantsTable;

    @FXML
    private TableColumn<Participation, Integer> rankColumn;

    @FXML
    private TableColumn<Participation, String> nameColumn;

    @FXML
    private TableColumn<Participation, Integer> votesColumn;

    @FXML
    private TableColumn<Participation, String> dateColumn;

    private final Participationlmpl participationService = new Participationlmpl();
    private Concours selectedConcours;
    private Participation winner;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // First, verify that the user has the correct role
        if (UserSession.getCurrentUser() == null ||
                !UserSession.getCurrentUser().getRole().equals("User")) {
            // If wrong role, show alert and redirect back
            Platform.runLater(() -> {
                showAlert(Alert.AlertType.WARNING, "Accès refusé",
                        "Accès refusé",
                        "Seuls les utilisateurs avec le rôle 'user' peuvent voir les résultats.");
                handleBack();
            });
            return;
        }
        userimpl user = new userimpl();
        // Setup table columns
        rankColumn.setCellValueFactory(cellData ->
                new SimpleIntegerProperty(topParticipantsTable.getItems().indexOf(cellData.getValue()) + 1).asObject());

        nameColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(user.findById(cellData.getValue().getNom_artiste()).getLastName()));

        votesColumn.setCellValueFactory(cellData ->
                new SimpleIntegerProperty(cellData.getValue().getNbr_votes()).asObject());

        dateColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getDate_inscription()));
    }

    public void setConcours(Concours concours) {
        this.selectedConcours = concours;
        concoursNameLabel.setText(concours.getTitre());

        // Load participations for this contest
        loadResults();
    }

    private void loadResults() {
        if (selectedConcours == null) return;
        System.out.println("Loading results for contest: " + selectedConcours.getTitre());

        // Get all participations for this contest
        List<Participation> participations = participationService.showByConcours(selectedConcours.getId());

        // Sort by votes (highest first)
        participations.sort(Comparator.comparing(Participation::getNbr_votes).reversed());
        System.out.println("Sorted participations: " + participations);

        // Display top participants in the table (limit to top 10)
        List<Participation> topParticipants = participations.stream()
                .limit(10)
                .collect(Collectors.toList());

        topParticipantsTable.setItems(FXCollections.observableArrayList(topParticipants));

        // If there are participants, show the winner
        if (!participations.isEmpty()) {
            winner = participations.get(0);
            displayWinner(winner);
            generateQRCode(winner);
        } else {
            // No participants case
            artistNameLabel.setText("Aucun gagnant");
            artistEmailLabel.setText("Pas de participants");
            votesCountLabel.setText("0");
            participationDateLabel.setText("-");
        }
    }

    private void displayWinner(Participation winner) {
        userimpl user = new userimpl();

        artistNameLabel.setText(user.findById(winner.getNom_artiste()).getName());
        artistEmailLabel.setText(user.findById(winner.getNom_artiste()).getEmail());
        votesCountLabel.setText(String.valueOf(winner.getNbr_votes()));

        // Format date if needed
        try {
            LocalDate date = LocalDate.parse(winner.getDate_inscription());
            participationDateLabel.setText(date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        } catch (Exception e) {
            participationDateLabel.setText(winner.getDate_inscription());
        }

        // Load winner image
        try {
            File file = new File(winner.getImagePath());
            if (file.exists()) {
                winnerImageView.setImage(new Image(new FileInputStream(file)));
            } else {
                // Default image if not found
                System.out.println("Image not found: " + winner.getImagePath());
            }
        } catch (Exception e) {
            System.err.println("Error loading winner image: " + e.getMessage());
        }
    }

    private void generateQRCode(Participation winner) {
        userimpl user = new userimpl();
        try {
            // Generate QR code content with winner information
            String qrContent = String.format(
                    "GAGNANT CONCOURS: %s\n" +
                            "Artiste: %s\n" +
                            "Email: %s\n" +
                            "Votes: %d\n" +
                            "Date d'inscription: %s",
                    selectedConcours.getTitre(),
                    user.findById(winner.getNom_artiste()).getName(),
                    user.findById(winner.getNom_artiste()).getEmail(),
                    winner.getNbr_votes(),
                    winner.getDate_inscription()
            );

            // QR Code generation settings
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            Map<EncodeHintType, Object> hints = new HashMap<>();
            hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
            hints.put(EncodeHintType.MARGIN, 2);

            // Generate QR Code
            BitMatrix bitMatrix = qrCodeWriter.encode(qrContent, BarcodeFormat.QR_CODE, 200, 200, hints);

            // Convert to JavaFX Image using direct conversion (no SwingFXUtils)
            WritableImage qrImage = convertToFXImage(bitMatrix);

            // Set QR code to ImageView
            qrCodeImageView.setImage(qrImage);

        } catch (Exception e) {
            System.err.println("Error generating QR code: " + e.getMessage());
        }
    }

    // Helper method to convert BitMatrix directly to JavaFX WritableImage
    private WritableImage convertToFXImage(BitMatrix matrix) {
        int width = matrix.getWidth();
        int height = matrix.getHeight();

        WritableImage image = new WritableImage(width, height);
        PixelWriter pixelWriter = image.getPixelWriter();

        // Convert to black and white pixels
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (matrix.get(x, y)) {
                    pixelWriter.setArgb(x, y, 0xFF000000); // Black
                } else {
                    pixelWriter.setArgb(x, y, 0xFFFFFFFF); // White
                }
            }
        }

        return image;
    }

    @FXML
    private void handleBack() {
        // Get current stage
        Stage stage = (Stage) concoursNameLabel.getScene().getWindow();

        try {
            // Load the concours display view
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/tunarche/afficher_concours_grid.fxml"));
            Parent root = loader.load();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            System.err.println("Error returning to concours display: " + e.getMessage());
        }
    }

    @FXML
    private void handleShareResults() {
        if (winner == null) {
            showAlert(Alert.AlertType.INFORMATION, "Aucun résultat",
                    "Il n'y a pas encore de gagnant",
                    "Aucun participant n'a été trouvé pour ce concours.");
            return;
        }

        try {
            // Save QR code to temporary file (using AWT/Swing for image output)
            int width = 200;
            int height = 200;

            // Generate QR code again for saving
            String qrContent = String.format(
                    "GAGNANT CONCOURS: %s\n" +
                            "Artiste: %s\n" +
                            "Email: %s\n" +
                            "Votes: %d\n" +
                            "Date d'inscription: %s",
                    selectedConcours.getTitre(),
                    winner.getNom_artiste(),
                    winner.getEmail_artiste(),
                    winner.getNbr_votes(),
                    winner.getDate_inscription()
            );

            // QR Code generation settings
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            Map<EncodeHintType, Object> hints = new HashMap<>();
            hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
            hints.put(EncodeHintType.MARGIN, 2);

            // Generate QR Code
            BitMatrix bitMatrix = qrCodeWriter.encode(qrContent, BarcodeFormat.QR_CODE, width, height, hints);

            // Convert BitMatrix to BufferedImage for file output
            BufferedImage qrBufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    qrBufferedImage.setRGB(x, y, bitMatrix.get(x, y) ? 0xFF000000 : 0xFFFFFFFF);
                }
            }

            // Save to file
            File qrFile = new File(System.getProperty("java.io.tmpdir"), "winner_qr.png");
            ImageIO.write(qrBufferedImage, "png", qrFile);

            // Open the file with default viewer
            Desktop.getDesktop().open(qrFile);

            // Create sharing text
            String shareText = String.format(
                    "Félicitations au gagnant du concours \"%s\" !\n\n" +
                            "🏆 %s\n" +
                            "📧 %s\n" +
                            "🗳️ %d votes\n\n" +
                            "Merci à tous les participants !",
                    selectedConcours.getTitre(),
                    winner.getNom_artiste(),
                    winner.getEmail_artiste(),
                    winner.getNbr_votes()
            );

            // Create a text file with sharing text
            File shareFile = new File(System.getProperty("java.io.tmpdir"), "share_results.txt");
            Files.write(shareFile.toPath(), shareText.getBytes());

            // Open the text file with default editor
            Desktop.getDesktop().open(shareFile);

            showAlert(Alert.AlertType.INFORMATION, "Partage",
                    "Fichiers de partage créés",
                    "Le QR code et le texte ont été générés avec succès. Vous pouvez les partager sur vos réseaux.");

        } catch (Exception e) {
            System.err.println("Error sharing results: " + e.getMessage());
            showAlert(Alert.AlertType.ERROR, "Erreur",
                    "Erreur lors du partage",
                    "Une erreur est survenue: " + e.getMessage());
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String headerText, String contentText) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);
        alert.showAndWait();
    }
}