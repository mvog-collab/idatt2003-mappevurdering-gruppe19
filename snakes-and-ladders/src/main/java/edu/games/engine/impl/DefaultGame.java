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
import java.util.logging.Level;
import java.util.logging.Logger;

public final class DefaultGame implements Game {

  private static final Logger LOG = Logger.getLogger(DefaultGame.class.getName());

  private final Board board;
  private final GameStrategy strategy;
  private final Dice dice;
  private final List<Player> players;
  private int currentIndex = 0;
  private Player winner;

  public DefaultGame(Board board, GameStrategy strategy, List<Player> initialPlayers, Dice dice) {
    this.board = Objects.requireNonNull(board, "Board cannot be null.");
    this.strategy = Objects.requireNonNull(strategy, "GameStrategy cannot be null.");
    this.dice = Objects.requireNonNull(dice, "Dice cannot be null.");

    this.players = new ArrayList<>(Objects.requireNonNull(initialPlayers, "Initial players list cannot be null."));

    LOG.info(() -> "DefaultGame instance created. Board: " + board.getClass().getSimpleName() +
        ", Strategy: " + strategy.getClass().getSimpleName() +
        ", Dice: " + dice.getClass().getSimpleName() +
        ", Initial Players: " + this.players.size());

    if (this.players.isEmpty()) {
      LOG.warning(
          "Game initialized with an empty player list. Players may need to be added before playTurn() is called.");
    }
  }

  @Override
  public int playTurn() {
    LOG.fine("playTurn called.");
    if (winner != null) {
      String msg = "Game already finished - winner is " + winner.getName();
      LOG.warning(msg);
      throw new RuleViolationException(msg);
    }
    if (players.isEmpty()) {
      String msg = "Cannot play turn: No players in the game.";
      LOG.severe(msg);
      throw new ValidationException(msg);
    }

    Player currentPlayer = currentPlayer();
    LOG.info(
        () -> "Player " + currentPlayer.getName() + " (Token: " + currentPlayer.getToken() + ") is taking a turn.");

    int rolledValue = dice.roll();
    LOG.info(() -> currentPlayer.getName() + " rolled " + dice.lastValues() + " (sum: " + rolledValue + ")");

    Tile destinationTile = strategy.movePiece(currentPlayer, -1, rolledValue, this);
    if (destinationTile != null) {
      LOG.fine(() -> currentPlayer.getName() + " attempting to move to tile " + destinationTile.id());
      Tile oldTile = currentPlayer.getCurrentTile();
      currentPlayer.moveTo(destinationTile);
      LOG.info(() -> currentPlayer.getName() + " moved from " +
          (oldTile != null ? oldTile.id() : "Start/Home") + " to tile " + destinationTile.id());
      strategy.applySpecialRules(currentPlayer, null, destinationTile, this);
    } else {
      LOG.info(() -> currentPlayer.getName() + " could not move with roll " + rolledValue + " (destination was null).");
    }

    boolean extraTurn = strategy.processDiceRoll(currentPlayer, rolledValue, this);
    if (!extraTurn) {
      currentIndex = (currentIndex + 1) % players.size();
      LOG.info(() -> "Turn passed. Next player: " + currentPlayer().getName() + " (Index: " + currentIndex + ")");
    } else {
      LOG.info(() -> currentPlayer.getName() + " gets an extra turn.");
    }

    if (strategy.checkWinCondition(currentPlayer, this)) {
      winner = currentPlayer;
      LOG.info("WINNER DECLARED: " + winner.getName());
    }

    return rolledValue;
  }

  @Override
  public Player currentPlayer() {
    if (players.isEmpty()) {
      LOG.severe("currentPlayer() called but no players have been added to the game.");
      throw new ValidationException("No players added to the game");
    }
    if (currentIndex < 0 || currentIndex >= players.size()) {
      LOG.severe(
          "Invalid currentIndex " + currentIndex + " for player list size " + players.size() + ". Resetting to 0.");
      currentIndex = 0;
      if (players.isEmpty()) {
        throw new ValidationException("No players added (double check after index reset).");
      }
    }
    return players.get(currentIndex);
  }

  @Override
  public Optional<Player> winner() {
    return Optional.ofNullable(winner);
  }

  @Override
  public List<Player> players() {
    return players;
  }

  public void setCurrentPlayerIndex(int idx) {
    if (players.isEmpty()) {
      LOG.severe("Cannot set current player index: No players in the game.");
      throw new ValidationException("No players - cannot set the current player index");
    }
    int oldIndex = this.currentIndex;
    this.currentIndex = Math.floorMod(idx, players.size());
    if (oldIndex != this.currentIndex) {
      LOG.fine(() -> "Current player index changed from " + oldIndex + " to: " + this.currentIndex +
          " (Player: " + (players.get(this.currentIndex) != null ? players.get(this.currentIndex).getName() : "N/A")
          + ")");
    }
  }

  public void setWinner(Player winner) {
    Player oldWinner = this.winner;
    this.winner = winner;
    if (winner != null && winner != oldWinner) {
      LOG.info("Winner set to: " + winner.getName());
    } else if (winner == null && oldWinner != null) {
      LOG.info("Winner cleared (was: " + oldWinner.getName() + ").");
    }
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