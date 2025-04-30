package edu.games.engine.board.factory;

import edu.games.engine.board.Board;
import edu.games.engine.board.LinearBoard;

public class LinearBoardFactory implements JsonBoardLoader {

    @Override
    public Board create(int size) {
        return new LinearBoard(size);
    }
}
