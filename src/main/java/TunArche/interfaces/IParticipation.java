package TunArche.interfaces;

import TunArche.entities.Participation;

import java.util.List;

public interface IParticipation <T> {
    void create(T participation);
    void update(T participation);
    void save(Participation participation);

    boolean delete(int id);
    T findById(int id);
    List<T> showByConcours(int concoursId);
    List<T> showAll();
}

