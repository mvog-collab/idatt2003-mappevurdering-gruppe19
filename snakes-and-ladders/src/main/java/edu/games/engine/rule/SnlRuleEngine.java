package edu.games.engine.rule;

import edu.games.engine.board.LinearBoard;
import edu.games.engine.board.Tile;
import edu.games.engine.impl.DefaultGame;
import edu.games.engine.model.Player;
import edu.games.engine.model.PlayerPiece;
import edu.ntnu.idatt2003.utils.Log;
import java.util.List;
import java.util.Map;

public final class SnlRuleEngine implements RuleEngine {
  private final Map<Integer, Integer> snakes;
  private final Map<Integer, Integer> ladders;

  public SnlRuleEngine(Map<Integer, Integer> snakes, Map<Integer, Integer> ladders) {
    this.snakes = snakes;
    this.ladders = ladders;
  }

  @Override
  public boolean grantsExtraTurn(Player player, List<Integer> diceValues, DefaultGame game) {
    if (diceValues.size() != 2) return false;
    boolean isDouble = diceValues.get(0).equals(diceValues.get(1));
    int sum = diceValues.get(0) + diceValues.get(1);
    return isDouble && sum != 12; // No extra turn for double 6
  }

  @Override
  public void applyPostLandingEffects(
      Player player, PlayerPiece piece, Tile landedTile, DefaultGame game) {
    if (!isValidState(player, landedTile, game)) {
      return;
    }

    LinearBoard board = (LinearBoard) game.board();
    Tile destinationTile = applySnakesOrLadders(player, landedTile, board);
    applyBumping(player, destinationTile, board, game);
  }

  private boolean isValidState(Player player, Tile tile, DefaultGame game) {
    return player != null && tile != null && game != null && game.board() instanceof LinearBoard;
  }

  private Tile applySnakesOrLadders(Player player, Tile tile, LinearBoard board) {
    int pos = tile.id();
    Integer newPos = snakes.getOrDefault(pos, ladders.get(pos));

    if (newPos != null) {
      Tile newTile = board.tile(newPos);
      player.moveTo(newTile);
      Log.game()
          .info(
              () ->
                  player.getName()
                      + (newPos > pos ? " climbs a ladder" : " slides down a snake")
                      + " to tile "
                      + newPos);
      return newTile;
    }

    return tile;
  }

  private void applyBumping(Player currentPlayer, Tile tile, LinearBoard board, DefaultGame game) {
    if (tile.id() == board.start().id()) return;

    game.players().stream()
        .filter(
            p ->
                p != currentPlayer
                    && p.getCurrentTile() != null
                    && p.getCurrentTile().id() == tile.id())
        .forEach(
            other -> {
              other.moveTo(board.start());
              Log.game()
                  .info(
                      () ->
                          currentPlayer.getName()
                              + " bumps "
                              + other.getName()
                              + " back to start from tile "
                              + tile.id());
            });
  }

  @Override
  public boolean hasWon(Player player, DefaultGame game) {
    return player != null
        && player.getCurrentTile() != null
        && game != null
        && game.board() != null
        && game.board().isEnd(player.getCurrentTile());
  }
}
