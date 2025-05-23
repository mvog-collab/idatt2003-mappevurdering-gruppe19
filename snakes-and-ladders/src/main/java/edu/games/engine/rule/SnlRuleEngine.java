package edu.games.engine.rule;

import edu.games.engine.board.LinearBoard;
import edu.games.engine.board.Tile;
import edu.games.engine.impl.DefaultGame;
import edu.games.engine.model.Player;
import edu.games.engine.model.PlayerPiece;
import edu.ntnu.idatt2003.utils.Log;
import java.util.List;
import java.util.Map;

/**
 * Rule engine for Snakes and Ladders.
 * Handles extra turn logic, tile effects like snakes and ladders,
 * bumping opponents, and win conditions.
 */
public final class SnlRuleEngine implements RuleEngine {
  private final Map<Integer, Integer> snakes;
  private final Map<Integer, Integer> ladders;

  /**
   * Constructs a new rule engine with given snake and ladder mappings.
   *
   * @param snakes  map of snake head positions to their tails
   * @param ladders map of ladder base positions to their tops
   */
  public SnlRuleEngine(Map<Integer, Integer> snakes, Map<Integer, Integer> ladders) {
    this.snakes = snakes;
    this.ladders = ladders;
  }

  /**
   * Grants an extra turn if two dice show the same number,
   * unless the total is 12 (double sixes).
   *
   * @param player      the player who rolled
   * @param diceValues  list of two dice values
   * @param game        the game context
   * @return true if player should get an extra turn
   */
  @Override
  public boolean grantsExtraTurn(Player player, List<Integer> diceValues, DefaultGame game) {
    if (diceValues.size() != 2) return false;
    boolean isDouble = diceValues.get(0).equals(diceValues.get(1));
    int sum = diceValues.get(0) + diceValues.get(1);
    return isDouble && sum != 12;
  }

  /**
   * Applies post-move effects like climbing ladders, sliding down snakes,
   * and bumping other players on the same tile.
   *
   * @param player      the player who moved
   * @param piece       the piece that moved
   * @param landedTile  the tile the piece landed on
   * @param game        the game context
   */
  @Override
  public void applyPostLandingEffects(
      Player player, PlayerPiece piece, Tile landedTile, DefaultGame game) {
    if (!isValidState(player, landedTile, game)) {
      return;
    }

    LinearBoard board = (LinearBoard) game.getBoard();
    Tile destinationTile = applySnakesOrLadders(player, landedTile, board);
    applyBumping(player, destinationTile, board, game);
  }

  private boolean isValidState(Player player, Tile tile, DefaultGame game) {
    return player != null && tile != null && game != null && game.getBoard() instanceof LinearBoard;
  }

  private Tile applySnakesOrLadders(Player player, Tile tile, LinearBoard board) {
    int pos = tile.tileId();
    Integer newPos = snakes.getOrDefault(pos, ladders.get(pos));

    if (newPos != null) {
      Tile newTile = board.tile(newPos);
      player.moveTo(newTile);
      Log.game()
          .info(() -> player.getName()
              + (newPos > pos ? " climbs a ladder" : " slides down a snake")
              + " to tile " + newPos);
      return newTile;
    }

    return tile;
  }

  private void applyBumping(Player currentPlayer, Tile tile, LinearBoard board, DefaultGame game) {
    if (tile.tileId() == board.start().tileId()) return;

    game.getPlayers().stream()
        .filter(p ->
            p != currentPlayer &&
                p.getCurrentTile() != null &&
                p.getCurrentTile().tileId() == tile.tileId())
        .forEach(other -> {
          other.moveTo(board.start());
          Log.game()
              .info(() -> currentPlayer.getName()
                  + " bumps " + other.getName()
                  + " back to start from tile " + tile.tileId());
        });
  }

  /**
   * Determines if the player has won by reaching the final tile.
   *
   * @param player the player to check
   * @param game   the game context
   * @return true if the player is on the final tile
   */
  @Override
  public boolean hasWon(Player player, DefaultGame game) {
    return player != null
        && player.getCurrentTile() != null
        && game != null
        && game.getBoard() != null
        && game.getBoard().isEnd(player.getCurrentTile());
  }
}
