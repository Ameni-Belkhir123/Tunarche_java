package TunArche.services;

import TunArche.entities.Evaluation;
import TunArche.entities.Formation;
import TunArche.interfaces.IEvaluation;
import TunArche.tools.MyConnection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class EvaluationImpl implements IEvaluation <Evaluation> {
    @Override
    public void create(Evaluation evaluation) {
        try {
            String requete = "INSERT INTO evaluation (commentaire, note, formation_id) VALUES (?, ?, ?)";
            PreparedStatement st = MyConnection.getInstance().getCnx().prepareStatement(requete);
            st.setString(1, evaluation.getCommentaire());
            st.setInt(2, evaluation.getNote());
            st.setInt(3, evaluation.getFormation().getId());
            st.executeUpdate();
            System.out.println("Évaluation ajoutée !");
        } catch (Exception e) {
            System.out.println("Erreur : " + e.getMessage());
        }
    }

    @Override
    public void update(Evaluation evaluation) {
        try {
            String requete = "UPDATE evaluation SET commentaire = ?, note = ?, formation_id = ? WHERE id = ?";
            PreparedStatement st = MyConnection.getInstance().getCnx().prepareStatement(requete);
            st.setString(1, evaluation.getCommentaire());
            st.setInt(2, evaluation.getNote());
            st.setInt(3, evaluation.getFormation().getId());
            st.setInt(4, evaluation.getId());
            st.executeUpdate();
            System.out.println("Évaluation modifiée !");
        } catch (Exception e) {
            System.out.println("Erreur lors de la modification : " + e.getMessage());
        }

    }

    @Override
    public void delete(int id) {
        try {
            String requete = "DELETE FROM evaluation WHERE id = ?";
            PreparedStatement st = MyConnection.getInstance().getCnx().prepareStatement(requete);
            st.setInt(1, id);
            st.executeUpdate();
            System.out.println("Évaluation supprimée !");
        } catch (Exception e) {
            System.out.println("Erreur lors de la suppression : " + e.getMessage());
        }

    }
    private FormationImpl formationImpl;

    public EvaluationImpl() {
        // Initialisation de l'objet FormationImpl
        formationImpl = new FormationImpl();
    }

    @Override
    public Evaluation findById(int id) {
        try {
            String requete = "SELECT * FROM evaluation WHERE id = ?";
            PreparedStatement st = MyConnection.getInstance().getCnx().prepareStatement(requete);
            st.setInt(1, id);
            ResultSet rs = st.executeQuery();
            if (rs.next()) {
                // On récupère la formation liée à cette évaluation via FormationImpl
                Formation formation = formationImpl.findById(rs.getInt("formation_id"));
                return new Evaluation(rs.getInt("id"), rs.getString("commentaire"), rs.getInt("note"), formation);
            }
        } catch (Exception e) {
            System.out.println("Erreur lors de la récupération de l'évaluation : " + e.getMessage());
        }
        return null;
    }

    public void deleteByFormation(int formationId) {
        try {
            String requete = "DELETE FROM evaluation WHERE formation_id = ?";
            PreparedStatement st = MyConnection.getInstance().getCnx().prepareStatement(requete);
            st.setInt(1, formationId);
            st.executeUpdate();
            System.out.println("Évaluations supprimées pour la formation " + formationId);
        } catch (Exception e) {
            System.out.println("Erreur lors de la suppression des évaluations : " + e.getMessage());
        }
    }

    @Override
    public List<Evaluation> showByFormation(int formationId) {
        List<Evaluation> evaluations = new ArrayList<>();
        try {
            String requete = "SELECT * FROM evaluation WHERE formation_id = ?";
            PreparedStatement st = MyConnection.getInstance().getCnx().prepareStatement(requete);
            st.setInt(1, formationId);
            ResultSet rs = st.executeQuery();
            while (rs.next()) {
                // On récupère la formation associée à chaque évaluation via FormationImpl
                Formation formation = formationImpl.findById(formationId);
                evaluations.add(new Evaluation(rs.getInt("id"), rs.getString("commentaire"), rs.getInt("note"), formation));
            }
        } catch (Exception e) {
            System.out.println("Erreur lors de la récupération des évaluations : " + e.getMessage());
        }
        return evaluations;
    }
}
