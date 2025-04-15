package TunArche.entities;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Concours {
    private int id;
    private String titre;
    private String description;
    private LocalDate dateDebut;
    private LocalDate dateFin;
    private List<Participation> participations = new ArrayList<>();

    // Constructeurs
    public Concours() {
    }

    public Concours(int id, String titre, String description, LocalDate dateDebut, LocalDate dateFin) {
        this.id = id;
        this.titre = titre;
        this.description = description;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
    }

    public Concours(String titre, String description, LocalDate dateDebut, LocalDate dateFin) {
        this.titre = titre;
        this.description = description;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getTitre() {
        return titre;
    }

    public String getDescription() {
        return description;
    }

    public LocalDate getDateDebut() {
        return dateDebut;
    }

    public LocalDate getDateFin() {
        return dateFin;
    }

    public List<Participation> getParticipations() {
        return participations;
    }

    // Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setDateDebut(LocalDate dateDebut) {
        this.dateDebut = dateDebut;
    }

    public void setDateFin(LocalDate dateFin) {
        this.dateFin = dateFin;
    }

    public void setParticipations(List<Participation> participations) {
        this.participations = participations;
    }

    // toString
    @Override
    public String toString() {
        return "Concours{" +
                "id=" + id +
                ", titre='" + titre + '\'' +
                ", description='" + description + '\'' +
                ", dateDebut=" + dateDebut +
                ", dateFin=" + dateFin +
                '}';
    }
}
