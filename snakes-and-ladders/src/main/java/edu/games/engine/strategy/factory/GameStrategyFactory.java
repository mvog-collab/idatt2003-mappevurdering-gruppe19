package edu.games.engine.strategy.factory;

import edu.games.engine.board.LudoPath;
import edu.games.engine.rule.LudoRuleEngine;
import edu.games.engine.rule.RuleEngine;
import edu.games.engine.rule.SnlRuleEngine;
import edu.games.engine.strategy.GameStrategy;
import edu.games.engine.strategy.LudoGameStrategy;
import edu.games.engine.strategy.SnlGameStrategy;
import edu.ntnu.idatt2003.persistence.BoardAdapter;

/**
 * Factory class for creating game strategies for different board game types.
 * <p>
 * Supports both Ludo and Snakes and Ladders (SNL).
 */
public class GameStrategyFactory {

  // Private constructor to prevent instantiation
  private GameStrategyFactory() {}

  /**
   * Creates a Ludo game strategy based on the given LudoPath.
   *
   * @param ludoPath the LudoPath used to construct movement and rule logic
   * @return a GameStrategy implementation for Ludo
   */
  public static GameStrategy createLudoStrategy(LudoPath ludoPath) {
    RuleEngine ludoRuleEngine = new LudoRuleEngine(ludoPath);
    return new LudoGameStrategy(ludoRuleEngine);
  }

  /**
   * Creates a Snakes and Ladders game strategy based on parsed map data.
   *
   * @param mapData the board data including snakes and ladders positions
   * @return a GameStrategy implementation for Snakes and Ladders
   */
  public static GameStrategy createSnlStrategy(BoardAdapter.MapData mapData) {
    RuleEngine snlRuleEngine = new SnlRuleEngine(mapData.snakes(), mapData.ladders());
    return new SnlGameStrategy(snlRuleEngine, mapData.snakes(), mapData.ladders());
  }
}
