package TunArche.interfaces;

import TunArche.entities.user;
import java.util.List;
public interface Iuser <T> {
    void create(T user);
    void update(T user);
    void delete(int id);
    T findById(int id);
    List<T> showAll();
}
