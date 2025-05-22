package edu.games.engine.board;

import edu.games.engine.model.LudoColor;

/**
 * Defines movement logic for a board with color-specific paths, such as Ludo.
 */
public interface MovementPath {

  /**
   * Returns the starting tile of the path.
   * For Ludo, this is typically {@code null} since pieces start at home.
   *
   * @return the starting tile, or {@code null} if not applicable
   */
  Tile start();

  /**
   * Calculates the destination tile after moving a given number of steps from the current tile,
   * taking the player's color into account.
   *
   * @param current the current tile
   * @param steps number of steps to move
   * @param player the player's color
   * @return the tile reached after moving
   */
  Tile nextTile(Tile current, int steps, LudoColor player);

  /**
   * Checks if the given tile is the final tile in a player's path.
   *
   * @param tile the tile to check
   * @return {@code true} if the tile is the end of the path, {@code false} otherwise
   */
  boolean isEnd(Tile tile);
}
