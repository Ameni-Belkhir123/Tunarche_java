package TunArche.entities;

import javafx.beans.binding.BooleanExpression;
import javafx.beans.value.ObservableValue;

public class Commantaire {

    private int id;
    private String contenu;
    private int id_pub_id; // Ajout de l'attribut pour la clé étrangère

    public Commantaire() {
    }

    public Commantaire(int id, String contenu) {
        this.id = id;
        this.contenu = contenu;
    }

    public Commantaire(int id) {
        this.id = id;
    }

    public Commantaire(String contenu) {
        this.contenu = contenu;
    }

    public Commantaire(String contenu, String idPubId) {

    }
    // ✅ Constructeur complet
    public Commantaire(int id, String contenu, int publicationId) {
        this.id = id;
        this.contenu = contenu;
        this.id_pub_id = publicationId;
    }

    // ✅ Constructeur pour insertion (sans ID)
    public Commantaire(String contenu, int publicationId) {
        this.contenu = contenu;
        this.id_pub_id = publicationId;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getContenu() {
        return contenu;
    }

    public void setContenu(String contenu) {
        this.contenu = contenu;
    }

    public int getPublicationId() {
        return id_pub_id;
    }

    public void setPublicationId(int publicationId) {
        this.id_pub_id = publicationId;
    }

    @Override
    public String toString() {
        return "commantaire{"
                + "id=" + id
                + ", contenu='" + contenu + '\''
                + ", publicationId=" + id_pub_id
                + '}';
    }
}
