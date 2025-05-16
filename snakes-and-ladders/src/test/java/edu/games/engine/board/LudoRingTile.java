package edu.games.engine.board;

import edu.games.engine.model.LudoColor;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LudoRingTileTest {

  @Test
  void shouldReturnCorrectId() {
    LudoRingTile tile = new LudoRingTile(14);
    assertEquals(14, tile.id());
  }

  @Test
  void shouldReturnNextTile() {
    LudoRingTile first = new LudoRingTile(1);
    LudoRingTile second = new LudoRingTile(2);
    first.next(second);
    assertSame(second, first.next(LudoColor.BLUE));
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
}
