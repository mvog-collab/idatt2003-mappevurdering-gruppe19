package edu.games.engine.board;

import edu.games.engine.model.LudoColor;

/**
 * Represents a tile on the main Ludo ring path.
 * Each ring tile points to the next tile in the circular path.
 * Some tiles also act as entry points into goal paths for specific colors.
 */
public final class LudoRingTile implements LudoTile {

  private final int tileId;
  private LudoTile nextTile;
  private LudoTile goalEntry;

  /**
   * Constructs a ring tile with the given ID.
   *
   * @param tileId the tile's identifier (1–52)
   */
  public LudoRingTile(int tileId) {
    this.tileId = tileId;
  }

  /**
   * Gets the unique ID of this tile.
   *
   * @return the tile's ID
   */
  @Override
  public int tileId() {
    return tileId;
  }

  /**
   * Sets the next tile in the ring path.
   *
   * @param nextTile the next tile
   */
  public void setNext(LudoTile nextTile) {
    this.nextTile = nextTile;
  }

  /**
   * Links this tile to a goal path entry tile.
   *
   * @param goalEntry the first tile in the player's goal stretch
   */
  public void goalEntry(LudoTile goalEntry) {
    this.goalEntry = goalEntry;
  }

  /**
   * Returns the next tile in the ring path.
   * Ignores the player color.
   *
   * @param ownerIgnored unused
   * @return the next ring tile
   */
  @Override
  public LudoTile next(LudoColor ownerIgnored) {
    return nextTile;
  }

  /**
   * Checks whether this tile is the entry point into the player's goal path.
   *
   * @param currentId the ID of the current tile
   * @param player    the player's color
   * @return true if entering the goal path, false otherwise
   */
  public boolean shouldEnterGoalPath(int currentId, LudoColor player) {
    int entryPoint = switch (player) {
      case BLUE -> 1;
      case RED -> 14;
      case GREEN -> 27;
      case YELLOW -> 40;
    };
    return tileId() == entryPoint && currentId != entryPoint;
  }

  /**
   * Gets the entry tile into the goal path, if any.
   *
   * @return the goal entry tile or null if not set
   */
  public LudoTile goalEntry() {
    return this.goalEntry;
  }
}
