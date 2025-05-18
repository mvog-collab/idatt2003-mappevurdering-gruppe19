package edu.games.engine.dice.factory;

import edu.games.engine.dice.Dice;
import edu.games.engine.dice.RandomDice;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RandomDiceFactoryTest {

  @Nested
  class DefaultConstructor {

    @Test
    void shouldCreateRandomDiceWithDefaultTwoDice() {
      DiceFactory factory = new RandomDiceFactory();

      Dice dice = factory.create();

      assertTrue(dice instanceof RandomDice);
      assertEquals(2, ((RandomDice) dice).getDiceCount());
    }
  }

  @Nested
  class CustomConstructor {

    @Test
    void shouldCreateRandomDiceWithSpecifiedNumberOfDice() {
      int expectedDice = 4;
      DiceFactory factory = new RandomDiceFactory(expectedDice);

      Dice dice = factory.create();

      assertTrue(dice instanceof RandomDice);
      assertEquals(expectedDice, ((RandomDice) dice).getDiceCount());
    }

    @Test
    void shouldHandleZeroDiceGracefully() {
      DiceFactory factory = new RandomDiceFactory(0);

      Dice dice = factory.create();

      assertTrue(dice instanceof RandomDice);
      assertEquals(0, ((RandomDice) dice).getDiceCount()); // eller forvent en exception?
    }

    @Test
    void shouldHandleNegativeDiceGracefully() {
      DiceFactory factory = new RandomDiceFactory(-1);

      Dice dice = factory.create();

      assertTrue(dice instanceof RandomDice);
      assertEquals(-1, ((RandomDice) dice).getDiceCount()); // evt. kast unntak?
    }
  }
}
