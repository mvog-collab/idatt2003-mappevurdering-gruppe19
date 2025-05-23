package edu.games.engine.board;

import edu.games.engine.model.LudoColor;

/**
 * Represents a full Ludo game board using a color-based path structure.
 * <p>
 * The board consists of 52 ring tiles and 6 goal tiles per color.
 */
public final class LudoBoard implements Board {

  private final LudoPath path;

  /**
   * Constructs a LudoBoard with a given path structure.
   *
   * @param path the {@link LudoPath} defining movement logic and layout
   */
  public LudoBoard(LudoPath path) {
    this.path = path;
  }

  /**
   * Ludo pieces start from "home", so this method returns {@code null}.
   *
   * @return {@code null} to indicate home spawn
   */
  @Override
  public Tile start() {
    return null;
  }

  /**
   * Checks if the given tile is the final goal tile.
   *
   * @param tile the tile to check
   * @return {@code true} if the tile is a final goal tile, {@code false}
   *         otherwise
   */
  @Override
  public boolean isEnd(Tile tile) {
    return path.isEnd(tile);
  }

  /**
   * Disabled generic movement. Use {@link #move(Tile, int, LudoColor)} instead.
   *
   * @param from  the starting tile
   * @param steps number of steps to move
   * @return same tile, unchanged
   */
  @Override
  public Tile move(Tile from, int steps) {
    return from;
  }

  /**
   * Moves a piece a number of steps based on its color-specific path.
   *
   * @param from  Starting tile
   * @param steps Number of steps to move
   * @param color Player's color
   * @return the resulting tile after movement
   */
  public Tile move(Tile from, int steps, LudoColor color) {
    return path.nextTile(from, steps, color);
  }

  /**
   * Returns the start tile for a player of a given color.
   *
   * @param color the player's color
   * @return the corresponding starting tile
   */
  public Tile getStartTile(LudoColor color) {
    if (path instanceof LudoPath ludoPath) {
      return ludoPath.getStartTile(color);
    }
    return null;
  }

  /**
   * Returns the total number of tiles on the board.
   *
   * @return fixed size of the Ludo board (76)
   */
  public int size() {
    return 76;
  }
}
