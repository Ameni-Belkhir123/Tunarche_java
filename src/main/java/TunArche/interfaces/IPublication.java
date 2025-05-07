package TunArche.interfaces;

import java.util.List;

public interface IPublication<T> {
    void create(T publication);
    void update(T publication);
    void delete(int id);
    T findById(int id);
    List<T> showAll();
}
