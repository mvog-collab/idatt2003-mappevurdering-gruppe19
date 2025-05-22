package edu.games.engine.board;

import edu.games.engine.model.LudoColor;

public interface MovementPath {
  Tile start();

  Tile next(Tile current, int steps, LudoColor player);

  boolean isEnd(Tile tile);
}
