package edu.ntnu.idatt2003.gateway;

import edu.ntnu.idatt2003.persistence.BoardAdapter;

public interface GameSetup extends GameGateway {
    void newGame(int boardSize);
    void newGame(BoardAdapter.MapData data);
    void resetGame();
}