package edu.ntnu.idatt2003.controllers;

import edu.ntnu.idatt2003.models.Player;
import edu.ntnu.idatt2003.models.PlayerTokens;

import java.time.LocalDate;
import java.util.Optional;

import edu.ntnu.idatt2003.models.Board;
import edu.ntnu.idatt2003.models.GameModel;
import edu.ntnu.idatt2003.models.Tile;
import edu.ntnu.idatt2003.ui.BoardView;
import edu.ntnu.idatt2003.utils.ResourcePaths;

public class BoardController {
    private final BoardView boardView;
    private final Board board;
    private final GameModel gameModel;
    
    
    public BoardController(BoardView boardView, GameModel gameModel) {
        this.boardView = boardView;
        this.gameModel = gameModel;
        this.board = gameModel.getBoard();
    }

    private Tile getTileAfterMovingRolledValue(int startTileId, int steps) {
        Tile tileToLandOn = gameModel.getBoard().getTile(startTileId);
        for (int i = 0; i < steps; i++) {
            if (tileToLandOn.getNextTile() == null) break;
            tileToLandOn = tileToLandOn.getNextTile();
        }
        return tileToLandOn;
    }

    public void addPlayer(String name, PlayerTokens token, LocalDate birthday) {
        gameModel.addPlayer(name, token, birthday);
    }

    public void playATurn() {
        boardView.disableRollButton();
        int startTileId = gameModel.getCurrentPlayer().getCurrentTile().getTileId();
        int rolledValue = gameModel.getDice().rollDice();

        if (rolledValue == 12) {
            //TODO: Add something to happen when player rolls 12
            gameModel.nextPlayersTurn();
            boardView.enableRollButton();
            return;
        }

        Tile tileBeforeSnakeOrLadder = getTileAfterMovingRolledValue(startTileId, rolledValue);

        boardView.movePlayerByDiceRoll(startTileId, tileBeforeSnakeOrLadder.getTileId(), gameModel.getCurrentPlayer(), () -> {
            Optional<Tile> newTile = gameModel.moveCurrentPlayer(rolledValue); 
            newTile.ifPresent(tile -> boardView.movePlayerOnSnakeOrLadder(tile.getTileId(), gameModel.getCurrentPlayer()));
            
            Player playerToBeRemoved = gameModel.playerCollision();
            if (playerToBeRemoved != null) {
                boardView.movePlayerByDiceRoll(0, playerToBeRemoved.getCurrentTile().getTileId(), playerToBeRemoved, null);
            }

            if (gameModel.hasPlayerWon(gameModel.getCurrentPlayer())) {
                boardView.announceWinner(gameModel.getCurrentPlayer());
            }

            gameModel.nextPlayersTurn();
            boardView.updateCurrentPlayerView(gameModel.getCurrentPlayer());
            boardView.enableRollButton();
        });
        //TODO: Add more to happen in a turn. Updating UI, checking winner, logging, etc
    }

    private void playAgain() {
        for (Player player : gameModel.getPlayers()) {
            gameModel.setStartPosition(player); // Setter spiller på startfelt
        }

        if (!gameModel.getPlayers().isEmpty()) {
            gameModel.setCurrentPlayer(gameModel.getPlayers().getFirst());
            boardView.updateCurrentPlayerView(gameModel.getCurrentPlayer());
        }

        boardView.enableRollButton();
    }


    public String getTokenPath(PlayerTokens token) {
        return ResourcePaths.IMAGE_DIR + token.getImagePath();
    }

    public BoardView getBoardView() {
        return boardView;
    }
    
    public Board getBoard() {
        return board;
    }
    
    public GameModel getGameModel() {
        return gameModel;
    }
}
