package edu.games.engine.strategy;

import edu.games.engine.board.Tile;
import edu.games.engine.impl.DefaultGame;
import edu.games.engine.model.Player;
import edu.games.engine.model.PlayerPiece;

public interface GameStrategy {
  void initializeGame(DefaultGame game);

  boolean processDiceRoll(Player player, int diceValue, DefaultGame game);

  Tile movePiece(Player player, int pieceIndex, int diceValue, DefaultGame game);

  boolean checkWinCondition(Player player, DefaultGame game);

  void applySpecialRules(Player player, PlayerPiece piece, Tile destinationTile, DefaultGame game);
}
