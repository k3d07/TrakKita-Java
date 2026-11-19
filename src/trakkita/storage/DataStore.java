package trakkita.storage;

import java.io.IOException;
import java.util.List;
import trakkita.exception.DataCorruptedException;
import trakkita.model.Persistable;

/** The DAO contract. The rest of the app never knows the file format. */
public interface DataStore {
    LoadResult load() throws DataCorruptedException;

    void save(List<? extends Persistable> entries) throws IOException;
}
