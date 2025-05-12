package edu.games.engine.dice;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RandomDice implements Dice {

  private final int dice;
  private final Random random;
  private final List<Integer> lastRolledValues = new ArrayList<>();

  public RandomDice(int dice) {
    this.dice = dice;
    this.random = new Random();
  }

  public RandomDice(int dice, Random rnd) {
    if (dice < 1) throw new IllegalArgumentException("dice < 1");
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

  @Override
  public List<Integer> lastValues() {
    return List.copyOf(lastRolledValues);
  }
}
