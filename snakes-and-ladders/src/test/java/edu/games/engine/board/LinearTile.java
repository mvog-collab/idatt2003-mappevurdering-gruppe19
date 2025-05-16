package edu.games.engine.board;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LinearTileTest {

  @Test
  void shouldReturnCorrectId() {
    LinearTile tile = new LinearTile(7);
    assertEquals(7, tile.id());
  }

  @Test
  void shouldReturnNullNextIfNotConnected() {
    LinearTile tile = new LinearTile(1);
    assertNull(tile.next());
  }

  @Test
  void shouldReturnNextTileIfConnected() {
    LinearTile first = new LinearTile(1);
    LinearTile second = new LinearTile(2);
    first.next = second;
    assertEquals(second, first.next());
  }
}
