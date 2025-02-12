import java.util.ArrayList;

public class Dice {

  private final ArrayList<Die> dice;

  public Dice() {
    this.dice = new ArrayList<>();
    Die firstDie = new Die();
    Die secondDie = new Die();

    this.dice.add(firstDie);
    this.dice.add(secondDie);
  }


}
