import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import models.Dice;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("Test class for the Dice class")
public class DiceTest {

    private Dice testDice;

    @BeforeEach
    void setUp() {
        testDice = new Dice();
    }

    @Nested
    @DisplayName("Tests for Dice constructor")
    class ConstructorTests {

        @Test
        @DisplayName("Constructor should initialize the dice list correctly")
        void testCreateConstructorShouldInitializeCorrectly() {
            assertNotNull(testDice);
            assertNotNull(testDice.getDice());
        }
    }

    @Nested
    @DisplayName("Tests for rollDice method")
    class RollDiceMethodTests {

        @Test
        @DisplayName("rollDice should return values between 2 and 12")
        void testRollDiceShouldRollWithinExpectedRange() {
            int rollDice = 0;
            for (int i = 0; i < 20; i++) {
                rollDice = testDice.rollDice();
                if (rollDice > 12) {
                    assertTrue(rollDice < 12);
                } else if (rollDice < 2) {
                    assertTrue(rollDice > 2);
                }
            }
            assertTrue(rollDice < 12);
            assertTrue(rollDice > 2);
        }
    }

    @Nested
    @DisplayName("Tests for isPair method")
    class IsPairMethodTests {

        @Test
        @DisplayName("isPair should return true if both dice have the same value")
        void testIsPairShouldReturnTrueIfPairIsRolledFalseIfNoPair() {
            testDice.getDice().get(0).setLastRolledValue(5);
            testDice.getDice().get(1).setLastRolledValue(5);
            assertTrue(testDice.isPair());

            testDice.getDice().get(0).setLastRolledValue(1);
            testDice.getDice().get(1).setLastRolledValue(5);
            assertFalse(testDice.isPair());
        }
    }

    @Nested
    @DisplayName("Tests for isPairOfSix method")
    class IsPairOfSixMethodTests {

        @Test
        @DisplayName("isPairOfSix should return true if both dice roll six")
        void testIsPairOfSixShouldReturnTrueAndFalseAsExpected() {
            testDice.getDice().get(0).setLastRolledValue(6);
            testDice.getDice().get(1).setLastRolledValue(6);
            assertTrue(testDice.isPairOfSix());

            testDice.getDice().get(0).setLastRolledValue(5);
            testDice.getDice().get(1).setLastRolledValue(5);
            assertFalse(testDice.isPairOfSix());
        }
    }

    @Nested
    @DisplayName("Tests for getDice method")
    class GetDiceMethodTests {

        @Test
        @DisplayName("getDice should return the initialized dice list")
        void testGetDiceShouldReturnDiceAsExpected() {
            assertNotNull(testDice.getDice());
            assertFalse(testDice.getDice().isEmpty());
        }
    }
}
