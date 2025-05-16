package edu.games.engine.strategy.factory;

import edu.games.engine.board.LudoPath;
import edu.games.engine.rule.LudoRuleEngine;
import edu.games.engine.rule.RuleEngine;
import edu.games.engine.rule.SnlRuleEngine;
import edu.games.engine.strategy.GameStrategy;
import edu.games.engine.strategy.LudoGameStrategy;
import edu.games.engine.strategy.SnlGameStrategy;
import edu.ntnu.idatt2003.persistence.BoardAdapter;

public class GameStrategyFactory {

  private GameStrategyFactory() {}

  public static GameStrategy createLudoStrategy(LudoPath ludoPath) {
    RuleEngine ludoRuleEngine = new LudoRuleEngine(ludoPath);
    return new LudoGameStrategy(ludoRuleEngine);
  }

  public static GameStrategy createSnlStrategy(BoardAdapter.MapData mapData) {
    RuleEngine snlRuleEngine = new SnlRuleEngine(mapData.snakes(), mapData.ladders());
    return new SnlGameStrategy(snlRuleEngine, mapData.snakes(), mapData.ladders());
  }
}
