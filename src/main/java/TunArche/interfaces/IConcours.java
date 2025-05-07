package TunArche.interfaces;

import java.util.List;

public interface IConcours <T>{

        public boolean existsWithTitle(String titre);

        void create(T concours);
        void update(T concours);
        boolean delete(int id);
        T findById(int id);
        List<T> showAll();
}

