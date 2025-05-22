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

/**
 * DefaultGame manages the lifecycle of a game session.
 * It handles turns, dice rolls, movement, rules, and win conditions.
 */
public final class DefaultGame implements Game {

  private static final Logger LOG = Logger.getLogger(DefaultGame.class.getName());

  private final Board board;
  private final GameStrategy strategy;
  private final Dice dice;
  private final List<Player> players;
  private int currentIndex = 0;
  private Player winner;

  /**
   * Constructs a new DefaultGame with the given components.
   *
   * @param board the game board
   * @param strategy the game rules and logic
   * @param initialPlayers the initial list of players
   * @param dice the dice mechanism
   */
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

  /**
   * Plays a turn for the current player.
   * Rolls the dice, attempts a move, applies special rules, and determines if the player gets another turn.
   *
   * @return the rolled value
   */
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
      LOG.fine(() -> currentPlayer.getName() + " attempting to move to tile " + destinationTile.tileId());
      Tile oldTile = currentPlayer.getCurrentTile();
      currentPlayer.moveTo(destinationTile);
      LOG.info(() -> currentPlayer.getName() + " moved from " +
          (oldTile != null ? oldTile.tileId() : "Start/Home") + " to tile " + destinationTile.tileId());
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

  /**
   * Returns the player whose turn it currently is.
   *
   * @return the current player
   */
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

  /**
   * Returns the winner of the game, if the game is finished.
   *
   * @return an Optional containing the winner, or empty if no one has won yet
   */
  @Override
  public Optional<Player> getWinner() {
    return Optional.ofNullable(winner);
  }

  /**
   * Returns the list of all players in the game.
   *
   * @return list of players
   */
  @Override
  public List<Player> getPlayers() {
    return players;
  }

  /**
   * Manually sets the current player index.
   *
   * @param idx index to set
   */
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

  /**
   * Manually sets or clears the winner.
   *
   * @param winner the winning player, or null to clear the winner
   */
  public void setWinner(Player winner) {
    Player oldWinner = this.winner;
    this.winner = winner;
    if (winner != null && winner != oldWinner) {
      LOG.info("Winner set to: " + winner.getName());
    } else if (winner == null && oldWinner != null) {
      LOG.info("Winner cleared (was: " + oldWinner.getName() + ").");
    }
  }

  /**
   * Returns the game strategy.
   *
   * @return the strategy used to control the game
   */
  public GameStrategy getStrategy() {
    return strategy;
  }

  /**
   * Returns the board used in this game.
   *
   * @return the game board
   */
  public Board board() {
    return board;
  }

  /**
   * Returns the dice used in this game.
   *
   * @return the dice instance
   */
  public Dice dice() {
    return dice;
  }
}
