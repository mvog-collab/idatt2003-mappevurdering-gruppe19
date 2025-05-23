package edu.games.engine.board;

import edu.games.engine.model.LudoColor;

/**
 * Returns the next tile for the given player color.
 * This may differ depending on whether the player is entering the goal path.
 *
 * @param player the player's color
 * @return the next tile in the path
 */
public interface LudoTile extends Tile {
  LudoTile next(LudoColor player);
}
