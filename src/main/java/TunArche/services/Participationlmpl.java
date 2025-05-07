package TunArche.services;

import TunArche.entities.Participation;
import TunArche.interfaces.IParticipation;
import TunArche.tools.MyConnection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class Participationlmpl implements IParticipation<Participation> {
    @Override
    public void create(Participation participation) {
        try {
            String requete = "INSERT INTO participation (concours_id, oeuvre_id, date_inscription, artist_id, nbr_votes, image_path) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement st = MyConnection.getInstance().getCnx().prepareStatement(requete, Statement.RETURN_GENERATED_KEYS);
            st.setInt(1, participation.getConcours_id());
            st.setInt(2, participation.getOeuvre_id());
            st.setString(3, participation.getDate_inscription());
            st.setInt(4, participation.getNom_artiste());

            st.setInt(5, participation.getNbr_votes());
            st.setString(6, participation.getImagePath());

            int affectedRows = st.executeUpdate();

            if (affectedRows > 0) {
                // Get the generated ID
                ResultSet generatedKeys = st.getGeneratedKeys();
                if (generatedKeys.next()) {
                    participation.setId(generatedKeys.getInt(1));
                }
                System.out.println("Participation ajoutée avec succès! ID: " + participation.getId());
            } else {
                System.out.println("Échec d'ajout de la participation.");
            }
        } catch (Exception e) {
            System.out.println("Erreur lors de l'ajout de la participation : " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void update(Participation participation) {
        try {
            String requete = "UPDATE participation SET concours_id = ?, oeuvre_id = ?, date_inscription = ?, artist_id = ?, nbr_votes = ?, image_path = ? WHERE id = ?";
            PreparedStatement st = MyConnection.getInstance().getCnx().prepareStatement(requete);
            st.setInt(1, participation.getConcours_id());
            st.setInt(2, participation.getOeuvre_id());
            st.setString(3, participation.getDate_inscription());
            st.setInt(4, participation.getNom_artiste());

            st.setInt(5, participation.getNbr_votes());
            st.setString(6, participation.getImagePath());
            st.setInt(7, participation.getId());
            st.executeUpdate();
            System.out.println("Participation modifiée !");
        } catch (Exception e) {
            System.out.println("Erreur lors de la modification : " + e.getMessage());
        }
    }

    @Override
    public boolean delete(int id) {
        String requete = "DELETE FROM participation WHERE id=?";
        try (PreparedStatement st = MyConnection.getInstance().getCnx().prepareStatement(requete)) {
            st.setInt(1, id);
            int rowsAffected = st.executeUpdate();
            System.out.println("Participation supprimée !");
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Participation findById(int id) {
        try {
            String requete = "SELECT * FROM participation WHERE id = ?";
            PreparedStatement st = MyConnection.getInstance().getCnx().prepareStatement(requete);
            st.setInt(1, id);
            ResultSet rs = st.executeQuery();
            if (rs.next()) {
                return new Participation(
                        rs.getInt("id"),
                        rs.getInt("concours_id"),
                        rs.getInt("oeuvre_id"),
                        rs.getString("date_inscription"),
                        rs.getInt("artist_id"),
                        rs.getInt("nbr_votes"),
                        rs.getString("image_path")
                );
            }
        } catch (Exception e) {
            System.out.println("Erreur lors de la récupération de la participation : " + e.getMessage());
        }
        return null;
    }

    @Override
    public List<Participation> showByConcours(int concoursId) {
        List<Participation> participations = new ArrayList<>();
        try {
            String requete = "SELECT * FROM participation WHERE concours_id = ?";
            PreparedStatement st = MyConnection.getInstance().getCnx().prepareStatement(requete);
            st.setInt(1, concoursId);
            ResultSet rs = st.executeQuery();
            while (rs.next()) {
                participations.add(new Participation(
                        rs.getInt("id"),
                        rs.getInt("concours_id"),
                        rs.getInt("oeuvre_id"),
                        rs.getString("date_inscription"),
                        rs.getInt("artist_id"),

                        rs.getInt("nbr_votes"),
                        rs.getString("image_path")
                ));
            }
        } catch (Exception e) {
            System.out.println("Erreur lors de la récupération des participations : " + e.getMessage());
        }
        return participations;
    }

    @Override
    public List<Participation> showAll() {
        List<Participation> participations = new ArrayList<>();
        try {
            String requete = "SELECT * FROM participation";
            PreparedStatement st = MyConnection.getInstance().getCnx().prepareStatement(requete);
            ResultSet rs = st.executeQuery();
            while (rs.next()) {
                participations.add(new Participation(
                        rs.getInt("id"),
                        rs.getInt("concours_id"),
                            rs.getInt("oeuvre_id"),
                        rs.getString("date_inscription"),
                    rs.getInt("artist_id"),

                        rs.getInt("nbr_votes"),
                        rs.getString("image_path")
                ));
            }
        } catch (Exception e) {
            System.out.println("Erreur lors de showAll() : " + e.getMessage());
        }
        return participations;
    }

    @Override
    public void save(Participation participation) {
        if (participation.getId() == 0) {  // Si l'ID est 0, cela signifie que c'est une nouvelle participation
            create(participation);  // Appel à la méthode create pour ajouter une nouvelle participation
        } else {
            update(participation);  // Sinon, appel de la méthode update pour modifier une participation existante
        }
    }
}