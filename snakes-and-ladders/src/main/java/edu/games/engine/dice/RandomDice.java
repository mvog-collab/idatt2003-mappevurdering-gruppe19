package edu.games.engine.dice;

import edu.games.engine.exception.ValidationException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * A dice implementation that rolls one or more six-sided dice using {@link Random}.
 */
public class RandomDice implements Dice {

  private final int numberOfDice;
  private final Random random;
  private final List<Integer> lastRolledValues = new ArrayList<>();

  /**
   * Creates a new random dice roller with a given number of dice.
   *
   * @param numberOfDice the number of dice to roll
   * @throws ValidationException if the number is less than 1
   */
  public RandomDice(int numberOfDice) {
    if (numberOfDice < 1) {
      throw new ValidationException("Invalid number of dice: must be greater than 0");
    }
    this.numberOfDice = numberOfDice;
    this.random = new Random();
  }

  /**
   * Creates a new random dice roller with a given number of dice and a custom random source.
   *
   * @param numberOfDice the number of dice to roll
   * @param rnd a {@link Random} instance to use
   * @throws ValidationException if arguments are invalid
   */
  public RandomDice(int numberOfDice, Random rnd) {
    if (numberOfDice < 1)
      throw new ValidationException("Invalid number of dice: must be greater than 0");
    if (rnd == null) {
      throw new ValidationException("Invalid rnd: must not be null");
    }
    this.numberOfDice = numberOfDice;
    this.random = rnd;
  }

  /**
   * Rolls all dice and returns the total value.
   *
   * @return the sum of the dice rolled
   */
  @Override
  public int roll() {
    lastRolledValues.clear();
    int sum = 0;
    for (int i = 0; i < numberOfDice; i++) {
      int dieValue = random.nextInt(1, 7);
      lastRolledValues.add(dieValue);
      sum += dieValue;
    }
    return sum;
  }

  /**
   * Returns how many dice this instance rolls.
   *
   * @return the number of dice
   */
  public int getDiceCount() {
    return numberOfDice;
  }

  /**
   * Gets the values from the last roll.
   *
   * @return an unmodifiable list of last rolled values
   */
  @Override
  public List<Integer> lastValues() {
    return List.copyOf(lastRolledValues);
  }
}
