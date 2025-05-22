package edu.games.engine.store;

import edu.games.engine.exception.StorageException;
import edu.games.engine.model.Player;
import java.nio.file.Path;
import java.util.List;

public interface PlayerStore {
  void savePlayers(List<Player> players, Path out) throws StorageException;

  List<Player> loadPlayers(Path in) throws StorageException;
}
