package TunArche.entities;

public class Evaluation {

    private int id;
    private String commentaire;
    private int note;
    private Formation formation;

    public Evaluation() {
    }
    public Evaluation(int id, String commentaire, int note, Formation formation) {
        this.id = id;
        this.commentaire = commentaire;
        this.note = note;
        this.formation = formation;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCommentaire() {
        return commentaire;
    }

    public void setCommentaire(String commentaire) {
        this.commentaire = commentaire;
    }

    public int getNote() {
        return note;
    }

    public void setNote(int note) {
        this.note = note;
    }

    public Formation getFormation() {
        return formation;
    }

    public void setFormation(Formation formation) {
        this.formation = formation;
    }

    @Override
    public String toString() {
        return "Evaluation{" +
                "id=" + id +
                ", commentaire='" + commentaire + '\'' +
                ", note=" + note+
                '}';
    }
}
