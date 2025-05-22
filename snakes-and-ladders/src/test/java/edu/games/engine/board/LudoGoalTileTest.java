package edu.games.engine.board;

import edu.games.engine.model.LudoColor;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LudoGoalTileTest {

  @Test
  void shouldReturnCorrectTileId() {
    LudoGoalTile tile = new LudoGoalTile(42);
    assertEquals(42, tile.tileId());
  }

  @Test
  void shouldReturnNullIfNextNotLinked() {
    LudoGoalTile tile = new LudoGoalTile(1);
    assertNull(tile.next());
  }

  @Test
  void shouldLinkAndReturnNextTile() {
    LudoGoalTile first = new LudoGoalTile(1);
    LudoGoalTile second = new LudoGoalTile(2);
    first.linkNext(second);
    assertSame(second, first.next());
  }

  @Test
  void shouldReturnSameNextRegardlessOfColor() {
    LudoGoalTile first = new LudoGoalTile(1);
    LudoGoalTile second = new LudoGoalTile(2);
    first.linkNext(second);

    for (LudoColor color : LudoColor.values()) {
      assertSame(second, first.next(color));
    }
  }
}
