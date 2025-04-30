package edu.games.engine.store;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import edu.games.engine.model.Player;

public interface PlayerStore {
    void save(List<Player> players, Path out) throws IOException;
    List<Player> load(Path in) throws IOException;
}
