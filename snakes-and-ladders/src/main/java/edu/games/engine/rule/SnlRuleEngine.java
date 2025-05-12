package edu.games.engine.rule;

import edu.games.engine.board.Board;
import edu.games.engine.board.LinearBoard;
import edu.games.engine.model.Player;
import java.util.List;
import java.util.Map;

public final class SnlRuleEngine implements RuleEngine {

  private final Map<Integer, Integer> snakes;
  private final Map<Integer, Integer> ladders;
  private final RuleConfig.ExtraTurnPolicy extraTurn;

  public SnlRuleEngine(
      Map<Integer, Integer> snakes,
      Map<Integer, Integer> ladders,
      RuleConfig.ExtraTurnPolicy extraTurn) {
    this.snakes = snakes;
    this.ladders = ladders;
    this.extraTurn = extraTurn;
  }

  @Override
  public boolean apply(Board board, Player player, List<Integer> dice) {

    int sum = dice.stream().mapToInt(Integer::intValue).sum();
    boolean doubleTurn = dice.size() == 2 && dice.get(0).equals(dice.get(1));

    if (sum == 12) return false;

    if (sum > 0) {
      int position = player.getCurrentTile().id();
      Integer to = snakes.get(position);
      if (to == null) to = ladders.get(position);
      if (to != null) player.moveTo(((LinearBoard) board).tile(to));
    }

    return doubleTurn;
  }
}
