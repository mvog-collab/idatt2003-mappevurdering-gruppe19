package edu.games.engine.board;

public class LinearTile implements Tile {
  private final int tileId;
  LinearTile nextTile;

  public LinearTile(int tileId) {
    this.tileId = tileId;
  }

  @Override
  public int tileId() {
    return tileId;
  }

  public LinearTile next() {
    return nextTile;
  }
}
