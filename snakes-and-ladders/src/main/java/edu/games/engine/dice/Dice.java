package edu.games.engine.dice;

import java.util.List;

/**
 * Represents a dice-rolling mechanism.
 * Implementations define how dice are rolled and how values are retrieved.
 */
public interface Dice {

  /**
   * Rolls the dice and returns the total value.
   *
   * @return the sum of all dice rolled
   */
  int roll();

  /**
   * Returns the individual values from the most recent roll.
   *
   * @return a list of integers representing the last rolled values
   */
  List<Integer> lastValues();
}
