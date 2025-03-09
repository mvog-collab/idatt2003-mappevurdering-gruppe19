package edu.ntnu.idatt2003.models;
import java.util.ArrayList;
import java.util.List;

public class Dice {

  private final List<Die> dice;

  public Dice() {
    this.dice = new ArrayList<>();
    Die firstDie = new Die();
    Die secondDie = new Die();

    this.dice.add(firstDie);
    this.dice.add(secondDie);
  }

  public int rollDice() {
    int firstRoll = dice.getFirst().rollDie();
    int secondRoll = dice.get(1).rollDie();
    return firstRoll + secondRoll;
  }

  public boolean isPair() {
    return dice.getFirst().getLastRolledValue() == dice.get(1).getLastRolledValue();
  }

  public boolean isPairOfSix() {
    return dice.getFirst().getLastRolledValue() == 6 || dice.get(1).getLastRolledValue() == 6;
  }

  public List<Die> getDice() {
    return dice;
  }
}
