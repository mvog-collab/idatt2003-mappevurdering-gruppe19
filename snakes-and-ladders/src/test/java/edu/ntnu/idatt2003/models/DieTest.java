package edu.ntnu.idatt2003.models;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("Test class for the Die class")
public class DieTest {

  private Die die;

  @BeforeEach
  void setUp() {
    die = new Die();
  }

  @Nested
  @DisplayName("Tests for die constructor")
  class constructorTests {

    @Test
    @DisplayName("The constructor should not initialize attributes")
    void testConstructorIsNull() {
      assertNotNull(die);
      assertEquals(0, die.getLastRolledValue());
    }
  }

  @Nested
  @DisplayName("Tests for rollDie method")
  class rollDieMethodTests {

    @Test
    @DisplayName("rollDie should return values between 1 and 6")
    void testRollDieWithinRange() {
      for (int i = 0; i < 10; i++) {
        int roll = die.rollDie();
        assertTrue(roll >= 1 && roll <= 6);
      }
    }

    @Test
    @DisplayName("rollDie should update lastRolledValue correctly")
    void testLastRolledValueUpdated() {
      int roll = die.rollDie();
      assertEquals(roll, die.getLastRolledValue());
    }
  }

 @Nested
  @DisplayName("Tests for getLastRolledValue method")
  class GetLastRolledValueTests {

    @Test
    @DisplayName("getLastRolledValue should return the correct stored value")
    void testGetLastRolledValue() {
      die.setLastRolledValue(4);
      assertEquals(4, die.getLastRolledValue());
    }

    @Test
    @DisplayName("Default lastRolledValue should be 0 when created")
    void testDefaultLastRolledValue() {
      assertEquals(0, die.getLastRolledValue());
    }
  }

  @Nested
  @DisplayName("Tests for setLastRolledValue method")
  class SetLastRolledValueTests {

    @Test
    @DisplayName("setLastRolledValue should correctly set a valid value")
    void testSetValidLastRolledValue() {
      die.setLastRolledValue(5);
      assertEquals(5, die.getLastRolledValue());
    }

    @Test
    @DisplayName("setLastRolledValue should throw exception for values out of range")
    void testSetInvalidValue() {
      assertThrows(IllegalArgumentException.class, () -> die.setLastRolledValue(0));
    }
  }
}


