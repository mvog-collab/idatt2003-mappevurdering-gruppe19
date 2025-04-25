package edu.games.engine;

import java.util.HashMap;
import java.util.Map;

public final class LinearBoard implements Board {

    private final Map<Integer, LinearTile> tiles = new HashMap<>();
  
    /** builds a 1-based, linear board of the given size */
    public LinearBoard(int size) {
      if (size < 2) {
        throw new IllegalArgumentException("Invalid size: cannot be less than 2");
      }
      LinearTile prev = null;
      for (int i = 0; i <= size; i++) {
        LinearTile tile = new LinearTile(i);
        tiles.put(i, tile);
        if (prev != null) {
            prev.next = tile;
        }
        prev = tile;
      }
    }
  
    /** convenience ctor used by adapters that already have tiles */
    LinearBoard(Map<Integer, LinearTile> tiles) { 
        this.tiles.putAll(tiles); 
    }
  
    @Override 
    public Tile start() { 
        return tiles.get(0); 
    }
  
    @Override
    public Tile move(Tile from, int steps) {
      if (steps < 0) {
          throw new IllegalArgumentException("steps < 0");
      }
      LinearTile tile = cast(from);
      for (int i = 0; i < steps && tile.next != null; i++) {
        tile = tile.next;
      }
      return tile;
    }
  
    public LinearTile getTileId(int id) { 
        return tiles.get(id); 
    }
  
    private static LinearTile cast(Tile tile) {
      if (!(tile instanceof LinearTile lt)) {
          throw new IllegalArgumentException("Not a LinearTile");
      }
      return lt;
    }
  }