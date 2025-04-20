package TunArche.controllers;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ChatbotController {
    @FXML
    private TextField userInput;

    @FXML
    private VBox chatBox;

    @FXML
    private ScrollPane chatScrollPane;

    private String lastGeneratedTitle = "";
    private String lastGeneratedDescription = "";

    private formationController formationController; // ✅ classe avec majuscule

    public void setFormationController(formationController controller) {
        this.formationController = controller;
    }

    @FXML
    private void handleSend(ActionEvent event) {
        String message = userInput.getText().trim();
        if (!message.isEmpty()) {
            addMessage("👤", message, "#dff9fb", "right");
            simulateBotResponse(message);
            userInput.clear();
        }
    }

    private void addMessage(String sender, String message, String bgColor, String alignment) {
        HBox messageContainer = new HBox();

        if ("left".equals(alignment)) {
            messageContainer.setStyle("-fx-alignment: center-left;");
        } else {
            messageContainer.setStyle("-fx-alignment: center-right;");
        }

        Label msg = new Label(sender + " " + message);
        msg.setWrapText(true);
        msg.setStyle("-fx-padding: 10; -fx-background-color: " + bgColor + "; -fx-background-radius: 10;");
        msg.setMaxWidth(300);

        messageContainer.getChildren().add(msg);
        chatBox.getChildren().add(messageContainer);

        Platform.runLater(() -> chatScrollPane.setVvalue(1.0));
    }

    private void simulateBotResponse(String userMessage) {
        Platform.runLater(() -> {
            lastGeneratedTitle = "🎨 Atelier de Peinture Moderne";
            lastGeneratedDescription = "Explorez les techniques modernes de peinture à travers des ateliers pratiques avec des artistes contemporains.";

            String response = "Voici une proposition de formation :\n"
                    + "Titre : " + lastGeneratedTitle + "\n"
                    + "Description : " + lastGeneratedDescription;

            addMessage("🤖", response, "#f1c40f", "left");

            Button insertBtn = new Button("✅ Insérer");
            insertBtn.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white;");
            insertBtn.setOnAction(e -> {
                if (formationController != null) {
                    formationController.setChatbotData(lastGeneratedTitle, lastGeneratedDescription);
                }
                closeChatbot();
            });

            HBox btnContainer = new HBox(insertBtn);
            btnContainer.setStyle("-fx-alignment: center-left;");
            chatBox.getChildren().add(btnContainer);
        });
    }

    private void closeChatbot() {
        Stage stage = (Stage) chatBox.getScene().getWindow();
        stage.close();
    }
}
