package edu.games.engine.dice.factory;

import edu.games.engine.dice.Dice;
import edu.games.engine.dice.RandomDice;

/**
 * Factory for creating {@link RandomDice} instances.
 * <p>
 * By default, it creates dice with two rolls, but a custom number of dice can be specified.
 */
public class RandomDiceFactory implements DiceFactory {

  private final int dice;

  /**
   * Creates a factory that produces dice with 2 rolls.
   */
  public RandomDiceFactory() {
    this.dice = 2;
  }

  /**
   * Creates a factory that produces dice with the given number of rolls.
   *
   * @param dice number of dice rolls to simulate
   */
  public RandomDiceFactory(int dice) {
    this.dice = dice;
  }

  /**
   * Creates a new {@link RandomDice} with the configured number of rolls.
   *
   * @return a new {@link Dice} instance
   */
  @Override
  public Dice create() {
    return new RandomDice(dice);
  }
}
