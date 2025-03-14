package edu.ntnu.idatt2003.controllers;

import edu.ntnu.idatt2003.models.Board;
import edu.ntnu.idatt2003.models.GameModel;
import edu.ntnu.idatt2003.ui.BoardView;

public class BoardController {
    private final BoardView boardView;
    private final Board board;
    private final GameModel gameModel;
    
    public BoardController(BoardView boardView, Board board, GameModel gameModel) {
        this.boardView = boardView;
        this.board = board;
        this.gameModel = gameModel;
    }

    public void rollDiceButtonPressed() {
        gameModel.moveCurrentPlayer();
        int newTile = gameModel.getCurrentPlayer().getCurrentTile().getTileId();
        boardView.updatePlayerPosition(newTile, gameModel.getCurrentPlayer());
    }

    public BoardView getBoardView() {
        return boardView;
    }

    public Board getBoard() {
        return board;
    }
}
