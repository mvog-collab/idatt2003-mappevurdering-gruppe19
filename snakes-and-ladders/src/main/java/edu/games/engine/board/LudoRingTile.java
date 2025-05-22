package edu.games.engine.board;

import edu.games.engine.model.LudoColor;

public final class LudoRingTile implements LudoTile {
  private final int id;
  private LudoTile next;
  private LudoTile goalEntry;

  public LudoRingTile(int id) {
    this.id = id;
  }

  @Override
  public int id() {
    return id;
  }

  public void setNext(LudoTile next) {
    this.next = next;
  }

  public void goalEntry(LudoTile goalEntry) {
    this.goalEntry = goalEntry;
  }

  @Override
  public LudoTile next(LudoColor ownerIgnored) {
    return next;
  }

  // Add a new method to check if we should enter the goal path
  public boolean shouldEnterGoalPath(int currentId, LudoColor owner) {
    // Check if we're passing the entry point (not just landing on it)
    int entryPoint =
        switch (owner) {
          case BLUE -> 1;
          case RED -> 14;
          case GREEN -> 27;
          case YELLOW -> 40;
        };

    return id() == entryPoint && currentId != entryPoint;
  }

  public LudoTile goalEntry() {
    return this.goalEntry;
  }
}
