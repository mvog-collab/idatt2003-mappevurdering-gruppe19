package edu.games.engine.impl;

import edu.games.engine.board.Board;
import edu.games.engine.board.Tile;
import edu.games.engine.dice.Dice;
import edu.games.engine.model.Player;
import edu.games.engine.model.Token;
import edu.games.engine.rule.RuleEngine;
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
  private RuleEngine rules;
  private GameStrategy strategy;
  private DefaultGame game;

  @BeforeEach
  void setUp() {
    player = new Player("Alice", Token.BLUE, LocalDate.of(2000, 1, 1));
    tile = mock(Tile.class);
    dice = mock(Dice.class);
    board = mock(Board.class);
    rules = mock(RuleEngine.class);
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
    // Merk: vi kan ikke bruke verify(player).moveTo(tile) siden Player er ekte
    assertEquals(tile, player.getCurrentTile());
    // verify på strategy
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
}
