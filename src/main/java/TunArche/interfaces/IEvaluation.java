package TunArche.interfaces;

import TunArche.entities.Evaluation;
import TunArche.entities.Formation;

import java.util.List;

public interface IEvaluation <T> {
    void create(T evaluation);
    void update(T evaluation);
    void delete(int id);
    T findById(int id);
    List<T> showByFormation(int formationId);
}
