package edu.games.engine.strategy;

import edu.games.engine.board.Tile;
import edu.games.engine.impl.DefaultGame;
import edu.games.engine.model.Player;
import edu.games.engine.model.PlayerPiece;
import edu.games.engine.rule.RuleEngine;
import java.util.Map;

public class SnlGameStrategy implements GameStrategy {
  private final RuleEngine ruleEngine;
  private final Map<Integer, Integer> snakes;
  private final Map<Integer, Integer> ladders;

  public SnlGameStrategy(
      RuleEngine ruleEngine, Map<Integer, Integer> snakes, Map<Integer, Integer> ladders) {
    this.ruleEngine = ruleEngine;
    this.snakes = snakes;
    this.ladders = ladders;
  }

  @Override
  public void initializeGame(DefaultGame game) {
    if (game != null && game.board() != null) {
      for (Player player : game.players()) {
        player.moveTo(game.board().start());
      }
    }
  }

  @Override
  public boolean processDiceRoll(Player player, int diceValue, DefaultGame game) {
    // Delegate to RuleEngine
    return ruleEngine.grantsExtraTurn(player, game.dice().lastValues(), game);
  }

  @Override
  public Tile movePiece(Player player, int pieceIndexIgnored, int diceValue, DefaultGame game) {
    if (player == null || game == null || player.getCurrentTile() == null || game.board() == null) {
      return null;
    }
    return game.board().move(player.getCurrentTile(), diceValue);
  }

  @Override
  public boolean checkWinCondition(Player player, DefaultGame game) {
    return ruleEngine.hasWon(player, game);
  }

  @Override
  public void applySpecialRules(
      Player player, PlayerPiece pieceIgnored, Tile destinationTile, DefaultGame game) {
    ruleEngine.applyPostLandingEffects(player, null, destinationTile, game);
  }

  public Map<Integer, Integer> getSnakes() {
    return Map.copyOf(snakes);
  }

  public Map<Integer, Integer> getLadders() {
    return Map.copyOf(ladders);
  }
}
