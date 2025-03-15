package edu.ntnu.idatt2003.controllers;

import java.time.LocalDate;
import java.util.Optional;

import edu.ntnu.idatt2003.game_logic.BoardMaker;
import edu.ntnu.idatt2003.models.Board;
import edu.ntnu.idatt2003.models.GameModel;
import edu.ntnu.idatt2003.models.Tile;
import edu.ntnu.idatt2003.ui.BoardView;

public class BoardController {
    private final BoardView boardView;
    private final Board board;
    private final GameModel gameModel;
    
    
    public BoardController(BoardView boardView, GameModel gameModel) {
        this.boardView = boardView;
        this.board = BoardMaker.createBoard(boardView.getHeight() * boardView.getWidth());
        this.gameModel = gameModel;
    }
    
    public void rollDiceForCurrentPlayer() {
        Optional<Tile> newTile = gameModel.moveCurrentPlayer();
        newTile.ifPresent(tile ->
        boardView.updatePlayerPosition(tile.getTileId(), gameModel.getCurrentPlayer()));
        
        //TODO: add something else to do if tile is not present
    }
    
    public void addPlayer(String name, LocalDate birthday) {
        gameModel.addPlayer(name, birthday);
    }

    public void playATurn() {
        rollDiceForCurrentPlayer();

        if (gameModel.hasPlayerWon(gameModel.getCurrentPlayer())) {
            boardView.announceWinner(gameModel.getCurrentPlayer());
        }

        gameModel.nextPlayersTurn();
        //TODO: Add more to happen in a turn. Updating UI, checking winner, logging, etc
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
