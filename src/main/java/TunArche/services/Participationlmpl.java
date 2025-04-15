package TunArche.services;

import TunArche.entities.Participation;
import TunArche.tools.MyConnection;
import TunArche.interfaces.IParticipation;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class Participationlmpl implements IParticipation<Participation> {
    @Override
    public void create(Participation participation) {
        try {
            String requete = "INSERT INTO participation (concours_id, id_oeuvre, date_inscription, nom_artiste, email_artiste, nbr_votes, image_path) VALUES (?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement st = MyConnection.getInstance().getCnx().prepareStatement(requete);
            st.setInt(1, participation.getConcours_id());
            st.setInt(2, participation.getOeuvre_id());
            st.setString(3, participation.getDate_inscription());
            st.setString(4, participation.getNom_artiste());
            st.setString(5, participation.getEmail_artiste());
            st.setInt(6, participation.getNbr_votes());
            st.setString(7, participation.getImage_path());
            st.executeUpdate();
            System.out.println("Participation ajoutée !");
        } catch (Exception e) {
            System.out.println("Erreur : " + e.getMessage());
        }
    }

    @Override
    public void update(Participation participation) {
        try {
            String requete = "UPDATE participation SET concours_id = ?, id_oeuvre = ?, date_inscription = ?, nom_artiste = ?, email_artiste = ?, nbr_votes = ?, image_path = ? WHERE id = ?";
            PreparedStatement st = MyConnection.getInstance().getCnx().prepareStatement(requete);
            st.setInt(1, participation.getConcours_id());
            st.setInt(2, participation.getOeuvre_id());
            st.setString(3, participation.getDate_inscription());
            st.setString(4, participation.getNom_artiste());
            st.setString(5, participation.getEmail_artiste());
            st.setInt(6, participation.getNbr_votes());
            st.setString(7, participation.getImage_path());
            st.setInt(8, participation.getId());
            st.executeUpdate();
            System.out.println("Participation modifiée !");
        } catch (Exception e) {
            System.out.println("Erreur lors de la modification : " + e.getMessage());
        }
    }

    @Override
    public void delete(int id) {
        try {
            String requete = "DELETE FROM participation WHERE id = ?";
            PreparedStatement st = MyConnection.getInstance().getCnx().prepareStatement(requete);
            st.setInt(1, id);
            st.executeUpdate();
            System.out.println("Participation supprimée !");
        } catch (Exception e) {
            System.out.println("Erreur lors de la suppression : " + e.getMessage());
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
                        rs.getInt("id_oeuvre"),
                        rs.getString("date_inscription"),
                        rs.getString("nom_artiste"),
                        rs.getString("email_artiste"),
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
                        rs.getInt("id_oeuvre"),
                        rs.getString("date_inscription"),
                        rs.getString("nom_artiste"),
                        rs.getString("email_artiste"),
                        rs.getInt("nbr_votes"),
                        rs.getString("image_path")
                ));
            }
        } catch (Exception e) {
            System.out.println("Erreur lors de la récupération des participations : " + e.getMessage());
        }
        return participations;
    }
}
