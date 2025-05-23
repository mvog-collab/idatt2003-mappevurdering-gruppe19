package edu.games.engine.board;

/**
 * Represents a tile in a linear board.
 * <p>
 * Each tile has a unique ID and optionally a reference to the next tile in the
 * sequence.
 */
public class LinearTile implements Tile {
  private final int tileId;
  // Not private because of usage within engine(package)
  LinearTile nextTile;

  /**
   * Creates a tile with a given ID.
   *
   * @param tileId the ID of this tile
   */
  public LinearTile(int tileId) {
    this.tileId = tileId;
  }

  /**
   * Returns the ID of this tile.
   *
   * @return the tile ID
   */
  @Override
  public int tileId() {
    return tileId;
  }

  /**
   * Returns the next tile in the sequence, or {@code null} if this is the last
   * tile.
   *
   * @return the next {@link LinearTile}, or {@code null} if none
   */
  public LinearTile next() {
    return nextTile;
  }
}
