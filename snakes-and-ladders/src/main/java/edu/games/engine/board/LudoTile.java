package edu.games.engine.board;

import edu.games.engine.model.LudoColor;

/**
 * Represents a single position or square on the a Ludo board.
 */
public interface LudoTile extends Tile {
  /**
   * Returns the next tile for the given player color.
   * This may differ depending on whether the player is entering the goal path.
   *
   * @param player the player's color
   * @return the next tile in the path
   */
  LudoTile next(LudoColor player);
}
