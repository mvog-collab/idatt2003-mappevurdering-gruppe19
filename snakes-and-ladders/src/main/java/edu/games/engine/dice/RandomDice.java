package edu.games.engine.dice;

import edu.games.engine.exception.ValidationException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RandomDice implements Dice {

  private final int numberOfDice;
  private final Random random;
  private final List<Integer> lastRolledValues = new ArrayList<>();

  public RandomDice(int numberOfDice) {
    if (numberOfDice < 1 ) {
      throw new ValidationException("Invalid number of dice: must be greater than 0");
    }
    this.numberOfDice= numberOfDice;
    this.random = new Random();
  }

  public RandomDice(int numberOfDice, Random rnd) {
    if (numberOfDice < 1)
      throw new ValidationException("Invalid number of dice: must be greater than 0");
    if (rnd == null) {
      throw new ValidationException("Invalid rnd: must not be null");
    }
    this.numberOfDice = numberOfDice;
    this.random = rnd;
  }

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

  public int getDiceCount() {
    return numberOfDice;
  }


  @Override
  public List<Integer> lastValues() {
    return List.copyOf(lastRolledValues);
  }
}
