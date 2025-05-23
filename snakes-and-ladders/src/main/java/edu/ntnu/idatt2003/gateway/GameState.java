package edu.ntnu.idatt2003.gateway;

import edu.ntnu.idatt2003.gateway.view.PlayerView;
import edu.ntnu.idatt2003.presentation.fx.OverlayParams;
import java.util.List;

/**
 * Exposes the current state of the game for querying by the UI.
 * <p>
 * Extends {@link GameGateway} so observers can be registered for state changes.
 */
public interface GameState extends GameGateway {

  /**
   * @return {@code true} if a winner has been declared, {@code false} otherwise.
   */
  boolean hasWinner();

  /**
   * @return the name of the player whose turn it currently is, or an empty
   * string if no game is active.
   */
  String currentPlayerName();

  /**
   * @return the size of the board in number of tiles or positions.
   */
  int boardSize();

  /**
   * @return a list of overlay parameters to render additional graphics
   * on top of the board, e.g. spawn points or special markers.
   */
  List<OverlayParams> boardOverlays();

  /**
   * @return a list of {@link PlayerView} objects representing all players,
   * their positions, and whose turn it is.
   */
  List<PlayerView> players();

  /**
   * @return the values of the most recent dice roll(s), as a copy to avoid mutation.
   */
  List<Integer> lastDiceValues();
}
