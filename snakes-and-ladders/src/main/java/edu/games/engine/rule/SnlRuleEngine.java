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
    if (player == null
        || landedTile == null
        || game == null
        || !(game.board() instanceof LinearBoard)) {
      return;
    }
    LinearBoard board = (LinearBoard) game.board();
    int currentPosition = landedTile.id();

    // Apply Snakes or Ladders
    Integer N_P =
        snakes.get(currentPosition); // Use a new name to avoid confusion if modified later
    if (N_P == null) {
      N_P = ladders.get(currentPosition);
    }
    final Integer newPositionFinal = N_P; // Make it final for the lambda

    Tile F_D = landedTile; // Use a new name
    if (newPositionFinal != null) {
      F_D = board.tile(newPositionFinal);
      player.moveTo(F_D); // Player moves due to snake/ladder

      // Log the snake/ladder move
      final int originalPosition = currentPosition; // Capture for lambda
      Log.game()
          .info(
              () -> // Lambda expression
              player.getName()
                      + (newPositionFinal > originalPosition // Use final variable
                          ? " climbs a ladder"
                          : " slides down a snake")
                      + " to tile "
                      + newPositionFinal); // Use final variable
    }
    final Tile finalDestinationForBumping = F_D; // Make it final for the lambda

    // Bumping logic (only if not on start tile and not the same player)
    if (finalDestinationForBumping.id() != board.start().id()) {
      game.players().stream()
          .filter(p -> p != player)
          .filter(
              p ->
                  p.getCurrentTile() != null
                      && p.getCurrentTile().id()
                          == finalDestinationForBumping.id()) // Use final variable
          .forEach(
              otherPlayer -> {
                otherPlayer.moveTo(board.start());
                Log.game()
                    .info(
                        () ->
                            player.getName()
                                + " bumps "
                                + otherPlayer.getName()
                                + " back to start from tile "
                                + finalDestinationForBumping.id()); // Use final variable
              });
    }
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
