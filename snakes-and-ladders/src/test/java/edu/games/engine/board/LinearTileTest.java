package edu.games.engine.board;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LinearTileTest {

  @Test
  void shouldReturnCorrectTileId() {
    LinearTile tile = new LinearTile(7);
    assertEquals(7, tile.tileId(), "Tile ID should match the constructor value");
  }

  @Test
  void shouldReturnNullAsNextIfNotSet() {
    LinearTile tile = new LinearTile(1);
    assertNull(tile.next(), "Next should be null if no next tile is set");
  }

  @Test
  void shouldReturnNextTileIfConnected() {
    LinearTile first = new LinearTile(1);
    LinearTile second = new LinearTile(2);

    first.nextTile = second;

    assertEquals(second, first.next(), "Next should return the connected tile");
    assertEquals(2, first.next().tileId(), "Next tile ID should be correct");
  }

  @Test
  void nextTileShouldBeMutableWithinPackage() {
    LinearTile tile = new LinearTile(10);
    assertDoesNotThrow(() -> tile.nextTile = new LinearTile(11));
    assertEquals(11, tile.next().tileId());
  }
}
