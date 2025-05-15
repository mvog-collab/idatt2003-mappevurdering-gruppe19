package edu.ntnu.idatt2003.ui.snl.controller;

import edu.ntnu.idatt2003.gateway.CompleteBoardGame;
import edu.ntnu.idatt2003.gateway.view.PlayerView;
import edu.ntnu.idatt2003.ui.common.controller.AbstractGameController;
import edu.ntnu.idatt2003.ui.snl.view.BoardView;

public class BoardController extends AbstractGameController<BoardView> {

  public BoardController(BoardView boardView, CompleteBoardGame gateway) {
    super(boardView, gateway);
  }

  @Override
  protected void onRollDice() {
    PlayerView current = getCurrentPlayer();
    if (current == null) {
      view.enableRollButton();
      return;
    }

    // Roll dice and get results
    int rolled = gateway.rollDice();
    var dice = gateway.lastDiceValues();
    view.showDice(dice.get(0), dice.get(1));

    // Handle movement
    handlePlayerMovement(current, rolled);
  }

  private void handlePlayerMovement(PlayerView player, int rolled) {
    if (rolled != 12) {
      String token = player.token();
      int startId = player.tileId();
      int pathEndId = Math.min(startId + rolled, gateway.boardSize());

      view.animateMove(token, startId, pathEndId, this::refreshTokens);
    } else {
      view.enableRollButton();
    }
  }

  private void refreshTokens() {
    view.setPlayers(gateway.players(), gateway.boardOverlays());
    if (!gateway.hasWinner()) {
      view.enableRollButton();
    }
  }

  private PlayerView getCurrentPlayer() {
    return gateway.players().stream().filter(PlayerView::hasTurn).findFirst().orElse(null);
  }

  public BoardView getView() {
    return view;
  }
}
