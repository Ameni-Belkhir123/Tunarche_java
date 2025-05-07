
package TunArche.interfaces;
import java.util.List;

public interface ICommantaire<T> {
    void create(T commantaire);
    void update(T commantaire);
    void delete(int id);
    T findById(int id);
    List<T> showByPublication(int publicationId); }
