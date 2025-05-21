package edu.games.engine.dice;

import edu.games.engine.exception.ValidationException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RandomDice implements Dice {

  private final int dice;
  private final Random random;
  private final List<Integer> lastRolledValues = new ArrayList<>();

  public RandomDice(int dice) {
    if (dice < 1 ) {
      throw new ValidationException("Invalid dice: must be greater than 0");
    }
    this.dice = dice;
    this.random = new Random();
  }

  public RandomDice(int dice, Random rnd) {
    if (dice < 1)
      throw new ValidationException("Invalid dice: must be greater than 0");
    if (rnd == null) {
      throw new ValidationException("Invalid rnd: must not be null");
    }
    this.dice = dice;
    this.random = rnd;
  }

  @Override
  public int roll() {
    lastRolledValues.clear();
    int sum = 0;
    for (int i = 0; i < dice; i++) {
      int dieValue = random.nextInt(1, 7);
      lastRolledValues.add(dieValue);
      sum += dieValue;
    }
    return sum;
  }

  public int getDiceCount() {
    return dice;
  }


  @Override
  public List<Integer> lastValues() {
    return List.copyOf(lastRolledValues);
  }
}
