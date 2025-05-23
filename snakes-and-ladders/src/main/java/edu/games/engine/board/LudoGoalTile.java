package edu.games.engine.board;

import edu.games.engine.model.LudoColor;

public class LudoGoalTile implements LudoTile {
  private final int tileId;
  private LudoTile nextTile;

  public LudoGoalTile(int tileId) {
    this.tileId = tileId;
  }

  @Override
  public int tileId() {
    return tileId;
  }

  void linkNext(LudoTile nextTile) {
    this.nextTile = nextTile;
  }

  LudoTile next() {
    return nextTile;
  }

  @Override
  public LudoTile next(LudoColor player) {
    return nextTile;
  }
}
