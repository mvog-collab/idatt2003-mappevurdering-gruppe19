import java.util.Random;

public class Die {

  private int lastRolledValue;
  private final Random random = new Random();

  public Die() {}

  public int rollDie() {
    int roll = random.nextInt(1, 7);
    setLastRolledValue(roll);
    return roll;
  }

  public int getLastRolledValue() {
    return lastRolledValue;
  }

  public void setLastRolledValue(int lastRolledValue) {
    if (lastRolledValue < 0 || lastRolledValue > 6) {
      throw new IllegalArgumentException("Invalid roll of die.");
    }
    this.lastRolledValue = lastRolledValue;
  }
}
