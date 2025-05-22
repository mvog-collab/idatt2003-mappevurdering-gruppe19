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
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DefaultGameTest {

  private Player player;
  private Tile tile;
  private Dice dice;
  private Board board;
  private GameStrategy strategy;
  private DefaultGame game;

  @BeforeEach
  void setUp() {
    player = new Player("Alice", Token.BLUE, LocalDate.of(2000, 1, 1));
    tile = mock(Tile.class);
    dice = mock(Dice.class);
    board = mock(Board.class);
    strategy = mock(GameStrategy.class);

    game = new DefaultGame(board, strategy, List.of(player), dice);
  }

  @Test
  void shouldReturnCurrentPlayer() {
    assertEquals(player, game.currentPlayer());
  }

  @Test
  void shouldRollDiceAndMovePlayer() {
    when(dice.roll()).thenReturn(4);
    when(strategy.movePiece(player, -1, 4, game)).thenReturn(tile);
    when(strategy.processDiceRoll(player, 4, game)).thenReturn(false);
    when(strategy.checkWinCondition(player, game)).thenReturn(false);

    int rolled = game.playTurn();

    assertEquals(4, rolled);
    assertEquals(tile, player.getCurrentTile());
    verify(strategy).applySpecialRules(eq(player), isNull(), eq(tile), eq(game));
  }

  @Test
  void shouldHandleExtraTurn() {
    when(dice.roll()).thenReturn(6);
    when(strategy.movePiece(player, -1, 6, game)).thenReturn(tile);
    when(strategy.processDiceRoll(player, 6, game)).thenReturn(true);
    when(strategy.checkWinCondition(player, game)).thenReturn(false);

    int rolled = game.playTurn();

    assertEquals(6, rolled);
    assertEquals(player, game.currentPlayer()); // samme spiller pga ekstra kast
  }

  @Test
  void shouldDeclareGetWinner() {
    when(dice.roll()).thenReturn(3);
    when(strategy.movePiece(player, -1, 3, game)).thenReturn(tile);
    when(strategy.processDiceRoll(player, 3, game)).thenReturn(false);
    when(strategy.checkWinCondition(player, game)).thenReturn(true);

    game.playTurn();

    assertEquals(Optional.of(player), game.getWinner());
  }

  @Test
  void shouldSetCurrentPlayerIndex() {
    game.setCurrentPlayerIndex(0);
    assertEquals(player, game.currentPlayer());
  }


  @Test
  void shouldThrowWhenNoPlayersInCurrentPlayer() {
    DefaultGame emptyGame = new DefaultGame(board, strategy, List.of(), dice);
    assertThrows(ValidationException.class, emptyGame::currentPlayer);
  }

  @Test
  void shouldThrowWhenSettingIndexWithNoPlayers() {
    DefaultGame emptyGame = new DefaultGame(board, strategy, List.of(), dice);
    assertThrows(ValidationException.class, () -> emptyGame.setCurrentPlayerIndex(1));
  }

  @Test
  void shouldThrowIfPlayTurnAfterGameIsOver() {
    when(dice.roll()).thenReturn(3);
    when(strategy.movePiece(player, -1, 3, game)).thenReturn(tile);
    when(strategy.processDiceRoll(player, 3, game)).thenReturn(false);
    when(strategy.checkWinCondition(player, game)).thenReturn(true);

    game.playTurn(); // spiller vinner

    assertThrows(RuleViolationException.class, game::playTurn);
  }

  @Test
  void shouldThrowIfPlayersListIsNull() {
    assertThrows(NullPointerException.class, () -> new DefaultGame(board, strategy, null, dice));
  }
}
