


package TunArche.entities;

public class Participation {
    private int id;
    private int concours_id;
    private int oeuvre_id;
    private String date_inscription;
    private String nom_artiste;
    private String email_artiste;
    private int nbr_votes;
    private String image_path;
    private Concours concours;


    public Participation(int id, int concours_id, int oeuvre_id, String date_inscription, String nom_artiste, String email_artiste, int nbr_votes, String image_path) {
        this.id = id;
        this.concours_id = concours_id;
        this.oeuvre_id = oeuvre_id;
        this.date_inscription = date_inscription;
        this.nom_artiste = nom_artiste;
        this.email_artiste = email_artiste;
        this.nbr_votes = nbr_votes;
        this.image_path = image_path;
    }

    public Participation() {

    }



    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getConcours_id() {
        return concours_id;
    }

    public void setConcours_id(int concours_id) {
        this.concours_id = concours_id;
    }

    public int getOeuvre_id() {
        return oeuvre_id;
    }

    public void setOeuvre_id(int oeuvre_id) {
        this.oeuvre_id = oeuvre_id;
    }

    public String getDate_inscription() {
        return date_inscription;
    }

    public void setDate_inscription(String date_inscription) {
        this.date_inscription = date_inscription;
    }

    public String getNom_artiste() {
        return nom_artiste;
    }

    public void setNom_artiste(String nom_artiste) {
        this.nom_artiste = nom_artiste;
    }

    public String getEmail_artiste() {
        return email_artiste;
    }

    public void setEmail_artiste(String email_artiste) {
        this.email_artiste = email_artiste;
    }

    public int getNbr_votes() {
        return nbr_votes;
    }

    public void setNbr_votes(int nbr_votes) {
        this.nbr_votes = nbr_votes;
    }

    public String getImage_path() {
        return image_path;
    }

    public void setImage_path(String image_path) {
        this.image_path = image_path;
    }

    @Override
    public String toString() {
        return "Participation{" +
                "id=" + id +
                ", concours_id=" + concours_id +
                ", oeuvre_id=" + oeuvre_id +
                ", date_inscription='" + date_inscription + '\'' +
                ", nom_artiste='" + nom_artiste + '\'' +
                ", email_artiste='" + email_artiste + '\'' +
                ", nbr_votes=" + nbr_votes +
                ", image_path='" + image_path + '\'' +
                ", concours=" + concours +
                '}';
    }
}