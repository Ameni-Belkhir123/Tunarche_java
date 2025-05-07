package TunArche.entities;

import javafx.beans.value.ObservableValue;

import java.time.LocalDate;

public class Publication {
    private int id;
    private int author_id;
    private String titre;
    private String description;
    private String image;
    private int likes;
    private int unlikes;
    private int rating;
    private LocalDate date_act; // ✅ nom conforme à ta base de données

    public Publication() {
    }

    public Publication(String titre,int author_id, String description, LocalDate date_act, String image) {
        this.titre = titre;
        this.author_id=author_id;
        this.description = description;
        this.date_act = date_act;
        this.image = image;
        this.likes = 0;
        this.unlikes = 0;
        this.rating = 0;
    }


    public Publication(int id,int author_id,String titre, String description, String image, int likes, int unlikes, int rating, LocalDate date_act) {
        this.id = id;
        this.titre = titre;
        this.author_id=author_id;
        this.description = description;
        this.image = image;
        this.likes = likes;
        this.unlikes = unlikes;
        this.rating = rating;
        this.date_act = date_act;
    }

    // Constructeur sans ID (pour insertion)
    public Publication(String titre, String description, String image, int likes, int unlikes, int rating, LocalDate date_act) {
        this.titre = titre;
        this.description = description;
        this.image = image;
        this.likes = likes;
        this.unlikes = unlikes;
        this.rating = rating;
        this.date_act = date_act;
    }

    public Publication(int id, String titre, String description, String image, int likes, int unlikes, int rating) {
    }

    // Getters & Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public int getUnlikes() {
        return unlikes;
    }

    public void setUnlikes(int unlikes) {
        this.unlikes = unlikes;
    }

    public int getRating() {
        return rating;
    }

    public void setauthor_id(int author_id) {
        this.author_id = rating;
    }
    public int getauthor_id() {
        return author_id;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }


    public LocalDate getDate_act() {
        return date_act;
    }

    public void setDate_act(LocalDate date_act) {
        this.date_act = date_act;
    }

    @Override
    public String toString() {
        return "Publication{" +
                "id=" + id +
                ", titre='" + titre + '\'' +
                ", description='" + description + '\'' +
                ", image='" + image + '\'' +
                ", likes=" + likes +
                ", unlikes=" + unlikes +
                ", rating=" + rating +
                ", date_act=" + date_act +
                '}';
    }

    public ObservableValue<String> titreProperty() {
        return null;
    }

    public ObservableValue<String> descriptionProperty() {
        return null;
    }

    public ObservableValue<String> imageProperty() {
        return null;
    }

    public ObservableValue<LocalDate> dateProperty() {
        return null;
    }
}
