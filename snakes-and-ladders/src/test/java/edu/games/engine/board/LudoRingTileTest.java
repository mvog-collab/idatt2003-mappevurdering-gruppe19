package edu.games.engine.board;

import edu.games.engine.model.LudoColor;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LudoRingTileTest {

  @Test
  void shouldReturnCorrectTileId() {
    LudoRingTile tile = new LudoRingTile(14);
    assertEquals(14, tile.tileId());
  }

  @Test
  void shouldReturnNextTile() {
    LudoRingTile first = new LudoRingTile(1);
    LudoRingTile second = new LudoRingTile(2);
    first.setNext(second);
    assertSame(second, first.next(LudoColor.BLUE));
  }

  @Test
  void shouldReturnGoalEntryIfSet() {
    LudoRingTile tile = new LudoRingTile(14);
    LudoGoalTile goal = new LudoGoalTile(59);
    tile.goalEntry(goal);
    assertSame(goal, tile.goalEntry());
  }

  @Test
  void shouldReturnNullGoalEntryIfNotSet() {
    LudoRingTile tile = new LudoRingTile(14);
    assertNull(tile.goalEntry());
  }

  @Test
  void shouldNotEnterGoalPathIfCurrentlyOnEntryPoint() {
    LudoRingTile tile = new LudoRingTile(14);
    assertFalse(tile.shouldEnterGoalPath(14, LudoColor.RED));
  }

  @Test
  void shouldEnterGoalPathIfPassingEntryPoint() {
    LudoRingTile tile = new LudoRingTile(14);
    assertTrue(tile.shouldEnterGoalPath(10, LudoColor.RED));
  }

  @Test
  void shouldNotEnterGoalPathIfNotEntryPoint() {
    LudoRingTile tile = new LudoRingTile(10);
    assertFalse(tile.shouldEnterGoalPath(5, LudoColor.RED));
  }

  @Test
  void shouldEnterGoalPathOnlyForCorrectColor() {
    LudoRingTile tile = new LudoRingTile(14); // RED entry
    assertFalse(tile.shouldEnterGoalPath(14, LudoColor.GREEN));
    assertFalse(tile.shouldEnterGoalPath(10, LudoColor.GREEN));
    assertTrue(tile.shouldEnterGoalPath(10, LudoColor.RED));
  }
}
