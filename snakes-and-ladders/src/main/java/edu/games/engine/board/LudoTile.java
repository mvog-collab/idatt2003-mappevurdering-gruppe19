package edu.games.engine.board;

import edu.games.engine.model.LudoColor;

public sealed interface LudoTile extends Tile permits LudoRingTile, LudoGoalTile {
  LudoTile next(LudoColor owner);
}
