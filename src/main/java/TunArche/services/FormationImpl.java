package TunArche.services;

import TunArche.entities.Evaluation;
import TunArche.entities.Formation;
import TunArche.interfaces.IFormation;
import TunArche.tools.MyConnection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class FormationImpl implements IFormation<Formation> {


    @Override
    public void create(Formation formation) {
        if (!formation.getDatedebut().before(formation.getDatefin())) {
            System.out.println("❌ La date de début doit être avant la date de fin.");
            return;
        }
        try {
            String requete = "INSERT INTO formation (titre, description, datedebut, datefin, nbrplaces, link, image_name, image_size, updated_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement st = MyConnection.getInstance().getCnx().prepareStatement(requete);
            st.setString(1, formation.getTitre());
            st.setString(2, formation.getDescription());
            st.setDate(3, new java.sql.Date(formation.getDatedebut().getTime()));
            st.setDate(4, new java.sql.Date(formation.getDatefin().getTime()));
            st.setInt(5, formation.getNbrplaces());
            st.setString(6, formation.getLink());
            st.setString(7, formation.getImage_name());
            st.setInt(8, formation.getImage_size());
            st.setDate(9, new java.sql.Date(formation.getUpdated_at().getTime()));
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
            st.setDate(3, new java.sql.Date(formation.getDatedebut().getTime()));
            st.setDate(4, new java.sql.Date(formation.getDatefin().getTime()));
            st.setInt(5, formation.getNbrplaces());
            st.setString(6, formation.getLink());
            st.setString(7, formation.getImage_name());
            st.setInt(8, formation.getImage_size());
            st.setDate(9, new java.sql.Date(formation.getUpdated_at().getTime()));
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
        try {
            String requete = "SELECT * FROM formation WHERE id=?";
            PreparedStatement st = MyConnection.getInstance().getCnx().prepareStatement(requete);
            st.setInt(1, id);
            ResultSet rs = st.executeQuery();
            if (rs.next()) {
                return new Formation(
                        rs.getInt("id"),
                        rs.getString("titre"),
                        rs.getString("description"),
                        rs.getDate("datedebut"),
                        rs.getDate("datefin"),
                        rs.getInt("nbrplaces"),
                        rs.getString("link"),
                        rs.getString("image_name"),
                        rs.getInt("image_size"),
                        rs.getDate("updated_at")
                );
            }
        } catch (Exception e) {
            System.out.println("Erreur findById : " + e.getMessage());
        }
        return null;
    }

    @Override
    public List<Formation> showAll() {
        List<Formation> formations = new ArrayList<>();
        EvaluationImpl evaluationImpl = new EvaluationImpl(); // pour récupérer les évaluations

        try {
            String requete = "SELECT * FROM formation";
            PreparedStatement st = MyConnection.getInstance().getCnx().prepareStatement(requete);
            ResultSet rs = st.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("id");
                List<Evaluation> evaluations = evaluationImpl.showByFormation(id);
                Formation f = new Formation(
                        id,
                        rs.getString("titre"),
                        rs.getString("description"),
                        rs.getDate("datedebut"),
                        rs.getDate("datefin"),
                        rs.getInt("nbrplaces"),
                        rs.getString("link"),
                        evaluations,
                        rs.getString("image_name"),
                        rs.getInt("image_size"),
                        rs.getDate("updated_at")
                );
                formations.add(f);
            }
        } catch (Exception e) {
            System.out.println("Erreur : " + e.getMessage());
        }

        return formations;
    }
}