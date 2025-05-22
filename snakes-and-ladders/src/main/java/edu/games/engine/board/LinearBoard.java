package edu.games.engine.board;

import edu.games.engine.exception.ValidationException;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents a linear board with tiles linked in a straight path.
 * <p>
 * Supports basic tile movement and end-tile checking.
 */
public final class LinearBoard implements Board {

  private final Map<Integer, LinearTile> tiles = new HashMap<>();

  /**
   * Constructs a linear board with the given number of tiles.
   * The board includes tiles from 0 to {@code size}.
   *
   * @param size the number of tiles (must be at least 2)
   * @throws ValidationException if size is less than 2
   */
  public LinearBoard(int size) {
    if (size < 2) {
      throw new ValidationException("Invalid boardSize: cannot be less than 2");
    }
    LinearTile prev = null;
    for (int i = 0; i <= size; i++) {
      LinearTile tile = new LinearTile(i);
      tiles.put(i, tile);
      if (prev != null) {
        prev.nextTile = tile;
      }
      prev = tile;
    }
  }

  /**
   * Constructs a linear board using a predefined tile map.
   *
   * @param tiles a non-null, non-empty map of tile ID to {@link LinearTile}
   * @throws ValidationException if map is null or empty
   */
  public LinearBoard(Map<Integer, LinearTile> tiles) {
    if (tiles == null || tiles.isEmpty()) {
      throw new ValidationException("Invalid map: cannot be null or empty");
    }
    this.tiles.putAll(tiles);
  }

  /**
   * Returns the starting tile of the board (ID 0).
   *
   * @return the start {@link Tile}
   */
  @Override
  public Tile start() {
    return tiles.get(0);
  }

  /**
   * Moves a piece a number of steps from the given tile.
   * Stops if it reaches the last tile.
   *
   * @param from  the tile to start from
   * @param steps the number of steps to move (must be non-negative)
   * @return the destination {@link Tile}
   * @throws ValidationException if steps are negative or tile is not a {@link LinearTile}
   */
  @Override
  public Tile move(Tile from, int steps) {
    if (steps < 0) {
      throw new ValidationException("steps cannot be < 0");
    }
    LinearTile tile = cast(from);
    for (int i = 0; i < steps && tile.next() != null; i++) {
      tile = tile.next();
    }
    return tile;
  }

  /**
   * Returns the tile with the given ID.
   *
   * @param id the tile ID
   * @return the {@link LinearTile} or {@code null} if not found
   */
  public LinearTile tile(int id) {
    return tiles.get(id);
  }

  /**
   * Checks if the given tile is the last tile on the board.
   *
   * @param tile the tile to check
   * @return {@code true} if it's the last tile, {@code false} otherwise
   * @throws ValidationException if the tile is not a {@link LinearTile}
   */
  @Override
  public boolean isEnd(Tile tile) {
    return cast(tile).next() == null;
  }

  /**
   * Casts a generic {@link Tile} to a {@link LinearTile}.
   *
   * @param tile the tile to cast
   * @return the casted {@link LinearTile}
   * @throws ValidationException if the tile is not a {@link LinearTile}
   */
  private static LinearTile cast(Tile tile) {
    if (!(tile instanceof LinearTile lt)) {
      throw new ValidationException("Not a LinearTile");
    }
    return lt;
  }
}
