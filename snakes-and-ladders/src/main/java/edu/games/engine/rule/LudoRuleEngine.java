package edu.games.engine.rule;

import edu.games.engine.board.LudoPath;
import edu.games.engine.board.Tile;
import edu.games.engine.impl.DefaultGame;
import edu.games.engine.model.LudoColor;
import edu.games.engine.model.Player;
import edu.games.engine.model.PlayerPiece;
import edu.ntnu.idatt2003.utils.Log;
import java.util.List;

public final class LudoRuleEngine implements RuleEngine {
  private final LudoPath path;

  public LudoRuleEngine(LudoPath path) {
    this.path = path;
  }

  @Override
  public boolean grantsExtraTurn(Player player, List<Integer> diceValues, DefaultGame game) {
    return !diceValues.isEmpty() && diceValues.get(0) == 6;
  }

  @Override
  public void applyPostLandingEffects(
      Player player, PlayerPiece piece, Tile landedTile, DefaultGame game) {
    if (player == null || piece == null || landedTile == null || game == null) {
      return;
    }

    // Bumping logic (only on the main ring, not in goal or home)
    if (landedTile.id() > 0 && landedTile.id() <= 52) {
      game.players().stream()
          .filter(p -> p != player) // Don't bump self
          .forEach(
              otherPlayer -> {
                otherPlayer.getPieces().stream()
                    .filter(
                        otherPiece ->
                            otherPiece.isOnBoard()
                                && otherPiece.getCurrentTile().id() == landedTile.id())
                    .forEach(
                        otherPieceToBump -> {
                          otherPieceToBump.moveTo(null); // Send back to home
                          Log.game()
                              .info(
                                  () ->
                                      player.getName()
                                          + " bumps "
                                          + otherPlayer.getName()
                                          + "'s piece back to home from tile "
                                          + landedTile.id());
                        });
              });
    }
  }

  @Override
  public boolean hasWon(Player player, DefaultGame game) {
    if (player == null) {
      return false;
    }
    // All 4 pieces must be on the board and in their respective final goal positions
    LudoColor color = LudoColor.valueOf(player.getToken().name());
    int goalBaseId =
        switch (color) {
          case BLUE -> 53;
          case RED -> 59;
          case GREEN -> 65;
          case YELLOW -> 71;
        };
    int finalGoalTileId = goalBaseId + 5; // Ludo goal length is 6 (0-5 index)

    return player.getPieces().stream()
        .allMatch(
            p ->
                p.isOnBoard()
                    && p.getCurrentTile().id() >= goalBaseId
                    && // in any goal tile
                    p.getCurrentTile().id() == finalGoalTileId);
    // return player.getPieces().stream()
    //         .filter(p -> p.isOnBoard() && p.getCurrentTile().id() > 52) // Piece is in any goal
    // area
    //         .count()
    //     == 4; // All 4 pieces are in some goal area
  }
}
