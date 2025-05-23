package edu.ntnu.idatt2003.presentation.snl.controller;

import edu.ntnu.idatt2003.gateway.CompleteBoardGame;
import edu.ntnu.idatt2003.gateway.view.PlayerView;
import edu.ntnu.idatt2003.presentation.common.controller.AbstractGameController;
import edu.ntnu.idatt2003.presentation.snl.view.SnlBoardView;

/**
 * Controller for managing Snakes and Ladders game logic and board interactions.
 * Handles dice rolling, player movement, animations, and game flow specific to
 * SNL.
 * Coordinates between the game model (gateway) and the board view.
 */
public class SnlBoardController extends AbstractGameController<SnlBoardView> {

  /**
   * Creates a new SNL board controller.
   * Sets up the connection between the board view and the game logic gateway.
   *
   * @param boardView the SNL board view to control
   * @param gateway   the game logic gateway for board operations
   */
  public SnlBoardController(SnlBoardView boardView, CompleteBoardGame gateway) {
    super(boardView, gateway);
  }

  /**
   * Handles the dice rolling action when a player clicks the roll button.
   * Rolls the dice, shows the results on screen, and triggers player movement.
   * Follows SNL rules where rolling double 6s (12 total) gives another turn.
   */
  @Override
  protected void onRollDice() {
    PlayerView current = getCurrentPlayer();
    if (current == null) {
      view.enableRollButton();
      return;
    }

    int rolled = gateway.rollDice();
    var dice = gateway.lastDiceValues();
    view.showDice(dice.get(0), dice.get(1));

    handlePlayerMovement(current, rolled);
  }

  /**
   * Manages player movement after a dice roll.
   * If the roll is 12 (double 6s), the player gets another turn.
   * Otherwise, animates the player token moving along the board path.
   *
   * @param player the player who rolled the dice
   * @param rolled the total value rolled on the dice
   */
  private void handlePlayerMovement(PlayerView player, int rolled) {
    if (rolled != 12) {
      String token = player.playerToken();
      int startId = player.tileId();
      int pathEndId = Math.min(startId + rolled, gateway.boardSize());
      view.showStatusMessage(player.playerName() + " rolled " + rolled + "!");

      view.animateMove(token, startId, pathEndId, this::refreshTokens);
    } else {
      view.showStatusMessage(player.playerName() + " rolled " + rolled + " and has to wait a turn!");
      view.enableRollButton();
    }
  }

  /**
   * Refreshes the game state after a player's move is complete.
   * Updates player positions on the board and re-enables the roll button
   * if the game hasn't ended yet. This is typically called after move animations
   * finish.
   */
  private void refreshTokens() {
    view.setPlayers(gateway.players(), gateway.boardOverlays());
    if (!gateway.hasWinner()) {
      view.enableRollButton();
    }
  }

  /**
   * Finds and returns the player whose turn it currently is.
   * Searches through all players to find the one with the active turn flag.
   *
   * @return the player who should roll next, or null if no player has the turn
   */
  private PlayerView getCurrentPlayer() {
    return gateway.players().stream().filter(PlayerView::hasTurn).findFirst().orElse(null);
  }

  /**
   * Gets the SNL board view managed by this controller.
   * Provides access to the view for external components that need to interact
   * with it.
   *
   * @return the board view instance
   */
  public SnlBoardView getView() {
    return view;
  }
}