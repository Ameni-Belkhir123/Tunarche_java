package TunArche.services;

import TunArche.entities.Vote;
import java.util.List;

public interface VoteService {
    // CRUD operations
    void save(Vote vote);
    void update(Vote vote);
    void delete(int id);
    void deleteByUserAndParticipation(int userId, int participationId);

    // Read operations
    Vote findById(int id);
    List<Vote> findAll();
    List<Vote> findByUserId(int userId);
    List<Vote> findByParticipationId(int participationId);
    List<Vote> findByConcoursId(int concoursId);

    // Check if user has voted
    boolean hasUserVoted(int userId, int participationId);
    boolean hasUserVotedInConcours(int userId, int concoursId);

    // Count votes
    int countVotesForParticipation(int participationId);
}