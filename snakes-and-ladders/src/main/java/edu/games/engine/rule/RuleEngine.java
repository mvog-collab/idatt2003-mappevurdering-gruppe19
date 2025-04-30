package edu.games.engine.rule;

import edu.games.engine.board.Board;
import edu.games.engine.model.Player;

public interface RuleEngine {
    // Maybe take in dice?
    boolean apply(Board board, Player player, int rolledValue);
}
