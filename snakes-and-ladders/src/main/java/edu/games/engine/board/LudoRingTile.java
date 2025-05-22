package edu.games.engine.board;

import edu.games.engine.model.LudoColor;

public final class LudoRingTile implements LudoTile {
  private final int tileId;
  private LudoTile nextTile;
  private LudoTile goalEntry;

  public LudoRingTile(int tileId) {
    this.tileId = tileId;
  }

  @Override
  public int tileId() {
    return tileId;
  }

  public void setNext(LudoTile nextTile) {
    this.nextTile = nextTile;
  }

  public void goalEntry(LudoTile goalEntry) {
    this.goalEntry = goalEntry;
  }

  @Override
  public LudoTile next(LudoColor ownerIgnored) {
    return nextTile;
  }

  // Add a new method to check if we should enter the goal path
  public boolean shouldEnterGoalPath(int currentId, LudoColor player) {
    // Check if we're passing the entry point (not just landing on it)
    int entryPoint =
        switch (player) {
          case BLUE -> 1;
          case RED -> 14;
          case GREEN -> 27;
          case YELLOW -> 40;
        };

    return tileId() == entryPoint && currentId != entryPoint;
  }

  public LudoTile goalEntry() {
    return this.goalEntry;
  }
}
