package edu.ntnu.idatt2003.game_logic;

import edu.ntnu.idatt2003.models.Board;
import edu.ntnu.idatt2003.models.Tile;

public class BoardMaker {

    private BoardMaker() {}

    public  static Board createBoard(int size) {
        if (121 < size || size < 0) {
          throw new IllegalArgumentException("Invalid board size.");
        }
        Board board = new Board(size);
        for (int i = 0; i < size; i++) {
          board.addTile(new Tile(i));
        }
    
        for (int i = 0; i < size - 1; i++) {
          Tile currentTile = board.getTile(i);
          Tile nextTile = board.getTile(i + 1);
          currentTile.setNextTile(nextTile);
        }
        SnakeAndLadderConfigurator.generateSnakesAndLadders(board);
        return board;
    }
}
