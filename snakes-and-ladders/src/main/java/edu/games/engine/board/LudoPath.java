package edu.games.engine.board;

import edu.games.engine.model.LudoColor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * LudoPath – builds a colored graph of 52 + 4×6 squares Handles the movement logic for a Ludo game
 * with color-specific paths.
 */
public final class LudoPath implements MovementPath {

  private final List<LudoRingTile> ring = new ArrayList<>(52);
  private final Map<LudoColor, List<LudoGoalTile>> goals = new EnumMap<>(LudoColor.class);

  private final Map<LudoColor, LudoRingTile> startingPoints = new EnumMap<>(LudoColor.class);

  public LudoPath() {
    for (int i = 0; i < 52; i++) ring.add(new LudoRingTile(i + 1));
    for (int i = 0; i < 52; i++) ring.get(i).setNext(ring.get((i + 1) % 52));

    Map<LudoColor, Integer> goalStartIds =
        Map.of(
            LudoColor.BLUE, 53,
            LudoColor.RED, 59,
            LudoColor.GREEN, 65,
            LudoColor.YELLOW, 71);

    for (LudoColor c : LudoColor.values()) {
      List<LudoGoalTile> lane = new ArrayList<>(6);
      int startId = goalStartIds.get(c);

      for (int j = 0; j < 6; j++) lane.add(new LudoGoalTile(startId + j));

      for (int j = 0; j < 5; j++) {
        lane.get(j).linkNext(lane.get(j + 1));
      }

      goals.put(c, lane);

      int entryIndex =
          switch (c) {
            case BLUE -> 0;
            case RED -> 13;
            case GREEN -> 26;
            case YELLOW -> 39;
          };

      LudoRingTile startTile = ring.get(entryIndex);
      startingPoints.put(c, startTile);
      ring.get(entryIndex).goalEntry(lane.getFirst());
    }
  }

  /**
   * Get the starting tile for a specific color Called when a player rolls a 6 and can leave home
   */
  public LudoTile getStartTile(LudoColor color) {
    return startingPoints.get(color);
  }

  /* MovementPath implementation ------------------------------------ */

  @Override
  public Tile start() {
    return null; // Players start at home (null tile)
  }

  @Override
  public Tile next(Tile from, int steps, LudoColor owner) {
    if (from == null) { // Piece is at home
      return (steps == 6) ? startingPoints.get(owner) : null;
    }

    LudoTile currentTile = (LudoTile) from;
    int ownerEntryPointRingId = getEntryPointId(owner);
    int ownerPreEntryPointRingId = (ownerEntryPointRingId == 1) ? 52 : ownerEntryPointRingId - 1;

    for (int k = 0; k < steps; k++) {
      LudoTile candidateNextTile = null;

      if (currentTile instanceof LudoRingTile currentRingTile) {
        if (currentRingTile.id() == ownerPreEntryPointRingId
            && currentRingTile.next(null) != null
            && currentRingTile.next(null).id() == ownerEntryPointRingId) {
          candidateNextTile = goals.get(owner).getFirst();
        } else {
          candidateNextTile = currentRingTile.next(null);
        }

      } else if (currentTile instanceof LudoGoalTile currentGoalTile) {
        candidateNextTile = currentGoalTile.next(null);
      }

      if (candidateNextTile == null) {
        return currentTile;
      }
      currentTile = candidateNextTile;
    }
    return currentTile;
  }

  // Helper method to get the entry point ID for a color
  private int getEntryPointId(LudoColor color) {
    return switch (color) {
      case BLUE -> 1;
      case RED -> 14;
      case GREEN -> 27;
      case YELLOW -> 40;
    };
  }

  @Override
  public boolean isEnd(Tile tile) {
    return (tile instanceof LudoGoalTile goalTile) && goalTile.next() == null;
  }

  public List<LudoRingTile> ring() {
    return List.copyOf(ring);
  }

  public Map<LudoColor, List<LudoGoalTile>> goals() {
    return Collections.unmodifiableMap(goals);
  }
}
