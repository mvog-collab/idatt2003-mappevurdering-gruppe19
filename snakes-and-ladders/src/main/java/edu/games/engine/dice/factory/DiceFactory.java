package edu.games.engine.dice.factory;

import edu.games.engine.dice.Dice;

/**
 * Factory interface for creating {@link Dice} instances.
 */
public interface DiceFactory {

  /**
   * Creates a new dice instance.
   *
   * @return a new {@code Dice} object
   */
  Dice create();
}
