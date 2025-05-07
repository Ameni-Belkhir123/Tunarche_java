package TunArche.services;

import TunArche.entities.Concours;
import TunArche.interfaces.IConcours;
import TunArche.tools.MyConnection;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Concourslmpl implements IConcours<Concours> {

    @Override
    public boolean existsWithTitle(String titre) {
        String query = "SELECT COUNT(*) FROM concours WHERE titre = ?";
        try (PreparedStatement st = MyConnection.getInstance().getCnx().prepareStatement(query)) {
            st.setString(1, titre);
            try (ResultSet rs = st.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean existsWithTitle(String titre, int ignoreId) {
        String query = "SELECT COUNT(*) FROM concours WHERE titre = ? AND id != ?";
        try (PreparedStatement st = MyConnection.getInstance().getCnx().prepareStatement(query)) {
            st.setString(1, titre);
            st.setInt(2, ignoreId);
            try (ResultSet rs = st.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

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
            e.printStackTrace();
        }
    }

    @Override
    public void update(Concours concours) {
        String requete = "UPDATE concours SET titre=?, description=?, datedebut=?, datefin=? WHERE id=?";
        try (PreparedStatement st = MyConnection.getInstance().getCnx().prepareStatement(requete)) {
            st.setString(1, concours.getTitre());
            st.setString(2, concours.getDescription());
            st.setDate(3, Date.valueOf(concours.getDateDebut()));
            st.setDate(4, Date.valueOf(concours.getDateFin()));
            st.setInt(5, concours.getId());
            st.executeUpdate();
            System.out.println("Concours modifié !");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean delete(int id) {
        String requete = "DELETE FROM concours WHERE id=?";
        try (PreparedStatement st = MyConnection.getInstance().getCnx().prepareStatement(requete)) {
            st.setInt(1, id);
            int rowsAffected = st.executeUpdate();
            System.out.println("Concours supprimé !");
            return rowsAffected > 0;
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
                            rs.getDate("datedebut").toLocalDate(),
                            rs.getDate("datefin").toLocalDate()
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
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
                        rs.getDate("datedebut").toLocalDate(),
                        rs.getDate("datefin").toLocalDate()
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return concoursList;
    }}

   /* // Méthode pour récupérer les participations d'un concours
    public List<Participation> getParticipationsByConcours(int concoursId) {
        List<Participation> participations = new ArrayList<>();
        String query = "SELECT * FROM participation WHERE concours_id = ?";
        try (PreparedStatement st = MyConnection.getInstance().getCnx().prepareStatement(query)) {
            st.setInt(1, concoursId);
            try (ResultSet rs = st.executeQuery()) {
                while (rs.next()) {
                    participations.add(new Participation(
                            rs.getInt("id"),
                            rs.getInt("nbrVotes"),
                            new Concours(rs.getInt("concours_id"), "", "", null, null) // Concours lié à la participation
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return participations;
    }*/

   /* // Méthode pour calculer la moyenne des votes pour un concours
    public double calculerMoyenneVotes(int concoursId) {
        List<Participation> participations = getParticipationsByConcours(concoursId);
        if (participations.isEmpty()) {
            return 0;
        }
        int totalVotes = 0;
        for (Participation participation : participations) {
            totalVotes += participation.getNbr_votes();
        }
        return (double) totalVotes / participations.size();
    }
}*/
