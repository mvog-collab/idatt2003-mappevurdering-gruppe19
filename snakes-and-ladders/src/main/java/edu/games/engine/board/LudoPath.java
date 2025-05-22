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
    for (int i = 0; i < 52; i++) ring.get(i).next(ring.get((i + 1) % 52));

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

      // Set the starting tile
      LudoRingTile startTile = ring.get(entryIndex);
      startingPoints.put(c, startTile);

      // Connect entry point to goal path
      // The key part: each entry point needs to connect to its goal path
      ring.get(entryIndex).goalEntry(lane.getFirst());

      System.out.println(
          "Set up "
              + c
              + " to enter goal at tile "
              + (entryIndex + 1)
              + " leading to goal tile "
              + lane.getFirst().tileId());
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
  public Tile nextTile(Tile from, int steps, LudoColor player) {
    // If player is at home, they need a 6 to get out
    if (from == null) {
      if (steps != 6) {
        return null;
      }
      // Return the starting point for this color
      return startingPoints.get(player);
    }

    // If the player is already on the board
    LudoTile currentTile = (LudoTile) from;
    LudoTile tile = currentTile;

    // Get the entry point for this color
    int entryPointId = getEntryPointId(player);

    // Move the specified number of steps
    for (int i = 0; i < steps; i++) {
      // Check if we're on the ring and about to pass our entry point
      if (tile instanceof LudoRingTile
          && tile.tileId() != entryPointId
          && // Not currently on entry point
          tile.next(player).tileId() == entryPointId) { // Next would be the entry point

        // Instead of continuing on the ring, we should enter the goal path
        tile = goals.get(player).getFirst();
        System.out.println(
            "Player " + player + " entering goal path at step " + (i + 1) + " of " + steps);
      } else {
        // Regular movement
        LudoTile next = tile.next(player);
        if (next == null) {
          // We're at the very last goal-tile; can't go any further
          return tile;
        }
        tile = next;
      }
    }

    return tile;
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
