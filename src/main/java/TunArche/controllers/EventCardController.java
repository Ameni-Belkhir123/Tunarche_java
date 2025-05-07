package TunArche.controllers;

import TunArche.entities.Event;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.Objects;

public class EventCardController {

    @FXML
    private Label eventName, eventPlace, eventDate, eventPrice;

    @FXML
    private ImageView eventImage;

    public void setData(Event event) {
        eventName.setText(event.getNameEvent());
        eventPlace.setText("📍 " + event.getPlaceEvent());
        eventDate.setText("📅 " + event.getDateStart() + " → " + event.getDateEnd());
        eventPrice.setText("🎫 " + event.getPrice() + " DT");

        // Exemple image par défaut
        Image img = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/affiche.jpg")));
        eventImage.setImage(img);
    }
}
