package edu.games.engine.impl;

import edu.games.engine.exception.ValidationException;
import edu.games.engine.exception.RuleViolationException;
import edu.games.engine.board.Board;
import edu.games.engine.board.Tile;
import edu.games.engine.dice.Dice;
import edu.games.engine.model.Game;
import edu.games.engine.model.Player;
import edu.games.engine.strategy.GameStrategy;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public final class DefaultGame implements Game {

  private final Board board;
  private final GameStrategy strategy;
  private final Dice dice;
  private final List<Player> players;
  private int currentIndex = 0;
  private Player winner;

  public DefaultGame(Board board, GameStrategy strategy, List<Player> players, Dice dice) {
    this.board = Objects.requireNonNull(board);
    this.strategy = Objects.requireNonNull(strategy);
    this.dice = Objects.requireNonNull(dice);
    this.players = new ArrayList<>(players);
    if (players == null || players.isEmpty()) {
      // throw new ValidationException("Player list is null or empty");
    }
  }

  // In DefaultGame
  public int playTurn() {
    if (winner != null) {
      throw new RuleViolationException("Game already finished - winner is " + winner.getName());
    }

    Player currentPlayer = currentPlayer();
    int rolledValue = dice.roll();

    // Let strategy determine if player can move and where
    Tile destinationTile = strategy.movePiece(currentPlayer, -1, rolledValue, this);
    if (destinationTile != null) {
      currentPlayer.moveTo(destinationTile);
      strategy.applySpecialRules(currentPlayer, null, destinationTile, this);
    }

    // Let strategy determine if player gets extra turn
    boolean extraTurn = strategy.processDiceRoll(currentPlayer, rolledValue, this);
    if (!extraTurn) {
      currentIndex = (currentIndex + 1) % players.size();
    }

    // Let strategy determine win condition
    if (strategy.checkWinCondition(currentPlayer, this)) {
      winner = currentPlayer;
    }

    return rolledValue;
  }

  @Override
  public Player currentPlayer() {
    if (players.isEmpty()) {
      throw new ValidationException("No players added to the game");
    }
    return players.get(currentIndex);
  }

  @Override
  public Optional<Player> getWinner() {
    return Optional.ofNullable(winner);
  }

  @Override
  public List<Player> getPlayers() {
    return players;
  }

  public void setCurrentPlayerIndex(int idx) {

    if (players.isEmpty()) {
      throw new ValidationException("No players - cannot set the current player index");
    }
    currentIndex = Math.floorMod(idx, players.size());
  }

  public void setWinner(Player winner) {
    this.winner = winner;
  }

  public GameStrategy getStrategy() {
    return strategy;
  }

  public Board board() {
    return board;
  }

  public Dice dice() {
    return dice;
  }
}
