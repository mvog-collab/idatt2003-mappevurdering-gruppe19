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
        String token   = current.token();
        int    startId = current.tileId();
    
        int rolled = gameGateway.rollDice();
        var dice   = gameGateway.lastDiceValues();
        view.showDice(dice.get(0), dice.get(1));
    
        int pathEndId = Math.min(startId + rolled, gameGateway.boardSize());
    
        view.animateMove(token, startId, pathEndId, () -> {
            refreshTokens();
            if (gameGateway.hasWinner()) {
                view.announceWinner(current.name());
            } else {
                view.enableRollButton();
            }
        });
    }

    public void resetGame() {
        gameGateway.resetGame();
        view.enableRollButton();
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
