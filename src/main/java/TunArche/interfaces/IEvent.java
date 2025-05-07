package TunArche.interfaces;

import TunArche.entities.Event;
import java.util.List;

public interface IEvent <T> {
    void create(T e);
    void update(T e);
    void delete(int id);
    Event getById(int id);
    Event getevent(int id);
    List<T> getAll();
}
