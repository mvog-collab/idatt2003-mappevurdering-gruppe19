package edu.games.engine.strategy;

import edu.games.engine.board.LudoBoard;
import edu.games.engine.board.Tile;
import edu.games.engine.exception.ValidationException;
import edu.games.engine.impl.DefaultGame;
import edu.games.engine.model.LudoColor;
import edu.games.engine.model.Player;
import edu.games.engine.model.PlayerPiece;
import edu.games.engine.rule.RuleEngine;

public class LudoGameStrategy implements GameStrategy {
  private final RuleEngine ruleEngine;

  public LudoGameStrategy(RuleEngine ruleEngine) {
    this.ruleEngine = ruleEngine;
  }

  @Override
  public void initializeGame(DefaultGame game) {
    game.players().forEach(player -> player.getPieces().forEach(p -> p.moveTo(null)));
  }

  @Override
  public boolean processDiceRoll(Player player, int diceValue, DefaultGame game) {
    return ruleEngine.grantsExtraTurn(player, game.dice().lastValues(), game);
  }

  @Override
  public Tile movePiece(Player player, int pieceIndex, int diceValue, DefaultGame game) {
    if (player == null
        || game == null
        || !(game.board() instanceof LudoBoard)
        || pieceIndex < 0
        || pieceIndex >= player.getPieces().size()) {
      throw new ValidationException("pieceIndex out of range: " + pieceIndex);
    }
    PlayerPiece piece = player.getPiece(pieceIndex);
    LudoBoard board = (LudoBoard) game.board();
    LudoColor color = LudoColor.valueOf(player.getToken().name());

    if (piece.isAtHome()) {
      if (diceValue == 6) {
        return board.getStartTile(color);
      } else {
        return null;
      }
    } else {
      return board.move(piece.getCurrentTile(), diceValue, color);
    }
  }

  @Override
  public boolean checkWinCondition(Player player, DefaultGame game) {
    return ruleEngine.hasWon(player, game);
  }

  @Override
  public void applySpecialRules(
      Player player, PlayerPiece piece, Tile destinationTile, DefaultGame game) {
    ruleEngine.applyPostLandingEffects(player, piece, destinationTile, game);
  }
}
