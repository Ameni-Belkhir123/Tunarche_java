package TunArche.services;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import TunArche.entities.Commantaire;
import TunArche.interfaces.ICommantaire;
import TunArche.tools.MyConnection;

public class CommantaireImpl implements ICommantaire<Commantaire> {

    @Override
    public void create(Commantaire commantaire) {
        try {
            // Vérifiez si la publication existe avant d'insérer le commentaire
            String checkPublicationQuery = "SELECT COUNT(*) FROM publication WHERE id = ?";
            PreparedStatement checkSt = MyConnection.getInstance().getCnx().prepareStatement(checkPublicationQuery);
            checkSt.setInt(1, commantaire.getPublicationId());
            ResultSet rs = checkSt.executeQuery();
            if (rs.next() && rs.getInt(1) == 0) {
                throw new SQLException("La publication avec l'ID " + commantaire.getPublicationId() + " n'existe pas.");
            }

            String requete = "INSERT INTO commantaire (contenu, id_pub_id, date) VALUES (?, ?, ?)";
            PreparedStatement st = MyConnection.getInstance().getCnx().prepareStatement(requete);
            st.setString(1, commantaire.getContenu());
            st.setInt(2, commantaire.getPublicationId()); // Utilisation correcte de la clé étrangère
            st.setDate(3, new java.sql.Date(System.currentTimeMillis())); // Date actuelle

            st.executeUpdate();
            System.out.println("Commantaire ajouté !");
        } catch (SQLException e) {
            System.out.println("Erreur : " + e.getMessage());
        }
    }

    @Override
    public void update(Commantaire commantaire) {
        try {
            String requete = "UPDATE commantaire SET contenu=?, id_pub_id=?, date=? WHERE id=?";
            PreparedStatement st = MyConnection.getInstance().getCnx().prepareStatement(requete);
            st.setString(1, commantaire.getContenu());
            st.setInt(2, commantaire.getId());
            st.executeUpdate();
            System.out.println("Commantaire modifié !");
        } catch (SQLException e) {
            System.out.println("Erreur : " + e.getMessage());
        }
    }

    @Override
    public void delete(int id) {
        try {
            String requete = "DELETE FROM commantaire WHERE id=?";
            PreparedStatement st = MyConnection.getInstance().getCnx().prepareStatement(requete);
            st.setInt(1, id);
            st.executeUpdate();
            System.out.println("Commantaire supprimé !");
        } catch (SQLException e) {
            System.out.println("Erreur : " + e.getMessage());
        }
    }

    @Override
    public Commantaire findById(int id) {
        try {
            String requete = "SELECT * FROM commantaire WHERE id=?";
            PreparedStatement st = MyConnection.getInstance().getCnx().prepareStatement(requete);
            st.setInt(1, id);
            ResultSet rs = st.executeQuery();
            if (rs.next()) {
                return new Commantaire(rs.getInt("id"), rs.getString("contenu"));
            }
        } catch (SQLException e) {
            System.out.println("Erreur : " + e.getMessage());
        }
        return null;
    }

    // Implémentation de la méthode showByPublication
    @Override
    public List<Commantaire> showByPublication(int idPublication) {
        List<Commantaire> commantaires = new ArrayList<>();
        try {
            String requete = "SELECT * FROM commantaire WHERE id_pub_id=?";
            PreparedStatement st = MyConnection.getInstance().getCnx().prepareStatement(requete);
            st.setInt(1, idPublication);
            ResultSet rs = st.executeQuery();
            while (rs.next()) {
                commantaires.add(new Commantaire(rs.getInt("id"), rs.getString("contenu")));
            }
        } catch (SQLException e) {
            System.out.println("Erreur : " + e.getMessage());
        }
        return commantaires;
    }

}
