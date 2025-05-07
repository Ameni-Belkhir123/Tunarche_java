package TunArche.entities;

public class Vote {
    private int id;
    private int user_id;
    private int participation_id;
    private int concours_id;

    // Constructors
    public Vote() {
    }

    public Vote(int user_id, int participation_id, int concours_id) {
        this.user_id = user_id;
        this.participation_id = participation_id;
        this.concours_id = concours_id;
    }

    public Vote(int id, int user_id, int participation_id, int concours_id) {
        this.id = id;
        this.user_id = user_id;
        this.participation_id = participation_id;
        this.concours_id = concours_id;
    }

    // Getters
    public int getId() {
        return id;
    }

    public int getUser_id() {
        return user_id;
    }

    public int getParticipation_id() {
        return participation_id;
    }

    public int getConcours_id() {
        return concours_id;
    }

    // Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public void setParticipation_id(int participation_id) {
        this.participation_id = participation_id;
    }

    public void setConcours_id(int concours_id) {
        this.concours_id = concours_id;
    }

    // toString
    @Override
    public String toString() {
        return "Vote{" +
                "id=" + id +
                ", user_id=" + user_id +
                ", participation_id=" + participation_id +
                ", concours_id=" + concours_id +
                '}';
    }
}