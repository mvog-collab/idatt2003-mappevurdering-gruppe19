package edu.games.engine;

public interface RuleEngine {
    // Maybe take in dice?
    boolean apply(Board board, Player player, int rolledValue);
}
