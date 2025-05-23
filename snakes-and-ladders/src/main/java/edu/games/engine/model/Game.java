package edu.games.engine.model;

import java.util.List;
import java.util.Optional;

/**
 * Represents the core interface for a board game.
 * Provides methods for managing player turns, tracking the current player,
 * checking for a winner, and accessing all players in the game.
 */
public interface Game {

  /**
   * Plays a full turn for the current player, including rolling dice,
   * moving, applying game logic, and determining win conditions.
   *
   * @return the total value rolled by the dice for this turn
   */
  int playTurn();

  /**
   * Returns the player whose turn it is currently.
   *
   * @return the active player
   */
  Player currentPlayer();

  /**
   * Returns the winner of the game if one has been determined.
   *
   * @return an {@code Optional} containing the winning player, or empty if no winner yet
   */
  Optional<Player> getWinner();

  /**
   * Returns a list of all players participating in the game.
   *
   * @return the list of players
   */
  List<Player> getPlayers();
}
