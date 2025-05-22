package edu.games.engine.board;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LinearTileTest {

  @Test
  void shouldReturnCorrectTileId() {
    LinearTile tile = new LinearTile(7);
    assertEquals(7, tile.tileId());
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
    first.nextTile = second;
    assertEquals(second, first.next());
  }
}
