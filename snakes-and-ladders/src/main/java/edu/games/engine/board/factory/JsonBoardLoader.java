package edu.games.engine.board.factory;

import edu.games.engine.board.Board;

/**
 * Interface for loading or creating a {@link Board} instance, typically from structured data like JSON.
 * <p>
 * Implementations define how a board of a given size should be created.
 */
public interface JsonBoardLoader {

  /**
   * Creates a new {@link Board} instance with the specified size.
   *
   * @param size the desired size or number of tiles on the board
   * @return a new {@link Board} instance
   */
  Board create(int size);
}
