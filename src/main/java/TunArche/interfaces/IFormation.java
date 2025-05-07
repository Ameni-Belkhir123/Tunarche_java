package TunArche.interfaces;

import java.util.List;

public interface IFormation <T>{
    void create(T formation);
    void update(T formation);
    void delete(int id);
    T findById(int id);
    List<T> showAll();
}
