package edu.games.engine.board;

import edu.games.engine.model.LudoColor;

public interface LudoTile extends Tile {
  LudoTile next(LudoColor player);
}
