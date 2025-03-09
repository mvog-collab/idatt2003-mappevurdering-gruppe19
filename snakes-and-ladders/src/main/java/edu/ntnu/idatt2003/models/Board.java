package edu.ntnu.idatt2003.models;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Board {

  private final Map<Integer, Tile> tiles;
  private final int size;

  public Board(int size) {
    this.size = size;
    this.tiles = new HashMap<>();
  }

  public void addTile(Tile tile) {
    if (tile == null) {
      throw new IllegalArgumentException("Tile cannot be null.");
    }
    tiles.put(tile.getTileId(), tile);

    if (tile.getTileId() > 0) {
      Tile previousTile = tiles.get(tile.getTileId() - 1);
      if (previousTile != null) {
        previousTile.setNextTile(tile);
      }
    }
  }

  public Tile getTile(int tileId) {
    return tiles.get(tileId);
  }

  public int getSize() {
    return size;
  }
}
