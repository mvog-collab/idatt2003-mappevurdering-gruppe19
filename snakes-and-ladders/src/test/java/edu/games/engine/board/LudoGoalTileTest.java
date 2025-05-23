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

  @Test
  void nextShouldReturnNullIfNotLinked_evenWithColor() {
    LudoGoalTile tile = new LudoGoalTile(7);
    for (LudoColor color : LudoColor.values()) {
      assertNull(tile.next(color), "Expected null when next is not linked");
    }
  }

  @Test
  void shouldHandleSelfLinkingGracefully() {
    LudoGoalTile tile = new LudoGoalTile(3);
    tile.linkNext(tile); // next tile is itself
    assertSame(tile, tile.next());
    assertSame(tile, tile.next(LudoColor.RED));
  }

  @Test
  void shouldAllowOverwritingNextTile() {
    LudoGoalTile tile = new LudoGoalTile(5);
    LudoGoalTile firstNext = new LudoGoalTile(6);
    LudoGoalTile secondNext = new LudoGoalTile(7);

    tile.linkNext(firstNext);
    assertSame(firstNext, tile.next());

    tile.linkNext(secondNext);
    assertSame(secondNext, tile.next());
  }
}
