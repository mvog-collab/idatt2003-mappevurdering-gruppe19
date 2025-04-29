package edu.ntnu.idatt2003.controllers;

import edu.ntnu.idatt2003.gateway.GameGateway;
import edu.ntnu.idatt2003.gateway.PlayerView;

import java.util.Optional;

import edu.ntnu.idatt2003.ui.BoardView;

public class BoardController {
    private final BoardView view;
    private final GameGateway gameGateway;
    
    public BoardController(BoardView boardView, GameGateway gameGateway) {
        this.view = boardView;
        this.gameGateway = gameGateway;
    }

    public void playTurn() {
        view.disableRollButton();
        int rolled = gameGateway.rollDice();
    
        PlayerView current = playerWithTurn().orElseThrow();
        int startId = current.tileId();
        int destId  = startId + rolled;                 // animation only (gateway will adjust)
    
        view.animateMove(current.token(), startId, destId, () -> {
          refreshTokens();
          if (gameGateway.hasWinner()) view.announceWinner(current.name());
          view.enableRollButton();
        });
      }

    public void resetGame() {
        gameGateway.newGame(gameGateway.boardSize());
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
