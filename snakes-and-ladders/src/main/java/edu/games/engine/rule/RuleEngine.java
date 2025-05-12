package edu.games.engine.rule;

import edu.games.engine.board.Board;
import edu.games.engine.model.Player;
import java.util.List;

public interface RuleEngine {
  // Maybe take in dice?
  boolean apply(Board board, Player player, List<Integer> diceValues);
}
