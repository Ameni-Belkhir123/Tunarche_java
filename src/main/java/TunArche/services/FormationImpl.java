package TunArche.services;

import TunArche.entities.Evaluation;
import TunArche.entities.Formation;
import TunArche.interfaces.IFormation;
import TunArche.tools.MyConnection;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class FormationImpl implements IFormation<Formation> {


    @Override
    public void create(Formation formation) {
        try {
            String requete = "INSERT INTO formation (titre, description, datedebut, datefin, nbrplaces, link, image_name, image_size, updated_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement st = MyConnection.getInstance().getCnx().prepareStatement(requete);
            st.setString(1, formation.getTitre());
            st.setString(2, formation.getDescription());
            st.setDate(3, Date.valueOf(formation.getDatedebut())); // LocalDate -> java.sql.Date
            st.setDate(4, Date.valueOf(formation.getDatefin()));
            st.setInt(5, formation.getNbrplaces());
            st.setString(6, formation.getLink());
            st.setString(7, formation.getImage_name());
            st.setInt(8, formation.getImage_size());
            st.setDate(9, Date.valueOf(formation.getUpdated_at()));
            st.executeUpdate();
            System.out.println("Formation ajoutée avec image !");
        } catch (Exception e) {
            System.out.println("Erreur create : " + e.getMessage());
        }
    }

    @Override
    public void update(Formation formation) {
        try {
            String requete = "UPDATE formation SET titre=?, description=?, datedebut=?, datefin=?, nbrplaces=?, link=?, image_name=?, image_size=?, updated_at=? WHERE id=?";
            PreparedStatement st = MyConnection.getInstance().getCnx().prepareStatement(requete);
            st.setString(1, formation.getTitre());
            st.setString(2, formation.getDescription());
            st.setDate(3, Date.valueOf(formation.getDatedebut()));
            st.setDate(4, Date.valueOf(formation.getDatefin()));
            st.setInt(5, formation.getNbrplaces());
            st.setString(6, formation.getLink());
            st.setString(7, formation.getImage_name());
            st.setInt(8, formation.getImage_size());
            st.setDate(9, Date.valueOf(formation.getUpdated_at()));
            st.setInt(10, formation.getId());
            st.executeUpdate();
            System.out.println("Formation modifiée !");
        } catch (Exception e) {
            System.out.println("Erreur update : " + e.getMessage());
        }
    }

    @Override
    public void delete(int id) {
        try {
            String requete = "DELETE FROM formation WHERE id=?";
            PreparedStatement st = MyConnection.getInstance().getCnx().prepareStatement(requete);
            st.setInt(1, id);
            st.executeUpdate();
            System.out.println("Formation supprimée !");
        } catch (Exception e) {
            System.out.println("Erreur delete : " + e.getMessage());
        }
    }

    @Override
    public Formation findById(int id) {
            String requete = "SELECT * FROM formation WHERE id=?";

            try (PreparedStatement st = MyConnection.getInstance().getCnx().prepareStatement(requete)) {
                st.setInt(1, id);

                try (ResultSet rs = st.executeQuery()) {
                    if (rs.next()) {
                        // Gestion des dates potentiellement nulles
                        LocalDate dateDebut = rs.getDate("datedebut") != null ?
                                rs.getDate("datedebut").toLocalDate() : null;
                        LocalDate dateFin = rs.getDate("datefin") != null ?
                                rs.getDate("datefin").toLocalDate() : null;
                        LocalDate updatedAt = rs.getDate("updated_at") != null ?
                                rs.getDate("updated_at").toLocalDate() : null;

                        return new Formation(
                                rs.getInt("id"),
                                rs.getString("titre"),
                                rs.getString("description"),
                                dateDebut,
                                dateFin,
                                rs.getInt("nbrplaces"),
                                rs.getString("link"),
                                rs.getString("image_name"),
                                rs.getInt("image_size"),
                                updatedAt
                        );
                    }
                }
            } catch (SQLException e) {
                System.err.println("Erreur findById (SQL): " + e.getMessage());
            } catch (Exception e) {
                System.err.println("Erreur findById (générale): " + e.getMessage());
            }
            return null;
         }
    @Override
    public List<Formation> showAll() {
        List<Formation> formations = new ArrayList<>();
        String requete = "SELECT * FROM formation";

        try (PreparedStatement st = MyConnection.getInstance().getCnx().prepareStatement(requete);
             ResultSet rs = st.executeQuery()) {

            EvaluationImpl evaluationImpl = new EvaluationImpl();

            while (rs.next()) {
                // Gestion des dates potentiellement nulles
                LocalDate dateDebut = rs.getDate("datedebut") != null ?
                        rs.getDate("datedebut").toLocalDate() : null;
                LocalDate dateFin = rs.getDate("datefin") != null ?
                        rs.getDate("datefin").toLocalDate() : null;
                LocalDate updatedAt = rs.getDate("updated_at") != null ?
                        rs.getDate("updated_at").toLocalDate() : null;

                int id = rs.getInt("id");
                List<Evaluation> evaluations = evaluationImpl.showByFormation(id);

                Formation f = new Formation(
                        id,
                        rs.getString("titre"),
                        rs.getString("description"),
                        dateDebut,
                        dateFin,
                        rs.getInt("nbrplaces"),
                        rs.getString("link"),
                        evaluations,
                        rs.getString("image_name"),
                        rs.getInt("image_size"),
                        updatedAt
                );
                formations.add(f);
            }
        } catch (SQLException e) {
            System.err.println("Erreur showAll (SQL): " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Erreur showAll (générale): " + e.getMessage());
        }

        return formations;}
}