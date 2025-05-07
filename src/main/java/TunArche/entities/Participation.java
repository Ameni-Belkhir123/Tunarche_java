


package TunArche.entities;

public class Participation {
    private int id;
    private int concours_id;
    private int oeuvre_id;
    private String date_inscription;

    private int artiste_id;
    private String email_artiste;
    private int nbr_votes;
    private String imagePath;
    private Concours concours;


    public Participation(int id, int concours_id, int oeuvre_id, String date_inscription, int artiste_id, int nbr_votes, String image_path) {
        this.id = id;
        this.concours_id = concours_id;
        this.oeuvre_id = oeuvre_id;
        this.date_inscription = date_inscription;
        this.artiste_id = artiste_id;

        this.nbr_votes = nbr_votes;
        this.imagePath = image_path;
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

    public int getNom_artiste() {
        return artiste_id;
    }

    public void setNom_artiste(int artiste_id) {
        this.artiste_id = artiste_id;
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

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String image_path) {
        this.imagePath = image_path;
    }

    @Override
    public String toString() {
        return "Participation{" +
                "id=" + id +
                ", concours_id=" + concours_id +
                ", oeuvre_id=" + oeuvre_id +
                ", date_inscription='" + date_inscription + '\'' +
                ", nom_artiste='" + artiste_id + '\'' +
                ", email_artiste='" + email_artiste + '\'' +
                ", nbr_votes=" + nbr_votes +
                ", image_path='" + imagePath + '\'' +
                ", concours=" + concours +
                '}';
    }


}