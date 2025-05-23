package edu.games.engine.strategy;

import edu.games.engine.board.Tile;
import edu.games.engine.impl.DefaultGame;
import edu.games.engine.model.Player;
import edu.games.engine.model.PlayerPiece;
import edu.games.engine.rule.RuleEngine;

import java.util.Map;

/**
 * A strategy implementation for the game Snakes and Ladders.
 * <p>
 * Handles basic movement, snakes/ladders effects, and win condition checks
 * by delegating logic to the provided {@link RuleEngine}.
 */
public class SnlGameStrategy implements GameStrategy {

  private final RuleEngine ruleEngine;
  private final Map<Integer, Integer> snakes;
  private final Map<Integer, Integer> ladders;

  /**
   * Constructs a Snakes and Ladders strategy using a rule engine and the game's
   * snakes/ladders map.
   *
   * @param ruleEngine the rule engine used to apply game-specific logic
   * @param snakes     map of tile IDs where snakes start and end
   * @param ladders    map of tile IDs where ladders start and end
   */
  public SnlGameStrategy(
      RuleEngine ruleEngine, Map<Integer, Integer> snakes, Map<Integer, Integer> ladders) {
    this.ruleEngine = ruleEngine;
    this.snakes = snakes;
    this.ladders = ladders;
  }

  /**
   * Places all players at the starting tile of the board.
   *
   * @param game the game to initialize
   */
  @Override
  public void initializeGame(DefaultGame game) {
    if (game != null && game.getBoard() != null) {
      for (Player player : game.getPlayers()) {
        player.moveTo(game.getBoard().start());
      }
    }
  }

  /**
   * Checks if the player should get an extra turn based on dice values.
   *
   * @param player    the player who rolled
   * @param diceValue the total dice value (unused directly)
   * @param game      the current game
   * @return true if the player should take another turn, false otherwise
   */
  @Override
  public boolean processDiceRoll(Player player, int diceValue, DefaultGame game) {
    return ruleEngine.grantsExtraTurn(player, game.getDice().lastValues(), game);
  }

  /**
   * Calculates the new tile a player should move to based on dice roll.
   * Handles a special case where rolling 12 results in no movement.
   *
   * @param player            the player making a move
   * @param pieceIndexIgnored unused (only one piece per player)
   * @param diceValue         the value of the dice roll
   * @param game              the game instance
   * @return the destination tile, or null if movement is not allowed
   */
  @Override
  public Tile movePiece(Player player, int pieceIndexIgnored, int diceValue, DefaultGame game) {
    if (player == null || game == null || player.getCurrentTile() == null || game.getBoard() == null) {
      return null;
    }
    if (diceValue == 12) {
      return game.getBoard().move(player.getCurrentTile(), 0);
    }
    return game.getBoard().move(player.getCurrentTile(), diceValue);
  }

  /**
   * Checks if the given player has reached the winning tile.
   *
   * @param player the player to check
   * @param game   the current game
   * @return true if the player has won, false otherwise
   */
  @Override
  public boolean checkWinCondition(Player player, DefaultGame game) {
    return ruleEngine.hasWon(player, game);
  }

  /**
   * Applies post-move effects like falling down snakes or climbing ladders.
   *
   * @param player          the player who moved
   * @param pieceIgnored    unused
   * @param destinationTile the tile landed on
   * @param game            the current game
   */
  @Override
  public void applySpecialRules(
      Player player, PlayerPiece pieceIgnored, Tile destinationTile, DefaultGame game) {
    ruleEngine.applyPostLandingEffects(player, null, destinationTile, game);
  }

  /**
   * Returns an unmodifiable view of the snakes map.
   *
   * @return map of snake positions
   */
  public Map<Integer, Integer> getSnakes() {
    return Map.copyOf(snakes);
  }

  /**
   * Returns an unmodifiable view of the ladders map.
   *
   * @return map of ladder positions
   */
  public Map<Integer, Integer> getLadders() {
    return Map.copyOf(ladders);
  }
}
