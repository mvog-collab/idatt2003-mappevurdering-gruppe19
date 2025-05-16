package edu.games.engine.rule.factory;

import edu.games.engine.rule.RuleConfig;
import edu.games.engine.rule.RuleEngine;
import edu.games.engine.rule.SnlRuleEngine;
import edu.ntnu.idatt2003.persistence.BoardAdapter;

public class SnlRuleFactory implements RuleFactory {

  @Override
  public RuleEngine create(BoardAdapter.MapData data, RuleConfig config) {
    return new SnlRuleEngine(data.snakes(), data.ladders());
  }
}
