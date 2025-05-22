package edu.games.engine.dice.factory;

import edu.games.engine.dice.Dice;
import edu.games.engine.dice.RandomDice;
import edu.games.engine.exception.ValidationException;
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
    void shouldThrowValidationExceptionWhenZeroDice() {
      assertThrows(ValidationException.class,
          () -> new RandomDiceFactory(0).create());
    }

    @Test
    void shouldThrowValidationExceptionWhenNegativeDice() {
      assertThrows(ValidationException.class,
          () -> new RandomDiceFactory(-1).create());
    }
  }
}