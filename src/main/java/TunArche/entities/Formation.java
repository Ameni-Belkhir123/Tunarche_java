package TunArche.entities;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Formation<F> {
    private int id ;
    private String titre ;
    private String description ;
    private LocalDate datedebut ;
    private LocalDate datefin ;
    private int nbrplaces ;
    private String link ;
    private List<Evaluation> evaluations = new ArrayList<>();
    private String image_name;
    private int image_size ;
    private LocalDate updated_at ;
    public Formation() {}

    public Formation(int id, String titre, String description, LocalDate datedebut, LocalDate datefin, int nbrplaces, String link, List<Evaluation> evaluations, String image_name, int image_size, LocalDate updated_at) {
        this.id = id;
        this.titre = titre;
        this.description = description;
        this.datedebut = datedebut;
        this.datefin = datefin;
        this.nbrplaces = nbrplaces;
        this.link = link;
        this.evaluations = evaluations;
        this.image_name = image_name;
        this.image_size = image_size;
        this.updated_at = updated_at;
    }

    public Formation(int id, String titre, String description, LocalDate datedebut, LocalDate datefin, int nbrplaces, String link, String image_name, int image_size, LocalDate updated_at) {
        this.id = id;
        this.titre = titre;
        this.description = description;
        this.datedebut = datedebut;
        this.datefin = datefin;
        this.nbrplaces = nbrplaces;
        this.link = link;
        this.image_name = image_name;
        this.image_size = image_size;
        this.updated_at = updated_at;
    }


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

    public LocalDate getDatedebut() {
        return datedebut;
    }

    public void setDatedebut(LocalDate datedebut) {
        this.datedebut = datedebut;
    }

    public LocalDate getDatefin() {
        return datefin;
    }

    public void setDatefin(LocalDate datefin) {
        this.datefin = datefin;
    }

    public int getNbrplaces() {
        return nbrplaces;
    }

    public void setNbrplaces(int nbrplaces) {
        this.nbrplaces = nbrplaces;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public List<Evaluation> getEvaluations() {
        return evaluations;
    }

    public void setEvaluations(List<Evaluation> evaluations) {
        this.evaluations = evaluations;
    }

    public String getImage_name() {
        return image_name;
    }

    public void setImage_name(String image_name) {
        this.image_name = image_name;
    }

    public int getImage_size() {
        return image_size;
    }

    public void setImage_size(int image_size) {
        this.image_size = image_size;
    }

    public LocalDate getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(LocalDate updated_at) {
        this.updated_at = updated_at;
    }

    @Override
    public String toString() {
        return "Formation{" +
                "id=" + id +
                ", titre='" + titre + '\'' +
                ", description='" + description + '\'' +
                ", datedebut=" + datedebut +
                ", datefin=" + datefin +
                ", nbrplaces=" + nbrplaces +
                ", link='" + link + '\'' +
                ", evaluations=" + evaluations +
                ", image_name='" + image_name + '\'' +
                ", image_size=" + image_size +
                ", updated_at=" + updated_at +
                '}';
    }
}
