package edu.games.engine.model;

import edu.games.engine.board.Tile;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PlayerPieceTest {

  @Test
  void shouldReturnCorrectId() {
    PlayerPiece piece = new PlayerPiece(2);
    assertEquals(2, piece.getPlayerPieceId());
  }

  @Test
  void shouldStartAtHome() {
    PlayerPiece piece = new PlayerPiece(0);
    assertNull(piece.getCurrentTile(), "Piece should start with null tile (home)");
    assertTrue(piece.isAtHome(), "Piece should report being at home");
    assertFalse(piece.isOnBoard(), "Piece should not report being on the board");
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

  @Test
  void shouldHandleMoveToNullTileGracefully() {
    PlayerPiece piece = new PlayerPiece(3);
    Tile mockTile = mock(Tile.class);
    piece.moveTo(mockTile);
    assertNotNull(piece.getCurrentTile());

    // Move back to home
    piece.moveTo(null);
    assertNull(piece.getCurrentTile(), "Piece should now be back at home");
    assertTrue(piece.isAtHome());
    assertFalse(piece.isOnBoard());
  }

  @Test
  void shouldSupportMultipleSequentialMoves() {
    PlayerPiece piece = new PlayerPiece(5);
    Tile tile1 = mock(Tile.class);
    Tile tile2 = mock(Tile.class);

    piece.moveTo(tile1);
    assertEquals(tile1, piece.getCurrentTile());

    piece.moveTo(tile2);
    assertEquals(tile2, piece.getCurrentTile(), "Tile should update to new position");

    piece.moveTo(null); // simulate being sent home
    assertNull(piece.getCurrentTile());
    assertTrue(piece.isAtHome());
  }
}
