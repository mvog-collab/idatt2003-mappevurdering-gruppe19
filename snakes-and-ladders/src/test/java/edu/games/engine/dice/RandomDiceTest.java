package edu.games.engine.dice;

import edu.games.engine.exception.ValidationException;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RandomDiceTest {

  @Nested
  class SingleParameterConstructor {

    @Test
    void shouldCreateDiceWithValidNumberOfDice() {
      RandomDice dice = new RandomDice(2);

      assertEquals(2, dice.getDiceCount());
    }

    @Test
    void shouldCreateDiceWithOnedie() {
      RandomDice dice = new RandomDice(1);

      assertEquals(1, dice.getDiceCount());
    }

    @Test
    void shouldCreateDiceWithManyDice() {
      RandomDice dice = new RandomDice(10);

      assertEquals(10, dice.getDiceCount());
    }

    @Test
    void shouldThrowExceptionWhenNumberOfDiceIsZero() {
      assertThrows(ValidationException.class, () -> new RandomDice(0));
    }

    @Test
    void shouldThrowExceptionWhenNumberOfDiceIsNegative() {
      assertThrows(ValidationException.class, () -> new RandomDice(-1));
      assertThrows(ValidationException.class, () -> new RandomDice(-5));
    }

    @Test
    void shouldThrowExceptionWithCorrectMessageForInvalidDiceCount() {
      ValidationException exception = assertThrows(ValidationException.class, () -> new RandomDice(0));
      assertEquals("Invalid number of dice: must be greater than 0", exception.getMessage());
    }
  }

  @Nested
  class TwoParameterConstructor {

    @Test
    void shouldCreateDiceWithValidParameters() {
      Random mockRandom = mock(Random.class);
      RandomDice dice = new RandomDice(3, mockRandom);

      assertEquals(3, dice.getDiceCount());
    }

    @Test
    void shouldThrowExceptionWhenNumberOfDiceIsInvalid() {
      Random mockRandom = mock(Random.class);

      assertThrows(ValidationException.class, () -> new RandomDice(0, mockRandom));
      assertThrows(ValidationException.class, () -> new RandomDice(-1, mockRandom));
    }

    @Test
    void shouldThrowExceptionWhenRandomIsNull() {
      ValidationException exception = assertThrows(ValidationException.class, () -> new RandomDice(2, null));

      assertEquals("Invalid rnd: must not be null", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWithCorrectMessageForNullRandom() {
      ValidationException exception = assertThrows(ValidationException.class, () -> new RandomDice(1, null));

      assertEquals("Invalid rnd: must not be null", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionForInvalidDiceCountEvenWithValidRandom() {
      Random mockRandom = mock(Random.class);

      ValidationException exception = assertThrows(ValidationException.class, () -> new RandomDice(-2, mockRandom));

      assertEquals("Invalid number of dice: must be greater than 0", exception.getMessage());
    }

    @Test
    void shouldCreateDiceWithRealRandom() {
      Random realRandom = new Random(12345); // Seeded for predictability

      assertDoesNotThrow(() -> new RandomDice(2, realRandom));
    }
  }

  @Nested
  class RollBehavior {

    @Test
    void shouldRollSingleDie() {
      Random mockRandom = mock(Random.class);
      when(mockRandom.nextInt(1, 7)).thenReturn(4);

      RandomDice dice = new RandomDice(1, mockRandom);

      int result = dice.roll();

      assertEquals(4, result);
      verify(mockRandom).nextInt(1, 7);
    }

    @Test
    void shouldRollMultipleDiceAndSumResults() {
      Random mockRandom = mock(Random.class);
      when(mockRandom.nextInt(1, 7)).thenReturn(3, 5); // First call returns 3, second returns 5

      RandomDice dice = new RandomDice(2, mockRandom);

      int result = dice.roll();

      assertEquals(8, result); // 3 + 5 = 8
      verify(mockRandom, times(2)).nextInt(1, 7);
    }

    @Test
    void shouldRollCorrectNumberOfDice() {
      Random mockRandom = mock(Random.class);
      when(mockRandom.nextInt(1, 7)).thenReturn(1, 2, 3, 4, 5);

      RandomDice dice = new RandomDice(5, mockRandom);

      int result = dice.roll();

      assertEquals(15, result); // 1+2+3+4+5 = 15
      verify(mockRandom, times(5)).nextInt(1, 7);
    }

    @Test
    void shouldClearPreviousRollValues() {
      Random mockRandom = mock(Random.class);
      when(mockRandom.nextInt(1, 7)).thenReturn(6, 1);

      RandomDice dice = new RandomDice(1, mockRandom);

      dice.roll(); // First roll
      List<Integer> firstRoll = dice.lastValues();

      dice.roll(); // Second roll should clear first roll
      List<Integer> secondRoll = dice.lastValues();

      assertEquals(List.of(6), firstRoll);
      assertEquals(List.of(1), secondRoll);
    }

    @Test
    void shouldHandleRollingWithDefaultRandom() {
      RandomDice dice = new RandomDice(2);

      int result = dice.roll();

      // Result should be between 2 and 12 (2 dice, each 1-6)
      assertTrue(result >= 2 && result <= 12);
    }

    @Test
    void shouldProduceRepeatedRollsWithRealRandom() {
      RandomDice dice = new RandomDice(1);

      int roll1 = dice.roll();
      int roll2 = dice.roll();

      // Both should be valid die values
      assertTrue(roll1 >= 1 && roll1 <= 6);
      assertTrue(roll2 >= 1 && roll2 <= 6);
      // Results might be the same or different - that's fine for random
    }
  }

  @Nested
  class LastValuesBehavior {

    @Test
    void shouldReturnEmptyListBeforeFirstRoll() {
      Random mockRandom = mock(Random.class);
      RandomDice dice = new RandomDice(2, mockRandom);

      List<Integer> values = dice.lastValues();

      assertTrue(values.isEmpty());
    }

    @Test
    void shouldReturnLastRolledValues() {
      Random mockRandom = mock(Random.class);
      when(mockRandom.nextInt(1, 7)).thenReturn(2, 4, 6);

      RandomDice dice = new RandomDice(3, mockRandom);
      dice.roll();

      List<Integer> values = dice.lastValues();

      assertEquals(List.of(2, 4, 6), values);
    }

    @Test
    void shouldReturnCopyOfLastValues() {
      Random mockRandom = mock(Random.class);
      when(mockRandom.nextInt(1, 7)).thenReturn(3);

      RandomDice dice = new RandomDice(1, mockRandom);
      dice.roll();

      List<Integer> values = dice.lastValues();

      // Should not be able to modify the returned list
      assertThrows(UnsupportedOperationException.class, () -> values.add(7));
    }

    @Test
    void shouldReturnCorrectValuesAfterMultipleRolls() {
      Random mockRandom = mock(Random.class);
      when(mockRandom.nextInt(1, 7)).thenReturn(1, 2, 3, 4);

      RandomDice dice = new RandomDice(2, mockRandom);

      dice.roll(); // Should use values 1, 2
      assertEquals(List.of(1, 2), dice.lastValues());

      dice.roll(); // Should use values 3, 4 and clear previous
      assertEquals(List.of(3, 4), dice.lastValues());
    }

    @Test
    void shouldHandleSingleDieLastValues() {
      Random mockRandom = mock(Random.class);
      when(mockRandom.nextInt(1, 7)).thenReturn(5);

      RandomDice dice = new RandomDice(1, mockRandom);
      dice.roll();

      List<Integer> values = dice.lastValues();

      assertEquals(List.of(5), values);
    }
  }

  @Nested
  class DiceCountBehavior {

    @Test
    void shouldReturnCorrectDiceCount() {
      for (int count = 1; count <= 10; count++) {
        RandomDice dice = new RandomDice(count);
        assertEquals(count, dice.getDiceCount());
      }
    }

    @Test
    void shouldReturnConsistentDiceCount() {
      RandomDice dice = new RandomDice(4);

      // Should return same count multiple times
      assertEquals(4, dice.getDiceCount());
      assertEquals(4, dice.getDiceCount());

      // Even after rolling
      dice.roll();
      assertEquals(4, dice.getDiceCount());
    }

    @Test
    void shouldReturnCorrectDiceCountWithCustomRandom() {
      Random mockRandom = mock(Random.class);
      RandomDice dice = new RandomDice(7, mockRandom);

      assertEquals(7, dice.getDiceCount());
    }
  }

  @Nested
  class EdgeCasesAndIntegration {

    @Test
    void shouldHandleLargeDiceCount() {
      Random mockRandom = mock(Random.class);
      when(mockRandom.nextInt(1, 7)).thenReturn(1); // All dice return 1

      RandomDice dice = new RandomDice(100, mockRandom);

      int result = dice.roll();

      assertEquals(100, result); // 100 dice * 1 = 100
      assertEquals(100, dice.lastValues().size());
      verify(mockRandom, times(100)).nextInt(1, 7);
    }

    @Test
    void shouldHandleMaximumDiceValues() {
      Random mockRandom = mock(Random.class);
      when(mockRandom.nextInt(1, 7)).thenReturn(6); // All dice return 6

      RandomDice dice = new RandomDice(3, mockRandom);

      int result = dice.roll();

      assertEquals(18, result); // 3 dice * 6 = 18
      assertEquals(List.of(6, 6, 6), dice.lastValues());
    }

    @Test
    void shouldHandleMinimumDiceValues() {
      Random mockRandom = mock(Random.class);
      when(mockRandom.nextInt(1, 7)).thenReturn(1); // All dice return 1

      RandomDice dice = new RandomDice(2, mockRandom);

      int result = dice.roll();

      assertEquals(2, result); // 2 dice * 1 = 2
      assertEquals(List.of(1, 1), dice.lastValues());
    }

    @Test
    void shouldWorkWithSeededRandom() {
      Random seededRandom = new Random(42); // Fixed seed for predictable results
      RandomDice dice = new RandomDice(2, seededRandom);

      int result1 = dice.roll();

      // Reset with same seed
      seededRandom = new Random(42);
      dice = new RandomDice(2, seededRandom);
      int result2 = dice.roll();

      assertEquals(result1, result2); // Should be same with same seed
    }

    @Test
    void shouldMaintainIndependentState() {
      Random mockRandom1 = mock(Random.class);
      Random mockRandom2 = mock(Random.class);
      when(mockRandom1.nextInt(1, 7)).thenReturn(2);
      when(mockRandom2.nextInt(1, 7)).thenReturn(5);

      RandomDice dice1 = new RandomDice(1, mockRandom1);
      RandomDice dice2 = new RandomDice(1, mockRandom2);

      dice1.roll();
      dice2.roll();

      assertEquals(List.of(2), dice1.lastValues());
      assertEquals(List.of(5), dice2.lastValues());
    }

    @Test
    void shouldHandleRepeatedMethodCalls() {
      RandomDice dice = new RandomDice(1);

      // Multiple calls to each method should work
      for (int i = 0; i < 5; i++) {
        dice.roll();
        dice.lastValues();
        assertEquals(1, dice.getDiceCount());
      }
    }
  }
}