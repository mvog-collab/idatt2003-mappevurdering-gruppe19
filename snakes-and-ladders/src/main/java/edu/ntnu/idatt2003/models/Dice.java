package edu.ntnu.idatt2003.models;
import java.util.ArrayList;
import java.util.List;

public class Dice {

  private final List<Die> diceList;

  public Dice() {
    this.diceList = new ArrayList<>();
    Die firstDie = new Die();
    Die secondDie = new Die();

    this.diceList.add(firstDie);
    this.diceList.add(secondDie);
  }

  public int rollDice() {
    int firstRoll = diceList.getFirst().rollDie();
    int secondRoll = diceList.get(1).rollDie();
    return firstRoll + secondRoll;
  }

  public boolean isPair() {
    return diceList.getFirst().getLastRolledValue() == diceList.get(1).getLastRolledValue();
  }

  public boolean isPairOfSix() {
    return diceList.getFirst().getLastRolledValue() == 6 || diceList.get(1).getLastRolledValue() == 6;
  }

  public List<Die> getDiceList() {
    return diceList;
  }
}
