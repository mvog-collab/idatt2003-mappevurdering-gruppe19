package edu.games.engine.strategy;

import edu.games.engine.board.LudoBoard;
import edu.games.engine.board.Tile;
import edu.games.engine.exception.ValidationException;
import edu.games.engine.impl.DefaultGame;
import edu.games.engine.model.LudoColor;
import edu.games.engine.model.Player;
import edu.games.engine.model.PlayerPiece;
import edu.games.engine.rule.RuleEngine;

/**
 * A game strategy for the classic game Ludo.
 * <p>
 * Handles piece movement, rule enforcement, and winning conditions
 * using a {@link RuleEngine} for additional game logic.
 */
public class LudoGameStrategy implements GameStrategy {

  private final RuleEngine ruleEngine;

  /**
   * Constructs a strategy using the provided rule engine.
   *
   * @param ruleEngine the engine responsible for applying Ludo rules
   */
  public LudoGameStrategy(RuleEngine ruleEngine) {
    this.ruleEngine = ruleEngine;
  }

  /**
   * Resets all player pieces to "home" (null tile) at game start.
   *
   * @param game the game to initialize
   */
  @Override
  public void initializeGame(DefaultGame game) {
    game.getPlayers().forEach(player -> player.getPieces().forEach(p -> p.moveTo(null)));
  }

  /**
   * Determines whether the player should get an extra turn based on dice roll.
   *
   * @param player the player who rolled
   * @param diceValue the total dice value (not used here)
   * @param game the current game
   * @return true if the player gets an extra turn, false otherwise
   */
  @Override
  public boolean processDiceRoll(Player player, int diceValue, DefaultGame game) {
    return ruleEngine.grantsExtraTurn(player, game.getDice().lastValues(), game);
  }

  /**
   * Moves the selected piece according to the dice value and Ludo rules.
   *
   * @param player the current player
   * @param pieceIndex the index of the piece to move
   * @param diceValue the rolled dice value
   * @param game the current game instance
   * @return the tile the piece should move to, or null if it cannot move
   */
  @Override
  public Tile movePiece(Player player, int pieceIndex, int diceValue, DefaultGame game) {
    if (player == null
        || game == null
        || !(game.getBoard() instanceof LudoBoard)
        || pieceIndex < 0
        || pieceIndex >= player.getPieces().size()) {
      throw new ValidationException("pieceIndex out of range: " + pieceIndex);
    }

    PlayerPiece piece = player.getPiece(pieceIndex);
    LudoBoard board = (LudoBoard) game.getBoard();
    LudoColor color = LudoColor.valueOf(player.getToken().name());

    if (piece.isAtHome()) {
      return (diceValue == 6) ? board.getStartTile(color) : null;
    } else {
      return board.move(piece.getCurrentTile(), diceValue, color);
    }
  }

  /**
   * Checks if the given player has met the win condition.
   *
   * @param player the player to check
   * @param game the game instance
   * @return true if the player has won, false otherwise
   */
  @Override
  public boolean checkWinCondition(Player player, DefaultGame game) {
    return ruleEngine.hasWon(player, game);
  }

  /**
   * Applies any special effects after a piece has landed,
   * such as bumping opponents.
   *
   * @param player the player who moved
   * @param piece the moved piece
   * @param destinationTile the tile the piece landed on
   * @param game the game context
   */
  @Override
  public void applySpecialRules(
      Player player, PlayerPiece piece, Tile destinationTile, DefaultGame game) {
    ruleEngine.applyPostLandingEffects(player, piece, destinationTile, game);
  }
}
