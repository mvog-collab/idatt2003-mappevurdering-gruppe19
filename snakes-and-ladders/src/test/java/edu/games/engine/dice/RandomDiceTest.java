package edu.games.engine.dice;

import edu.games.engine.exception.ValidationException;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class RandomDiceTest {

  @Nested
  class Constructor {

    @Test
    void shouldThrowExceptionIfDiceLessThanOne() {
      ValidationException ex = assertThrows(
          ValidationException.class,
          () -> new RandomDice(0, new Random()));
      assertTrue(ex.getMessage().contains("dice"));
    }
  }

  @Nested
  class Rolling {

    @Test
    void shouldReturnSumWithinExpectedRangeWithOneDie() {
      RandomDice dice = new RandomDice(1, new Random());

      int result = dice.roll();

      assertTrue(result >= 1 && result <= 6);
    }

    @Test
    void shouldReturnCorrectNumberOfValuesInLastRoll() {
      RandomDice dice = new RandomDice(3, new Random());

      dice.roll();
      List<Integer> last = dice.lastValues();

      assertEquals(3, last.size());
      last.forEach(value -> assertTrue(value >= 1 && value <= 6));
    }

    @Test
    void lastValuesShouldReturnUnmodifiableCopy() {
      RandomDice dice = new RandomDice(2, new Random());

      dice.roll();
      List<Integer> values = dice.lastValues();

      assertThrows(UnsupportedOperationException.class, () -> values.add(42));
    }

    @Test
    void shouldBeDeterministicWithSeededRandom() {
      Random seededRandom = new Random(123);
      RandomDice dice = new RandomDice(2, seededRandom);

      int result1 = dice.roll();
      List<Integer> values1 = dice.lastValues();

      RandomDice dice2 = new RandomDice(2, new Random(123));
      int result2 = dice2.roll();
      List<Integer> values2 = dice2.lastValues();

      assertEquals(result1, result2);
      assertEquals(values1, values2);
    }
  }
}
