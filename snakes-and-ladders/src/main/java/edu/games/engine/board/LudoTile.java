package edu.games.engine.board;

import edu.games.engine.model.LudoColor;

/**
 * Represents a tile in the Ludo game path.
 * Ludo tiles can be either part of the main ring or the goal stretch.
 * Each tile knows which tile comes next for a given player.
 *
 * <p>This sealed interface is implemented by:
 * <ul>
 *   <li>{@link LudoRingTile} – tiles on the circular path</li>
 *   <li>{@link LudoGoalTile} – tiles in the player's goal path</li>
 * </ul>
 */
public sealed interface LudoTile extends Tile
    permits LudoRingTile, LudoGoalTile {

  /**
   * Returns the next tile for the given player color.
   * This may differ depending on whether the player is entering the goal path.
   *
   * @param player the player's color
   * @return the next tile in the path
   */
  LudoTile next(LudoColor player);
}
