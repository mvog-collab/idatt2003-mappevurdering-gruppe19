package edu.games.engine.rule;

import java.util.List;

import edu.games.engine.board.Board;
import edu.games.engine.model.Player;

public interface RuleEngine {
    // Maybe take in dice?
    boolean apply(Board board, Player player, List<Integer> diceValues);
}
