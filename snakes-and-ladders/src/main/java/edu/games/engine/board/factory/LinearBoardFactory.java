package edu.games.engine.board.factory;

import edu.games.engine.board.Board;
import edu.games.engine.board.LinearBoard;

/**
 * Factory for creating {@link LinearBoard} instances.
 * <p>
 * This implementation of {@link JsonBoardLoader} returns a linear board with the specified size.
 */
public class LinearBoardFactory implements JsonBoardLoader {

  /**
   * Creates a new {@link LinearBoard} with the given number of tiles.
   *
   * @param size the number of tiles for the board
   * @return a new {@link LinearBoard} instance
   */
  @Override
  public Board create(int size) {
    return new LinearBoard(size);
  }
}
