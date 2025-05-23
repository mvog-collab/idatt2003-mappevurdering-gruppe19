package edu.games.engine.board;

import edu.games.engine.model.LudoColor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * Represents the full Ludo movement path.
 * <p>
 * This includes a shared ring of 52 tiles and color-specific goal paths (6
 * tiles per color).
 * Responsible for handling piece movement based on color and steps.
 */
public class LudoPath implements MovementPath {

  private final List<LudoRingTile> ring = new ArrayList<>(52);
  private final Map<LudoColor, List<LudoGoalTile>> goals = new EnumMap<>(LudoColor.class);
  private final Map<LudoColor, LudoRingTile> startingPoints = new EnumMap<>(LudoColor.class);

  /**
   * Builds the Ludo path: 52 ring tiles and 4 goal paths (one per color).
   * Sets up tile connections and entry points into goal areas.
   */
  public LudoPath() {
    for (int i = 0; i < 52; i++)
      ring.add(new LudoRingTile(i + 1));
    for (int i = 0; i < 52; i++)
      ring.get(i).setNext(ring.get((i + 1) % 52));

    Map<LudoColor, Integer> goalStartIds = Map.of(
        LudoColor.BLUE, 53,
        LudoColor.RED, 59,
        LudoColor.GREEN, 65,
        LudoColor.YELLOW, 71);

    for (LudoColor c : LudoColor.values()) {
      List<LudoGoalTile> lane = new ArrayList<>(6);
      int startId = goalStartIds.get(c);
      for (int j = 0; j < 6; j++)
        lane.add(new LudoGoalTile(startId + j));
      for (int j = 0; j < 5; j++)
        lane.get(j).linkNext(lane.get(j + 1));
      goals.put(c, lane);

      int entryIndex = switch (c) {
        case BLUE -> 0;
        case RED -> 13;
        case GREEN -> 26;
        case YELLOW -> 39;
      };

      LudoRingTile startTile = ring.get(entryIndex);
      startingPoints.put(c, startTile);
      startTile.goalEntry(lane.getFirst());
    }
  }

  /**
   * Gets the entry tile for a player of a given color.
   * Used when the player rolls a six and can leave home.
   *
   * @param color the player's color
   * @return the start tile on the ring
   */
  public LudoTile getStartTile(LudoColor color) {
    return startingPoints.get(color);
  }

  /**
   * Returns the result of moving from a tile with a number of steps.
   * Handles transitions from ring to goal path.
   *
   * @param from  the starting tile
   * @param steps number of steps to move
   * @param owner the player's color
   * @return the destination tile after movement
   */
  @Override
  public Tile nextTile(Tile from, int steps, LudoColor owner) {
    if (from == null) {
      return (steps == 6) ? startingPoints.get(owner) : null;
    }

    LudoTile currentTile = (LudoTile) from;
    int entryId = getEntryPointId(owner);
    int preEntryId = (entryId == 1) ? 52 : entryId - 1;

    for (int i = 0; i < steps; i++) {
      LudoTile next;
      if (currentTile instanceof LudoRingTile ringTile) {
        if (ringTile.tileId() == preEntryId &&
            ringTile.next(null) != null &&
            ringTile.next(null).tileId() == entryId) {
          next = goals.get(owner).getFirst();
        } else {
          next = ringTile.next(null);
        }
      } else if (currentTile instanceof LudoGoalTile goalTile) {
        next = goalTile.next(null);
      } else {
        return currentTile;
      }

      if (next == null)
        return currentTile;
      currentTile = next;
    }

    return currentTile;
  }

  /**
   * Checks if the given tile is the final tile in a goal path.
   *
   * @param tile the tile to check
   * @return true if it's the final goal tile, false otherwise
   */
  @Override
  public boolean isEnd(Tile tile) {
    return tile instanceof LudoGoalTile goalTile && goalTile.next() == null;
  }

  /**
   * Returns an unmodifiable view of the ring tiles.
   *
   * @return list of all 52 ring tiles
   */
  public List<LudoRingTile> ring() {
    return List.copyOf(ring);
  }

  /**
   * Returns all goal paths for each player color.
   *
   * @return map of LudoColor to goal tiles
   */
  public Map<LudoColor, List<LudoGoalTile>> goals() {
    return Collections.unmodifiableMap(goals);
  }

  /**
   * Gets the entry point tile ID for a color's goal path.
   *
   * @param color the player's color
   * @return the ring tile ID just before goal entry
   */
  private int getEntryPointId(LudoColor color) {
    return switch (color) {
      case BLUE -> 1;
      case RED -> 14;
      case GREEN -> 27;
      case YELLOW -> 40;
    };
  }

  /**
   * Returns null since players start at home (not on the board).
   */
  @Override
  public Tile start() {
    return null;
  }
}
