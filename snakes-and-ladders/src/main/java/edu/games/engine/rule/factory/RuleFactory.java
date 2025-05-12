package edu.games.engine.rule.factory;

import edu.games.engine.rule.RuleConfig;
import edu.games.engine.rule.RuleEngine;
import edu.ntnu.idatt2003.persistence.BoardAdapter;

public interface RuleFactory {
  RuleEngine create(BoardAdapter.MapData data, RuleConfig config);
}
