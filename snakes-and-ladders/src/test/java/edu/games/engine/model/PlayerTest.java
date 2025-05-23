package edu.games.engine.model;

import edu.games.engine.board.Tile;
import edu.games.engine.exception.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

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

  // --- Constructor Validations ---
  @Test
  void shouldCreatePlayerWithValidData() {
    assertEquals("Alice", player.getName());
    assertEquals(token, player.getToken());
    assertEquals(birthday, player.getBirthday());
    assertEquals(4, player.getPieces().size());
  }

  @Test
  void shouldThrowIfNameIsNullOrBlank() {
    assertThrows(ValidationException.class, () -> new Player(null, token, birthday));
    assertThrows(ValidationException.class, () -> new Player("   ", token, birthday));
  }

  @Test
  void shouldThrowIfTokenIsNull() {
    assertThrows(ValidationException.class, () -> new Player("Bob", null, birthday));
  }

  @Test
  void shouldThrowIfBirthdayIsNull() {
    assertThrows(ValidationException.class, () -> new Player("Bob", token, null));
  }

  // --- Piece Logic ---
  @Test
  void shouldGetSpecificPieceById() {
    PlayerPiece piece = player.getPiece(2);
    assertEquals(2, piece.getPlayerPieceId());
  }

  @Test
  void shouldThrowIfPieceIdOutOfRange() {
    Tile tile = mock(Tile.class);
    assertThrows(ValidationException.class, () -> player.movePiece(-1, tile));
    assertThrows(ValidationException.class, () -> player.movePiece(4, tile));
  }

  @Test
  void shouldMoveSpecificPieceToTile() {
    Tile tile = mock(Tile.class);
    player.movePiece(1, tile);
    assertEquals(tile, player.getPiece(1).getCurrentTile());
  }

  @Test
  void shouldMoveFirstBoardPieceOrFirstHomePiece() {
    Tile tile = mock(Tile.class);
    player.moveTo(tile);
    assertEquals(tile, player.getCurrentTile());
  }

  // --- Piece State Checks ---
  @Test
  void shouldDetectHomeAndBoardPiecesCorrectly() {
    assertTrue(player.hasHomePieces());
    assertFalse(player.hasPiecesOnBoard());

    Tile tile = mock(Tile.class);
    player.moveTo(tile);

    assertFalse(player.getHomePieces().isEmpty());
    assertFalse(player.getBoardPieces().isEmpty());

    List<PlayerPiece> boardPieces = player.getBoardPieces();
    List<PlayerPiece> homePieces = player.getHomePieces();

    assertEquals(1, boardPieces.size());
    assertEquals(3, homePieces.size());
  }

  @Test
  void getCurrentTileShouldReturnNullIfAllAtHome() {
    Player freshPlayer = new Player("Dana", Token.GREEN, LocalDate.of(1999, 5, 5));
    assertNull(freshPlayer.getCurrentTile());
  }

  // --- Equality & String ---
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
  void shouldNotBeEqualIfBirthdayDiffers() {
    Player p2 = new Player("Alice", Token.RED, LocalDate.of(1999, 1, 1));
    assertNotEquals(player, p2);
  }

  @Test
  void toStringShouldContainNameAndToken() {
    String s = player.toString();
    assertTrue(s.contains("Alice"));
    assertTrue(s.contains("RED"));
  }

  @Test
  void hashCodeShouldBeConsistentWithEquals() {
    Player same = new Player("Alice", token, birthday);
    assertEquals(player.hashCode(), same.hashCode());
  }
}
