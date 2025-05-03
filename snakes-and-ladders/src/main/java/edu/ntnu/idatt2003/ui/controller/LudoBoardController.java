package edu.ntnu.idatt2003.ui.controller;

import edu.ntnu.idatt2003.gateway.GameGateway;
import edu.ntnu.idatt2003.gateway.view.PlayerView;
import edu.ntnu.idatt2003.ui.view.LudoBoardView;

public final class LudoBoardController {

    private final LudoBoardView view;
    private final GameGateway   gw;

    public LudoBoardController(LudoBoardView view, GameGateway gw) {
        this.view = view;
        this.gw   = gw;

        view.getRollButton() .setOnAction(e -> playTurn());
        view.getAgainButton().setOnAction(e -> {
            gw.resetGame();
            refreshTokens();
        });

        refreshTokens();
    }

    /* -------- gameplay ---------- */

    private void playTurn() {
        view.disableRollButton();
    
        PlayerView current = gw.players().stream()
                               .filter(PlayerView::hasTurn)
                               .findFirst()
                               .orElseThrow();
    
        int startId = current.tileId();
        int rolled  = gw.rollDice();
    
        // show the die
        view.showDice(rolled);
    
        int endId = Math.min(startId + rolled, gw.boardSize());
    
        // animate the move, then re-snapshot & maybe announce winner
        view.animateMove(
          current.token(),
          startId,
          endId,
          () -> {
            refreshTokens();
            if (gw.hasWinner()) {
              view.announceWinner(current.name());
            }
          }
        );
    }

    private void refreshTokens() {
        // give the view the full list of players + overlays
        view.setPlayers(gw.players());
        view.enableRollButton();
    }
}