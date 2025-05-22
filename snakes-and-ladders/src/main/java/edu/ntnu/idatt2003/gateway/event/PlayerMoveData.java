package edu.ntnu.idatt2003.gateway.event;

import edu.games.engine.board.Tile;
import edu.games.engine.model.Player;

public class PlayerMoveData {
  private final Player player;
  private final Object fromTile;
  private final Object toTile;

  // Constructor for Ludo (Tile objects)
  public PlayerMoveData(Player player, Tile fromTile, Tile toTile) {
    this.player = player;
    this.fromTile = fromTile;
    this.toTile = toTile;
  }

  // Constructor for SNL (tile IDs)
  public PlayerMoveData(Player player, int fromTileId, int toTileId) {
    this.player = player;
    this.fromTile = fromTileId;
    this.toTile = toTileId;
  }

  public Player getPlayer() {
    return player;
  }

  public Object getFrom() {
    return fromTile;
  }

  public Object getTo() {
    return toTile;
  }

  // Convenience methods
  public int getFromTileId() {
    return (fromTile instanceof Tile) ? ((Tile) fromTile).tileId() : (Integer) fromTile;
  }

  public int getToTileId() {
    return (toTile instanceof Tile) ? ((Tile) toTile).tileId() : (Integer) toTile;
  }

  public Tile getFromTile() {
    return (fromTile instanceof Tile) ? (Tile) fromTile : null;
  }

  public Tile getToTile() {
    return (toTile instanceof Tile) ? (Tile) toTile : null;
  }
}
