package TunArche.services;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import TunArche.entities.Publication;
import TunArche.interfaces.IPublication;
import TunArche.tools.MyConnection;

public class PublicationImpl implements IPublication<Publication> {

    @Override
    @SuppressWarnings("UseSpecificCatch")
    public void create(Publication publication) {
        try {
            String requete = "INSERT INTO publication (titre, description, image, likes, unlikes,date_act,rating) VALUES (?,,? ?, ?, ?, ?, ?)";
            PreparedStatement st = MyConnection.getInstance().getCnx().prepareStatement(requete);
            st.setString(1, publication.getTitre());
            st.setString(2, publication.getDescription());
            st.setString(3, publication.getImage());
            st.setInt(4, publication.getLikes());
            st.setInt(5, publication.getUnlikes());
            st.setDate(6, java.sql.Date.valueOf(publication.getDate_act()));
            st.setInt(7, publication.getRating());
            st.executeUpdate();
            System.out.println("Publication ajoutée !");
        } catch (Exception e) {
            System.out.println("Erreur : " + e.getMessage());
        }
    }

    @Override
    public void update(Publication publication) {
        try {
            String requete = "UPDATE publication SET titre=?, description=?, image=?, date_act=?, likes=?, unlikes=?, rating=? WHERE id=?"; // Correction de 'dateAct' en 'date_act'
            PreparedStatement st = MyConnection.getInstance().getCnx().prepareStatement(requete);
            st.setString(1, publication.getTitre());
            st.setString(2, publication.getDescription());
            st.setString(3, publication.getImage());
            st.setDate(4, new java.sql.Date(System.currentTimeMillis())); // Ajout de la date actuelle
            st.setInt(5, publication.getLikes());
            st.setInt(6, publication.getUnlikes());
            st.setInt(7, publication.getRating());
            st.setInt(8, publication.getId());
            st.executeUpdate();
            System.out.println("Publication modifiée !");
        } catch (SQLException e) {
            System.out.println("Erreur : " + e.getMessage());
        }
    }

    @Override
    public void delete(int id) {
        try {
            String requete = "DELETE FROM publication WHERE id=?";
            PreparedStatement st = MyConnection.getInstance().getCnx().prepareStatement(requete);
            st.setInt(1, id);
            st.executeUpdate();
            System.out.println("Publication supprimée !");
        } catch (SQLException e) {
            System.out.println("Erreur : " + e.getMessage());
        }
    }

    @Override


    public Publication findById(int id) {
        try {
            String requete = "SELECT * FROM publication WHERE id=?";
            PreparedStatement st = MyConnection.getInstance().getCnx().prepareStatement(requete);
            st.setInt(1, id);
            ResultSet rs = st.executeQuery();
            if (rs.next()) {
                return new Publication(rs.getInt("id"), rs.getString("titre"), rs.getString("description"), rs.getString("image"), rs.getInt("likes"), rs.getInt("unlikes"), rs.getInt("rating"));
            }
        } catch (SQLException e) {
            System.out.println("Erreur : " + e.getMessage());
        }
        return null;
    }

    @Override
    public List<Publication> showAll() {
        List<Publication> publications = new ArrayList<>();
        try {
            String requete = "SELECT * FROM publication";
            PreparedStatement st = MyConnection.getInstance().getCnx().prepareStatement(requete);
            ResultSet rs = st.executeQuery();
            while (rs.next()) {
                publications.add(new Publication(rs.getInt("id"), rs.getString("titre"), rs.getString("description"), rs.getString("image"), rs.getInt("likes"), rs.getInt("unlikes"), rs.getInt("rating")));
            }
        } catch (SQLException e) {
            System.out.println("Erreur : " + e.getMessage());
        }
        return publications;
    }

}
