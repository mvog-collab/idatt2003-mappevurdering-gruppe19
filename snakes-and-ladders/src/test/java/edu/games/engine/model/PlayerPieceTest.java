package edu.games.engine.model;

import edu.games.engine.board.Tile;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PlayerPieceTest {

  @Test
  void shouldReturnCorrectId() {
    PlayerPiece piece = new PlayerPiece(2);
    assertEquals(2, piece.getId());
  }

  @Test
  void shouldStartAtHome() {
    PlayerPiece piece = new PlayerPiece(0);
    assertNull(piece.getCurrentTile());
    assertTrue(piece.isAtHome());
    assertFalse(piece.isOnBoard());
  }

  @Test
  void shouldMoveToTileAndUpdateState() {
    PlayerPiece piece = new PlayerPiece(1);
    Tile mockTile = mock(Tile.class);

    piece.moveTo(mockTile);

    assertEquals(mockTile, piece.getCurrentTile());
    assertFalse(piece.isAtHome());
    assertTrue(piece.isOnBoard());
  }
}
