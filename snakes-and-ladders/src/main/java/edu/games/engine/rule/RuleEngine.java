package edu.games.engine.rule;

import edu.games.engine.board.Tile;
import edu.games.engine.impl.DefaultGame;
import edu.games.engine.model.Player;
import edu.games.engine.model.PlayerPiece;
import java.util.List;

public interface RuleEngine {

  /**
   * Determines if the current roll grants the player an extra turn.
   *
   * @param player The current player.
   * @param diceValues The values rolled.
   * @param game The current game instance.
   * @return true if an extra turn should be granted, false otherwise.
   */
  boolean grantsExtraTurn(Player player, List<Integer> diceValues, DefaultGame game);

  /**
   * Applies any effects that occur after a piece lands on a new tile. This can include moving due
   * to a snake/ladder, or bumping other pieces. This method might modify the player's piece's
   * position or other players' pieces.
   *
   * @param player The player whose piece moved.
   * @param piece The piece that moved (can be null for games like SnL where player is the piece).
   * @param landedTile The tile the piece landed on *before* these effects.
   * @param game The current game instance.
   */
  void applyPostLandingEffects(Player player, PlayerPiece piece, Tile landedTile, DefaultGame game);

  /**
   * Checks if the given player has met the conditions to win the game.
   *
   * @param player The player to check.
   * @param game The current game instance.
   * @return true if the player has won, false otherwise.
   */
  boolean hasWon(Player player, DefaultGame game);
}
