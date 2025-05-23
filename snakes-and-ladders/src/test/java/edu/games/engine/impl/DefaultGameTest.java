package edu.games.engine.impl;

import edu.games.engine.board.Board;
import edu.games.engine.board.Tile;
import edu.games.engine.dice.Dice;
import edu.games.engine.exception.RuleViolationException;
import edu.games.engine.exception.ValidationException;
import edu.games.engine.model.Player;
import edu.games.engine.model.Token;
import edu.games.engine.strategy.GameStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DefaultGameTest {

  @Mock
  private Board mockBoard;

  @Mock
  private GameStrategy mockStrategy;

  @Mock
  private Dice mockDice;

  @Mock
  private Tile mockTile;

  private Player player1;
  private Player player2;
  private List<Player> players;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    player1 = createTestPlayer("Alice", Token.BLUE);
    player2 = createTestPlayer("Bob", Token.RED);
    players = new ArrayList<>(Arrays.asList(player1, player2));
  }

  @Nested
  class ConstructorTests {

    @Test
    void shouldCreateGameWithValidParameters() {
      DefaultGame game = new DefaultGame(mockBoard, mockStrategy, players, mockDice);

      assertNotNull(game);
      assertEquals(mockBoard, game.getBoard());
      assertEquals(mockStrategy, game.getStrategy());
      assertEquals(mockDice, game.getDice());
      assertEquals(players, game.getPlayers());
      assertEquals(player1, game.currentPlayer()); // First player should be current
    }

    @Test
    void shouldCreateGameWithEmptyPlayersList() {
      List<Player> emptyPlayers = new ArrayList<>();

      // Should not throw exception but log warning
      assertDoesNotThrow(() -> new DefaultGame(mockBoard, mockStrategy, emptyPlayers, mockDice));
    }

    @Test
    void shouldThrowExceptionWhenBoardIsNull() {
      assertThrows(NullPointerException.class, () -> new DefaultGame(null, mockStrategy, players, mockDice));
    }

    @Test
    void shouldThrowExceptionWhenStrategyIsNull() {
      assertThrows(NullPointerException.class, () -> new DefaultGame(mockBoard, null, players, mockDice));
    }

    @Test
    void shouldThrowExceptionWhenDiceIsNull() {
      assertThrows(NullPointerException.class, () -> new DefaultGame(mockBoard, mockStrategy, players, null));
    }

    @Test
    void shouldThrowExceptionWhenPlayersListIsNull() {
      assertThrows(NullPointerException.class, () -> new DefaultGame(mockBoard, mockStrategy, null, mockDice));
    }

    @Test
    void shouldCreateCopyOfPlayersList() {
      DefaultGame game = new DefaultGame(mockBoard, mockStrategy, players, mockDice);

      // Original list modification shouldn't affect game
      players.add(createTestPlayer("Charlie", Token.GREEN));

      assertEquals(2, game.getPlayers().size()); // Should still be 2
    }
  }

  @Nested
  class PlayTurnTests {

    private DefaultGame game;

    @BeforeEach
    void setUp() {
      game = new DefaultGame(mockBoard, mockStrategy, players, mockDice);
    }

    @Test
    void shouldExecuteNormalTurnSuccessfully() {
      when(mockDice.roll()).thenReturn(5);
      when(mockDice.lastValues()).thenReturn(List.of(5));
      when(mockStrategy.movePiece(player1, -1, 5, game)).thenReturn(mockTile);
      when(mockTile.tileId()).thenReturn(10);
      when(mockStrategy.processDiceRoll(player1, 5, game)).thenReturn(false);
      when(mockStrategy.checkWinCondition(player1, game)).thenReturn(false);

      int result = game.playTurn();

      assertEquals(5, result);
      assertEquals(player2, game.currentPlayer()); // Should advance to next player
      verify(mockStrategy).movePiece(player1, -1, 5, game);
      verify(mockStrategy).applySpecialRules(player1, null, mockTile, game);
      verify(mockStrategy).processDiceRoll(player1, 5, game);
      verify(mockStrategy).checkWinCondition(player1, game);
    }

    @Test
    void shouldHandlePlayerCannotMove() {
      when(mockDice.roll()).thenReturn(3);
      when(mockDice.lastValues()).thenReturn(List.of(3));
      when(mockStrategy.movePiece(player1, -1, 3, game)).thenReturn(null);
      when(mockStrategy.processDiceRoll(player1, 3, game)).thenReturn(false);
      when(mockStrategy.checkWinCondition(player1, game)).thenReturn(false);

      int result = game.playTurn();

      assertEquals(3, result);
      assertEquals(player2, game.currentPlayer());
      verify(mockStrategy, never()).applySpecialRules(any(), any(), any(), any());
    }

    @Test
    void shouldGrantExtraTurn() {
      when(mockDice.roll()).thenReturn(6);
      when(mockDice.lastValues()).thenReturn(List.of(6));
      when(mockStrategy.movePiece(player1, -1, 6, game)).thenReturn(mockTile);
      when(mockTile.tileId()).thenReturn(15);
      when(mockStrategy.processDiceRoll(player1, 6, game)).thenReturn(true);
      when(mockStrategy.checkWinCondition(player1, game)).thenReturn(false);

      game.playTurn();

      // Should still be player1's turn
      assertEquals(player1, game.currentPlayer());
    }

    @Test
    void shouldDeclareWinner() {
      when(mockDice.roll()).thenReturn(4);
      when(mockDice.lastValues()).thenReturn(List.of(4));
      when(mockStrategy.movePiece(player1, -1, 4, game)).thenReturn(mockTile);
      when(mockTile.tileId()).thenReturn(100);
      when(mockStrategy.processDiceRoll(player1, 4, game)).thenReturn(false);
      when(mockStrategy.checkWinCondition(player1, game)).thenReturn(true);

      game.playTurn();

      assertEquals(Optional.of(player1), game.getWinner());
    }

    @Test
    void shouldThrowRuleViolationExceptionWhenGameAlreadyFinished() {
      // Set a winner first
      game.setWinner(player1);

      assertThrows(RuleViolationException.class, () -> game.playTurn());
    }

    @Test
    void shouldThrowValidationExceptionWhenNoPlayers() {
      DefaultGame emptyGame = new DefaultGame(mockBoard, mockStrategy, new ArrayList<>(), mockDice);

      assertThrows(ValidationException.class, () -> emptyGame.playTurn());
    }

    @Test
    void shouldHandlePlayerMovingFromNonNullTile() {
      player1.moveTo(mockTile); // Set initial position
      when(mockTile.tileId()).thenReturn(5);

      Tile newTile = mock(Tile.class);
      when(newTile.tileId()).thenReturn(8);

      when(mockDice.roll()).thenReturn(3);
      when(mockDice.lastValues()).thenReturn(List.of(3));
      when(mockStrategy.movePiece(player1, -1, 3, game)).thenReturn(newTile);
      when(mockStrategy.processDiceRoll(player1, 3, game)).thenReturn(false);
      when(mockStrategy.checkWinCondition(player1, game)).thenReturn(false);

      game.playTurn();

      verify(mockStrategy).applySpecialRules(player1, null, newTile, game);
    }
  }

  @Nested
  class CurrentPlayerTests {

    @Test
    void shouldReturnCurrentPlayerWithValidIndex() {
      DefaultGame game = new DefaultGame(mockBoard, mockStrategy, players, mockDice);

      assertEquals(player1, game.currentPlayer());
    }

    @Test
    void shouldThrowExceptionWhenNoPlayers() {
      DefaultGame emptyGame = new DefaultGame(mockBoard, mockStrategy, new ArrayList<>(), mockDice);

      assertThrows(ValidationException.class, () -> emptyGame.currentPlayer());
    }

    @Test
    void shouldResetInvalidCurrentIndex() {
      DefaultGame game = new DefaultGame(mockBoard, mockStrategy, players, mockDice);

      // Manually set invalid index using reflection or by manipulating the state
      game.setCurrentPlayerIndex(10); // This should get modulo'd to valid index

      assertEquals(0, game.getPlayers().indexOf(game.currentPlayer()));
    }

    @Test
    void shouldHandleNegativeCurrentIndex() {
      DefaultGame game = new DefaultGame(mockBoard, mockStrategy, players, mockDice);

      game.setCurrentPlayerIndex(-1); // Should use Math.floorMod

      assertEquals(player2, game.currentPlayer()); // -1 mod 2 = 1
    }
  }

  @Nested
  class WinnerTests {

    private DefaultGame game;

    @BeforeEach
    void setUp() {
      game = new DefaultGame(mockBoard, mockStrategy, players, mockDice);
    }

    @Test
    void shouldReturnEmptyOptionalWhenNoWinner() {
      assertEquals(Optional.empty(), game.getWinner());
    }

    @Test
    void shouldReturnWinnerWhenSet() {
      game.setWinner(player1);

      assertEquals(Optional.of(player1), game.getWinner());
    }

    @Test
    void shouldSetWinnerFromNull() {
      game.setWinner(player1);

      assertEquals(player1, game.getWinner().orElse(null));
    }

    @Test
    void shouldChangeWinner() {
      game.setWinner(player1);
      game.setWinner(player2);

      assertEquals(Optional.of(player2), game.getWinner());
    }

    @Test
    void shouldClearWinner() {
      game.setWinner(player1);
      game.setWinner(null);

      assertEquals(Optional.empty(), game.getWinner());
    }

    @Test
    void shouldHandleSettingSameWinner() {
      game.setWinner(player1);
      game.setWinner(player1); // Same winner

      assertEquals(Optional.of(player1), game.getWinner());
    }
  }

  @Nested
  class PlayerIndexTests {

    private DefaultGame game;

    @BeforeEach
    void setUp() {
      game = new DefaultGame(mockBoard, mockStrategy, players, mockDice);
    }

    @Test
    void shouldSetValidCurrentPlayerIndex() {
      game.setCurrentPlayerIndex(1);

      assertEquals(player2, game.currentPlayer());
    }

    @Test
    void shouldHandleIndexLargerThanPlayerCount() {
      game.setCurrentPlayerIndex(5); // 5 % 2 = 1

      assertEquals(player2, game.currentPlayer());
    }

    @Test
    void shouldHandleNegativeIndex() {
      game.setCurrentPlayerIndex(-3); // Math.floorMod(-3, 2) = 1

      assertEquals(player2, game.currentPlayer());
    }

    @Test
    void shouldHandleZeroIndex() {
      game.setCurrentPlayerIndex(1); // Change from default
      game.setCurrentPlayerIndex(0); // Back to first

      assertEquals(player1, game.currentPlayer());
    }

    @Test
    void shouldThrowExceptionWhenNoPlayersForIndexSetting() {
      DefaultGame emptyGame = new DefaultGame(mockBoard, mockStrategy, new ArrayList<>(), mockDice);

      assertThrows(ValidationException.class, () -> emptyGame.setCurrentPlayerIndex(0));
    }

    @Test
    void shouldHandleSettingSameIndex() {
      game.setCurrentPlayerIndex(0); // Same as current

      assertEquals(player1, game.currentPlayer());
    }
  }

  @Nested
  class GetterTests {

    private DefaultGame game;

    @BeforeEach
    void setUp() {
      game = new DefaultGame(mockBoard, mockStrategy, players, mockDice);
    }

    @Test
    void shouldReturnBoard() {
      assertEquals(mockBoard, game.getBoard());
    }

    @Test
    void shouldReturnStrategy() {
      assertEquals(mockStrategy, game.getStrategy());
    }

    @Test
    void shouldReturnDice() {
      assertEquals(mockDice, game.getDice());
    }

    @Test
    void shouldReturnPlayersList() {
      assertEquals(players, game.getPlayers());
    }

    @Test
    void shouldReturnSamePlayersListInstance() {
      List<Player> returnedPlayers = game.getPlayers();

      assertSame(returnedPlayers, game.getPlayers());
    }
  }

  @Nested
  class EdgeCasesAndIntegration {

    @Test
    void shouldHandleCompleteGameFlow() {
      DefaultGame game = new DefaultGame(mockBoard, mockStrategy, players, mockDice);

      // Turn 1: Player 1 moves, no extra turn
      when(mockDice.roll()).thenReturn(4);
      when(mockDice.lastValues()).thenReturn(List.of(4));
      when(mockStrategy.movePiece(player1, -1, 4, game)).thenReturn(mockTile);
      when(mockTile.tileId()).thenReturn(4);
      when(mockStrategy.processDiceRoll(player1, 4, game)).thenReturn(false);
      when(mockStrategy.checkWinCondition(player1, game)).thenReturn(false);

      game.playTurn();
      assertEquals(player2, game.currentPlayer());

      // Turn 2: Player 2 moves, gets extra turn
      when(mockDice.roll()).thenReturn(6);
      when(mockDice.lastValues()).thenReturn(List.of(6));
      when(mockStrategy.movePiece(player2, -1, 6, game)).thenReturn(mockTile);
      when(mockStrategy.processDiceRoll(player2, 6, game)).thenReturn(true);
      when(mockStrategy.checkWinCondition(player2, game)).thenReturn(false);

      game.playTurn();
      assertEquals(player2, game.currentPlayer()); // Still player 2's turn

      // Turn 3: Player 2 wins
      when(mockDice.roll()).thenReturn(3);
      when(mockDice.lastValues()).thenReturn(List.of(3));
      when(mockStrategy.movePiece(player2, -1, 3, game)).thenReturn(mockTile);
      when(mockStrategy.processDiceRoll(player2, 3, game)).thenReturn(false);
      when(mockStrategy.checkWinCondition(player2, game)).thenReturn(true);

      game.playTurn();
      assertEquals(Optional.of(player2), game.getWinner());
    }

    @Test
    void shouldHandleSinglePlayerGame() {
      List<Player> singlePlayer = List.of(player1);
      DefaultGame game = new DefaultGame(mockBoard, mockStrategy, singlePlayer, mockDice);

      when(mockDice.roll()).thenReturn(2);
      when(mockDice.lastValues()).thenReturn(List.of(2));
      when(mockStrategy.movePiece(player1, -1, 2, game)).thenReturn(mockTile);
      when(mockTile.tileId()).thenReturn(2);
      when(mockStrategy.processDiceRoll(player1, 2, game)).thenReturn(false);
      when(mockStrategy.checkWinCondition(player1, game)).thenReturn(false);

      game.playTurn();

      // Should cycle back to same player
      assertEquals(player1, game.currentPlayer());
    }

    @Test
    void shouldHandlePlayerIndexWrapAround() {
      DefaultGame game = new DefaultGame(mockBoard, mockStrategy, players, mockDice);

      // Advance past last player
      game.setCurrentPlayerIndex(1); // Player 2
      assertEquals(player2, game.currentPlayer());

      // Simulate turn that advances to next player (wraps to 0)
      when(mockDice.roll()).thenReturn(1);
      when(mockDice.lastValues()).thenReturn(List.of(1));
      when(mockStrategy.movePiece(player2, -1, 1, game)).thenReturn(null);
      when(mockStrategy.processDiceRoll(player2, 1, game)).thenReturn(false);
      when(mockStrategy.checkWinCondition(player2, game)).thenReturn(false);

      game.playTurn();

      assertEquals(player1, game.currentPlayer()); // Wrapped around
    }
  }

  private Player createTestPlayer(String name, Token token) {
    return new Player(name, token, LocalDate.of(1990, 1, 1));
  }
}