package edu.ntnu.idatt2003.gateway;

import edu.ntnu.idatt2003.persistence.BoardAdapter;

/**
 * Defines setup operations for initializing and resetting a board game session.
 * <p>
 * Extends {@link GameGateway} to allow observers to be notified of lifecycle events.
 */
public interface GameSetup extends GameGateway {

  /**
   * Starts a new game with a fixed board size.
   * <p>
   * This method should initialize game state, clear any existing players or
   * turns, and notify observers of the GAME_STARTED event.
   *
   * @param boardSize the size of the board to create (ignored by some games)
   */
  void newGame(int boardSize);

  /**
   * Starts a new game using custom board configuration data.
   * <p>
   * Typically used for games that support loading snakes, ladders,
   * or other dynamic board layouts from {@link BoardAdapter.MapData}.
   *
   * @param data the map data containing board configuration
   */
  void newGame(BoardAdapter.MapData data);

  /**
   * Resets the current game to its initial state.
   * <p>
   * All players’ positions should be cleared and turn order reset.
   * Observers should receive a GAME_RESET event.
   */
  void resetGame();
}
