package edu.ntnu.idatt2003.gateway.event;

import edu.games.engine.board.Tile;
import edu.games.engine.model.Player;

/**
 * Encapsulates data about a player move event.
 * Supports both tile-based moves (Ludo) and ID-based moves (Snakes & Ladders).
 */
public class PlayerMoveData {

  private final Player player;
  private final Object fromTile;
  private final Object toTile;

  /**
   * Creates a move record for games using {@link Tile} objects (e.g., Ludo).
   *
   * @param player   the player who moved
   * @param fromTile the starting tile of the move
   * @param toTile   the destination tile of the move
   */
  public PlayerMoveData(Player player, Tile fromTile, Tile toTile) {
    this.player = player;
    this.fromTile = fromTile;
    this.toTile = toTile;
  }

  /**
   * Creates a move record for games using integer tile IDs (e.g., Snakes & Ladders).
   *
   * @param player     the player who moved
   * @param fromTileId the starting tile ID of the move
   * @param toTileId   the destination tile ID of the move
   */
  public PlayerMoveData(Player player, int fromTileId, int toTileId) {
    this.player = player;
    this.fromTile = fromTileId;
    this.toTile = toTileId;
  }

  /**
   * Returns the player involved in this move.
   *
   * @return the moving player
   */
  public Player getPlayer() {
    return player;
  }

  /**
   * Returns the raw "from" value, which may be a {@link Tile} or an {@link Integer}.
   *
   * @return the starting tile or tile ID
   */
  public Object getFrom() {
    return fromTile;
  }

  /**
   * Returns the raw "to" value, which may be a {@link Tile} or an {@link Integer}.
   *
   * @return the destination tile or tile ID
   */
  public Object getTo() {
    return toTile;
  }

  /**
   * Retrieves the starting tile ID, regardless of representation.
   *
   * @return the starting tile ID
   */
  public int getFromTileId() {
    return (fromTile instanceof Tile) ? ((Tile) fromTile).tileId() : (Integer) fromTile;
  }

  /**
   * Retrieves the destination tile ID, regardless of representation.
   *
   * @return the destination tile ID
   */
  public int getToTileId() {
    return (toTile instanceof Tile) ? ((Tile) toTile).tileId() : (Integer) toTile;
  }

  /**
   * Returns the starting {@link Tile} if available, or {@code null} if this instance
   * was created using integer IDs.
   *
   * @return the starting Tile or null
   */
  public Tile getFromTile() {
    return (fromTile instanceof Tile) ? (Tile) fromTile : null;
  }

  /**
   * Returns the destination {@link Tile} if available, or {@code null} if this instance
   * was created using integer IDs.
   *
   * @return the destination Tile or null
   */
  public Tile getToTile() {
    return (toTile instanceof Tile) ? (Tile) toTile : null;
  }
}
