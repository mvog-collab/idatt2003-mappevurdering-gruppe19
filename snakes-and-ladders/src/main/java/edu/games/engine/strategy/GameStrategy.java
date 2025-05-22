package edu.games.engine.strategy;

import edu.games.engine.board.Tile;
import edu.games.engine.impl.DefaultGame;
import edu.games.engine.model.Player;
import edu.games.engine.model.PlayerPiece;

/**
 * Defines a strategy for playing a board game.
 * <p>
 * Implementations handle movement logic, rule enforcement,
 * win conditions, and any special effects triggered during gameplay.
 */
public interface GameStrategy {

  /**
   * Initializes game-specific setup logic when the game starts.
   *
   * @param game the game instance to be initialized
   */
  void initializeGame(DefaultGame game);

  /**
   * Processes logic after a dice roll, such as determining whether
   * the player receives an extra turn.
   *
   * @param player the player who rolled
   * @param diceValue the result of the dice roll
   * @param game the game context
   * @return true if the player gets another turn, false otherwise
   */
  boolean processDiceRoll(Player player, int diceValue, DefaultGame game);

  /**
   * Determines how a player's piece should move based on the dice roll.
   *
   * @param player the player taking the turn
   * @param pieceIndex the index of the piece to move (use -1 for auto-selection)
   * @param diceValue the dice roll value
   * @param game the game context
   * @return the destination tile the piece will move to, or null if it can't move
   */
  Tile movePiece(Player player, int pieceIndex, int diceValue, DefaultGame game);

  /**
   * Checks if the player has satisfied the winning condition.
   *
   * @param player the player to check
   * @param game the current game
   * @return true if the player has won, false otherwise
   */
  boolean checkWinCondition(Player player, DefaultGame game);

  /**
   * Applies any special rules triggered after a piece has moved,
   * such as bumping, climbing ladders, or entering a goal path.
   *
   * @param player the player who moved
   * @param piece the piece that moved
   * @param destinationTile the tile it landed on
   * @param game the game context
   */
  void applySpecialRules(Player player, PlayerPiece piece, Tile destinationTile, DefaultGame game);
}
