package edu.ntnu.idatt2003.ui.controller;

import edu.ntnu.idatt2003.gateway.GameGateway;
import edu.ntnu.idatt2003.gateway.view.PlayerView;
import edu.ntnu.idatt2003.ui.view.BoardView;

import java.util.Optional;

public class BoardController {
    private final BoardView view;
    private final GameGateway gameGateway;
    
    public BoardController(BoardView boardView, GameGateway gameGateway) {
        this.view = boardView;
        this.gameGateway = gameGateway;
    }

    public void playTurn() {
        view.disableRollButton();
        PlayerView current = playerWithTurn().orElseThrow();
        int startId        = current.tileId();

        int rolled = gameGateway.rollDice();
        int d1 = gameGateway.lastDiceValues().get(0);
        int d2 = gameGateway.lastDiceValues().get(1);
        view.showDice(d1, d2);
    
        int destId  = startId + rolled;
    
        view.animateMove(current.token(), startId, destId, () -> {
          refreshTokens();
          if (gameGateway.hasWinner()){
              view.announceWinner(current.name());
              return;
            }
            view.enableRollButton();
        });
      }

    public void resetGame() {
        gameGateway.resetGame();
        refreshTokens();
    }

    private void refreshTokens() {
        view.setPlayers(gameGateway.players(), gameGateway.boardOverlays());
    }

    private Optional<PlayerView> playerWithTurn() {
        return gameGateway.players().stream()
                          .filter(PlayerView::hasTurn)
                          .findFirst();
    }

    public BoardView getView() {
        return view;
    }
}
