package edu.ntnu.idatt2003.presentation.service.player;

import edu.ntnu.idatt2003.gateway.view.PlayerView;
import java.util.List;
import javafx.scene.Node;
import javafx.scene.image.ImageView;

/**
 * Interface for player UI management services.
 * <p>
 * Handles player display components, turn indicators, token creation,
 * and player status updates across different game types.
 * </p>
 */
public interface PlayerUIService {
  /**
   * Creates a player display box with turn indicator.
   *
   * @param player  the player to create a box for
   * @param hasTurn whether the player currently has the turn
   * @return the created player display node
   */
  Node createPlayerBox(PlayerView player, boolean hasTurn);

  /**
   * Creates a token image for the specified player.
   *
   * @param tokenName the token identifier
   * @return the created token ImageView
   */
  ImageView createTokenImage(String tokenName);

  /**
   * Updates turn indicator styling for a player box.
   *
   * @param playerBox the player display box to update
   * @param hasTurn   whether the player has the turn
   */
  void updateTurnIndicator(Node playerBox, boolean hasTurn);

  /**
   * Creates game pieces for a player (for multi-piece games).
   *
   * @param player the player to create pieces for
   * @return list of created piece ImageViews
   */
  List<ImageView> createPlayerPieces(PlayerView player);

  /**
   * Updates the player display container with current player list.
   *
   * @param container the container to update
   * @param players   the current list of players
   */
  void updatePlayerDisplay(Node container, List<PlayerView> players);

  /**
   * Creates a turn status box for the current player.
   *
   * @param currentPlayer the current player, or null for default state
   * @return the created turn box node
   */
  Node createCurrentPlayerTurnBox(PlayerView currentPlayer);

  /**
   * Updates the current player turn box with new information.
   *
   * @param turnBox       the turn box to update
   * @param currentPlayer the current player
   * @param message       optional status message to display
   */
  void updateCurrentPlayerTurnBox(Node turnBox, PlayerView currentPlayer, String message);
}