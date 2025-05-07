package TunArche.interfaces;

import TunArche.entities.Billet;
import java.util.List;

public interface IBillet <T>  {
    void create(T b);
    void update(T b);
    void delete(int id);
    Billet getById(int id);
    List<T> getAll();
}
