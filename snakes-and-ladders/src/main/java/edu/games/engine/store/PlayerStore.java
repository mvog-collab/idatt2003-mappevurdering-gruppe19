package edu.games.engine.store;

import edu.games.engine.exception.StorageException;
import edu.games.engine.model.Player;
import java.nio.file.Path;
import java.util.List;

/**
 * Interface for saving and loading players to and from persistent storage.
 */
public interface PlayerStore {

  /**
   * Saves a list of players to the given file path.
   *
   * @param players the players to save
   * @param out     the file path to write to
   * @throws StorageException if saving fails due to I/O or validation issues
   */
  void savePlayers(List<Player> players, Path out) throws StorageException;

  /**
   * Loads a list of players from the given file path.
   *
   * @param in the file path to read from
   * @return a list of players loaded from the file
   * @throws StorageException if loading fails due to I/O or parsing issues
   */
  List<Player> loadPlayers(Path in) throws StorageException;
}
