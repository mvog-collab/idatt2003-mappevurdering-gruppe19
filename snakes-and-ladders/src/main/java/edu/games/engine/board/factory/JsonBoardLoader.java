package edu.games.engine.board.factory;

import edu.games.engine.board.Board;

public interface JsonBoardLoader {
    Board create(int size);
}
