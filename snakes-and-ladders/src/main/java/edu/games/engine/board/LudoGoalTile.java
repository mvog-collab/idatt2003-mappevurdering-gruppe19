package edu.games.engine.board;

import edu.games.engine.model.LudoColor;

/**
 * Represents a goal tile in the Ludo board.
 * <p>
 * Goal tiles are color-specific and form a linear path of up to six tiles per player.
 */
public final class LudoGoalTile implements LudoTile {

  private final int tileId;
  private LudoTile nextTile;

  /**
   * Creates a goal tile with a unique ID.
   *
   * @param tileId the identifier for this tile
   */
  public LudoGoalTile(int tileId) {
    this.tileId = tileId;
  }

  /**
   * Returns the ID of this tile.
   *
   * @return tile ID
   */
  @Override
  public int tileId() {
    return tileId;
  }

  /**
   * Links this tile to the next tile in the goal path.
   *
   * @param nextTile the tile to link as the next one
   */
  void linkNext(LudoTile nextTile) {
    this.nextTile = nextTile;
  }

  /**
   * Gets the next tile in the goal path, if any.
   *
   * @return the next goal tile or {@code null} if this is the last
   */
  LudoTile next() {
    return nextTile;
  }

  /**
   * Returns the next tile for the player. Goal paths are linear,
   * so color is ignored here.
   *
   * @param player the player color (ignored)
   * @return the next goal tile
   */
  @Override
  public LudoTile next(LudoColor player) {
    return nextTile;
  }
}
