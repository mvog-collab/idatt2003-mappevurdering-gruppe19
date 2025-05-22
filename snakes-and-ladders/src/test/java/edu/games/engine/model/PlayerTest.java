package edu.games.engine.model;

import edu.games.engine.board.Tile;
import edu.games.engine.exception.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PlayerTest {

  private Player player;
  private Token token;
  private LocalDate birthday;

  @BeforeEach
  void setUp() {
    token = Token.RED;
    birthday = LocalDate.of(2000, 1, 1);
    player = new Player("Alice", token, birthday);
  }

  @Test
  void shouldCreatePlayerWithValidData() {
    assertEquals("Alice", player.getName());
    assertEquals(token, player.getToken());
    assertEquals(birthday, player.getBirthday());
    assertEquals(4, player.getPieces().size());
  }

  @Test
  void shouldGetSpecificPieceById() {
    PlayerPiece piece = player.getPiece(2);
    assertEquals(2, piece.getId());
  }

  @Test
  void shouldMoveFirstPieceToTileAndGetCurrentTile() {
    Tile tile = mock(Tile.class);
    player.moveTo(tile);
    assertEquals(tile, player.getCurrentTile());
  }

  @Test
  void shouldMoveSpecificPieceToTile() {
    Tile tile = mock(Tile.class);
    player.movePiece(1, tile);
    assertEquals(tile, player.getPiece(1).getCurrentTile());
  }

  @Test
  void shouldThrowIfInvalidPieceId() {
    Tile tile = mock(Tile.class);
    assertThrows(ValidationException.class, () -> player.movePiece(99, tile));
  }

  @Test
  void shouldDetectHomeAndBoardPiecesCorrectly() {
    assertTrue(player.hasHomePieces());
    assertFalse(player.hasPiecesOnBoard());

    Tile tile = mock(Tile.class);
    player.moveTo(tile);

    assertFalse(player.getHomePieces().isEmpty());
    assertFalse(player.getBoardPieces().isEmpty());
  }

  @Test
  void shouldComparePlayersByNameAndBirthday() {
    Player p2 = new Player("Alice", Token.BLUE, birthday);
    assertEquals(player, p2);
  }

  @Test
  void shouldNotBeEqualIfNameDiffers() {
    Player p2 = new Player("Bob", Token.RED, birthday);
    assertNotEquals(player, p2);
  }

  @Test
  void toStringShouldContainNameAndToken() {
    String s = player.toString();
    assertTrue(s.contains("Alice"));
    assertTrue(s.contains("RED"));
  }
}
