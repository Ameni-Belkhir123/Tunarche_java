package TunArche.services;

import TunArche.entities.Concours;
import TunArche.interfaces.IConcours;
import TunArche.tools.MyConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Concourslmpl implements IConcours<Concours> {

    @Override
    public void create(Concours concours) {
        String requete = "INSERT INTO concours (titre, description, datedebut, datefin) VALUES (?, ?, ?, ?)";
        try (PreparedStatement st = MyConnection.getInstance().getCnx().prepareStatement(requete)) {
            st.setString(1, concours.getTitre());
            st.setString(2, concours.getDescription());
            st.setDate(3, Date.valueOf(concours.getDateDebut()));  // Conversion LocalDate -> Date
            st.setDate(4, Date.valueOf(concours.getDateFin()));    // Conversion LocalDate -> Date
            st.executeUpdate();
            System.out.println("Concours ajouté !");
        } catch (SQLException e) {
            e.printStackTrace();  // Afficher une trace de l'erreur pour faciliter le débogage
        }
    }

    @Override
    public void update(Concours concours) {
        String requete = "UPDATE concours SET titre=?, description=?, datedebut=?, datefin=? WHERE id=?";
        try (PreparedStatement st = MyConnection.getInstance().getCnx().prepareStatement(requete)) {
            st.setString(1, concours.getTitre());
            st.setString(2, concours.getDescription());
            st.setDate(3, Date.valueOf(concours.getDateDebut()));  // Conversion LocalDate -> Date
            st.setDate(4, Date.valueOf(concours.getDateFin()));    // Conversion LocalDate -> Date
            st.setInt(5, concours.getId());
            st.executeUpdate();
            System.out.println("Concours modifié !");
        } catch (SQLException e) {
            e.printStackTrace();  // Afficher une trace de l'erreur pour faciliter le débogage
        }
    }

    @Override
    public boolean delete(int id) {
        String requete = "DELETE FROM concours WHERE id=?";
        try (PreparedStatement st = MyConnection.getInstance().getCnx().prepareStatement(requete)) {
            st.setInt(1, id);
            int rowsAffected = st.executeUpdate(); // 🔥 important
            System.out.println("Concours supprimé !");
            return rowsAffected > 0; // ✅ retourne true si suppression réussie
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    @Override
    public Concours findById(int id) {
        String requete = "SELECT * FROM concours WHERE id=?";
        try (PreparedStatement st = MyConnection.getInstance().getCnx().prepareStatement(requete)) {
            st.setInt(1, id);
            try (ResultSet rs = st.executeQuery()) {
                if (rs.next()) {
                    return new Concours(
                            rs.getInt("id"),
                            rs.getString("titre"),
                            rs.getString("description"),
                            rs.getDate("datedebut").toLocalDate(),  // Conversion Date -> LocalDate
                            rs.getDate("datefin").toLocalDate()     // Conversion Date -> LocalDate
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();  // Afficher une trace de l'erreur pour faciliter le débogage
        }
        return null;
    }

    @Override
    public List<Concours> showAll() {
        List<Concours> concoursList = new ArrayList<>();
        String requete = "SELECT * FROM concours";
        try (PreparedStatement st = MyConnection.getInstance().getCnx().prepareStatement(requete);
             ResultSet rs = st.executeQuery()) {
            while (rs.next()) {
                concoursList.add(new Concours(
                        rs.getInt("id"),
                        rs.getString("titre"),
                        rs.getString("description"),
                        rs.getDate("datedebut").toLocalDate(),  // Conversion Date -> LocalDate
                        rs.getDate("datefin").toLocalDate()     // Conversion Date -> LocalDate
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();  // Afficher une trace de l'erreur pour faciliter le débogage
        }
        return concoursList;
    }
}
