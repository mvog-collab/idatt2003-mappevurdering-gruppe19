package edu.games.engine.board;

/**
 * Represents a generic board used in a board game.
 * <p>
 * Provides basic methods for moving pieces and checking board conditions.
 */
public interface Board {

  /**
   * Returns the starting tile of the board.
   *
   * @return the starting {@link Tile}
   */
  Tile start();

  /**
   * Moves a piece a number of steps from a given tile.
   *
   * @param from  the tile the piece is currently on
   * @param steps the number of steps to move
   * @return the destination {@link Tile} after movement
   */
  Tile move(Tile from, int steps);

  /**
   * Checks if the given tile is the final tile on the board.
   *
   * @param tile the tile to check
   * @return {@code true} if the tile is the end tile, otherwise {@code false}
   */
  boolean isEnd(Tile tile);
}
