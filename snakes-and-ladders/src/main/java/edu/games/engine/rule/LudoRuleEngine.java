package edu.games.engine.rule;

import edu.games.engine.board.LudoPath;
import edu.games.engine.board.Tile;
import edu.games.engine.impl.DefaultGame;
import edu.games.engine.model.LudoColor;
import edu.games.engine.model.Player;
import edu.games.engine.model.PlayerPiece;
import edu.ntnu.idatt2003.utils.Log;
import java.util.List;

/**
 * Rule engine for Ludo, implementing color-specific goal paths,
 * bumping logic, and win conditions.
 */
public final class LudoRuleEngine implements RuleEngine {

  private final LudoPath path;

  /**
   * Creates a new Ludo rule engine using the specified movement path.
   *
   * @param path the Ludo movement path
   */
  public LudoRuleEngine(LudoPath path) {
    this.path = path;
  }

  /**
   * Grants an extra turn when the first (only) die shows a six.
   *
   * @param player the player taking the turn
   * @param diceValues list of rolled dice values
   * @param game the current game context
   * @return true if an extra turn is granted
   */
  @Override
  public boolean grantsExtraTurn(Player player, List<Integer> diceValues, DefaultGame game) {
    return !diceValues.isEmpty() && diceValues.get(0) == 6;
  }

  /**
   * Applies post-move effects such as bumping opponent pieces
   * from a shared tile on the main ring.
   *
   * @param player the player who moved
   * @param piece the piece that was moved
   * @param landedTile the tile the piece landed on
   * @param game the current game context
   */
  @Override
  public void applyPostLandingEffects(
      Player player, PlayerPiece piece, Tile landedTile, DefaultGame game) {
    if (isInvalidState(player, piece, landedTile, game)) return;

    if (isOnMainRing(landedTile)) {
      bumpOpponentsFromTile(player, landedTile, game);
    }
  }

  /**
   * Checks if the player has won. A player wins when all
   * four pieces reach the final tile in the goal path.
   *
   * @param player the player to check
   * @param game the current game
   * @return true if the player has won
   */
  @Override
  public boolean hasWon(Player player, DefaultGame game) {
    if (player == null) return false;

    int goalBaseId = goalBaseId(player);
    int finalGoalTileId = goalBaseId + 5;

    return player.getPieces().stream()
        .allMatch(p -> isPieceInFinalGoal(p, goalBaseId, finalGoalTileId));
  }

  private boolean isInvalidState(Player player, PlayerPiece piece, Tile tile, DefaultGame game) {
    return player == null || piece == null || tile == null || game == null;
  }

  private boolean isOnMainRing(Tile tile) {
    int id = tile.tileId();
    return id > 0 && id <= 52;
  }

  private void bumpOpponentsFromTile(Player currentPlayer, Tile tile, DefaultGame game) {
    game.getPlayers().stream()
        .filter(other -> other != currentPlayer)
        .forEach(
            other ->
                other.getPieces().stream()
                    .filter(p -> shouldBumpPiece(p, tile))
                    .forEach(p -> sendPieceHome(currentPlayer, other, p, tile)));
  }

  private boolean shouldBumpPiece(PlayerPiece piece, Tile tile) {
    return piece.isOnBoard() && piece.getCurrentTile().tileId() == tile.tileId();
  }

  private void sendPieceHome(Player bumper, Player bumpedPlayer, PlayerPiece piece, Tile fromTile) {
    piece.moveTo(null);
    Log.game()
        .info(
            () ->
                String.format(
                    "%s bumps %s's piece back to home from tile %d",
                    bumper.getName(), bumpedPlayer.getName(), fromTile.tileId()));
  }

  private int goalBaseId(Player player) {
    LudoColor color = LudoColor.valueOf(player.getToken().name());
    return switch (color) {
      case BLUE -> 53;
      case RED -> 59;
      case GREEN -> 65;
      case YELLOW -> 71;
    };
  }

  private boolean isPieceInFinalGoal(PlayerPiece piece, int goalBaseId, int finalGoalTileId) {
    return piece.isOnBoard()
        && piece.getCurrentTile().tileId() >= goalBaseId
        && piece.getCurrentTile().tileId() == finalGoalTileId;
  }
}
