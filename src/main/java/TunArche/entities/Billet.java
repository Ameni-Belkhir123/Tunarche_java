package TunArche.entities;


import java.sql.Timestamp;
import java.util.Date;

public class Billet {
    private int id;
    private int eventId;
    private int buyerId;
    private String numero;
    private Timestamp dateEmission;
    private String modePaiement;
    private String type;

    // 🧱 Constructeurs
    public Billet() {}

    public Billet(int id, int eventId, int buyerId, String numero, Date dateEmission, String modePaiement, String type) {
        this.id = id;
        this.eventId = eventId;
        this.buyerId = buyerId;
        this.numero = numero;
        this.dateEmission = (Timestamp) dateEmission;
        this.modePaiement = modePaiement;
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getEventId() {
        return eventId;
    }

    public void setEventId(int eventId) {
        this.eventId = eventId;
    }

    public int getBuyerId() {
        return buyerId;
    }

    public void setBuyerId(int buyerId) {
        this.buyerId = buyerId;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public Timestamp getDateEmission() {
        return dateEmission;
    }

    public void setDateEmission(Timestamp dateEmission) {
        this.dateEmission = dateEmission;
    }

    public String getModePaiement() {
        return modePaiement;
    }

    public void setModePaiement(String modePaiement) {
        this.modePaiement = modePaiement;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "Billet{" +
                "id=" + id +
                ", eventId=" + eventId +
                ", buyerId=" + buyerId +
                ", numero='" + numero + '\'' +
                ", dateEmission=" + dateEmission +
                ", modePaiement='" + modePaiement + '\'' +
                ", type='" + type + '\'' +
                '}';
    }


}
