package TunArche.services;

import TunArche.entities.Vote;
import TunArche.tools.MyConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class VoteImpl implements TunArche.services.VoteService {

    private Connection cnx;

    public VoteImpl() {
        this.cnx = MyConnection.getInstance().getCnx();
    }

    @Override
    public void save(Vote vote) {
        // Check if user has already voted for this participation
        if (hasUserVoted(vote.getUser_id(), vote.getParticipation_id())) {
            System.out.println("Utilisateur a déjà voté pour cette participation!");
            return;
        }

        String query = "INSERT INTO vote (user_id, participation_id, concours_id) VALUES (?, ?, ?)";

        try (PreparedStatement ps = cnx.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, vote.getUser_id());
            ps.setInt(2, vote.getParticipation_id());
            ps.setInt(3, vote.getConcours_id());

            int affectedRows = ps.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("La création du vote a échoué, aucune ligne n'a été ajoutée.");
            }

            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    vote.setId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("La création du vote a échoué, aucun ID n'a été retourné.");
                }
            }

            System.out.println("Vote enregistré avec succès !");

            // Mise à jour du nombre de votes pour la participation
            updateVoteCount(vote.getParticipation_id());

        } catch (SQLException e) {
            System.err.println("Erreur lors de l'enregistrement du vote: " + e.getMessage());
        }
    }

    @Override
    public void update(Vote vote) {
        String query = "UPDATE vote SET user_id=?, participation_id=?, concours_id=? WHERE id=?";

        try (PreparedStatement ps = cnx.prepareStatement(query)) {
            ps.setInt(1, vote.getUser_id());
            ps.setInt(2, vote.getParticipation_id());
            ps.setInt(3, vote.getConcours_id());
            ps.setInt(4, vote.getId());

            int affectedRows = ps.executeUpdate();

            if (affectedRows > 0) {
                System.out.println("Vote mis à jour avec succès !");
            } else {
                System.out.println("Aucun vote trouvé avec l'ID: " + vote.getId());
            }

        } catch (SQLException e) {
            System.err.println("Erreur lors de la mise à jour du vote: " + e.getMessage());
        }
    }

    @Override
    public void delete(int id) {
        // First get the participation_id to update vote count later
        Vote vote = findById(id);
        int participationId = 0;
        if (vote != null) {
            participationId = vote.getParticipation_id();
        }

        String query = "DELETE FROM vote WHERE id=?";

        try (PreparedStatement ps = cnx.prepareStatement(query)) {
            ps.setInt(1, id);

            int affectedRows = ps.executeUpdate();

            if (affectedRows > 0) {
                System.out.println("Vote supprimé avec succès !");
                // Update vote count if we had a valid participation ID
                if (participationId > 0) {
                    updateVoteCount(participationId);
                }
            } else {
                System.out.println("Aucun vote trouvé avec l'ID: " + id);
            }

        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression du vote: " + e.getMessage());
        }
    }

    @Override
    public void deleteByUserAndParticipation(int userId, int participationId) {
        String query = "DELETE FROM vote WHERE user_id=? AND participation_id=?";

        try (PreparedStatement ps = cnx.prepareStatement(query)) {
            ps.setInt(1, userId);
            ps.setInt(2, participationId);

            int affectedRows = ps.executeUpdate();

            if (affectedRows > 0) {
                System.out.println("Vote de l'utilisateur " + userId + " pour la participation " + participationId + " supprimé avec succès !");
                // Update vote count
                updateVoteCount(participationId);
            } else {
                System.out.println("Aucun vote trouvé pour l'utilisateur " + userId + " et participation " + participationId);
            }

        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression du vote: " + e.getMessage());
        }
    }

    @Override
    public Vote findById(int id) {
        String query = "SELECT * FROM vote WHERE id=?";

        try (PreparedStatement ps = cnx.prepareStatement(query)) {
            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return extractVoteFromResultSet(rs);
                }
            }

        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche du vote: " + e.getMessage());
        }

        return null;
    }

    @Override
    public List<Vote> findAll() {
        List<Vote> votes = new ArrayList<>();
        String query = "SELECT * FROM vote";

        try (Statement stmt = cnx.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                votes.add(extractVoteFromResultSet(rs));
            }

        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des votes: " + e.getMessage());
        }

        return votes;
    }

    @Override
    public List<Vote> findByUserId(int userId) {
        List<Vote> votes = new ArrayList<>();
        String query = "SELECT * FROM vote WHERE user_id=?";

        try (PreparedStatement ps = cnx.prepareStatement(query)) {
            ps.setInt(1, userId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    votes.add(extractVoteFromResultSet(rs));
                }
            }

        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche des votes de l'utilisateur: " + e.getMessage());
        }

        return votes;
    }

    @Override
    public List<Vote> findByParticipationId(int participationId) {
        List<Vote> votes = new ArrayList<>();
        String query = "SELECT * FROM vote WHERE participation_id=?";

        try (PreparedStatement ps = cnx.prepareStatement(query)) {
            ps.setInt(1, participationId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    votes.add(extractVoteFromResultSet(rs));
                }
            }

        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche des votes pour la participation: " + e.getMessage());
        }

        return votes;
    }

    @Override
    public List<Vote> findByConcoursId(int concoursId) {
        List<Vote> votes = new ArrayList<>();
        String query = "SELECT * FROM vote WHERE concours_id=?";

        try (PreparedStatement ps = cnx.prepareStatement(query)) {
            ps.setInt(1, concoursId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    votes.add(extractVoteFromResultSet(rs));
                }
            }

        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche des votes du concours: " + e.getMessage());
        }

        return votes;
    }

    @Override
    public boolean hasUserVoted(int userId, int participationId) {
        String query = "SELECT COUNT(*) FROM vote WHERE user_id=? AND participation_id=?";

        try (PreparedStatement ps = cnx.prepareStatement(query)) {
            ps.setInt(1, userId);
            ps.setInt(2, participationId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }

        } catch (SQLException e) {
            System.err.println("Erreur lors de la vérification du vote: " + e.getMessage());
        }

        return false;
    }

    @Override
    public boolean hasUserVotedInConcours(int userId, int concoursId) {
        String query = "SELECT COUNT(*) FROM vote WHERE user_id=? AND concours_id=?";

        try (PreparedStatement ps = cnx.prepareStatement(query)) {
            ps.setInt(1, userId);
            ps.setInt(2, concoursId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }

        } catch (SQLException e) {
            System.err.println("Erreur lors de la vérification du vote pour le concours: " + e.getMessage());
        }

        return false;
    }

    @Override
    public int countVotesForParticipation(int participationId) {
        String query = "SELECT COUNT(*) FROM vote WHERE participation_id=?";

        try (PreparedStatement ps = cnx.prepareStatement(query)) {
            ps.setInt(1, participationId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }

        } catch (SQLException e) {
            System.err.println("Erreur lors du comptage des votes: " + e.getMessage());
        }

        return 0;
    }

    // Helper method to update the vote count in the participation table
    private void updateVoteCount(int participationId) {
        String updateQuery = "UPDATE participation SET nbr_votes=? WHERE id=?";
        int voteCount = countVotesForParticipation(participationId);

        try (PreparedStatement ps = cnx.prepareStatement(updateQuery)) {
            ps.setInt(1, voteCount);
            ps.setInt(2, participationId);

            ps.executeUpdate();
            System.out.println("Nombre de votes mis à jour pour la participation " + participationId + ": " + voteCount);

        } catch (SQLException e) {
            System.err.println("Erreur lors de la mise à jour du nombre de votes: " + e.getMessage());
        }
    }

    // Helper method to extract a Vote object from a ResultSet
    private Vote extractVoteFromResultSet(ResultSet rs) throws SQLException {
        Vote vote = new Vote();
        vote.setId(rs.getInt("id"));
        vote.setUser_id(rs.getInt("user_id"));
        vote.setParticipation_id(rs.getInt("participation_id"));
        vote.setConcours_id(rs.getInt("concours_id"));
        return vote;
    }
}