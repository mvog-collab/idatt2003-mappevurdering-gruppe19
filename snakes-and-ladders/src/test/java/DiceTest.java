import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class DiceTest {

    private Dice testDice;

    @BeforeEach
    void setUp() {
        testDice = new Dice();
    }

    @Test
    void testCreateConstructorShouldInitializeCorrectly() {
        assertNotNull(testDice);
        assertNotNull(testDice.getDice());
    }

    @Test
    void testRollDiceShouldRollWithinExpectedRange() {
        assertTrue(testDice.rollDice() < 12);
        assertTrue(testDice.rollDice() > 2);
    }

    @Test
    void testIsPairShouldReturnTrueIfPairIsRolledFalseIfNoPair() {
        testDice.getDice().get(0).setLastRolledValue(5);
        testDice.getDice().get(1).setLastRolledValue(5);
        assertTrue(testDice.isPair());

        testDice.getDice().get(0).setLastRolledValue(1);
        testDice.getDice().get(1).setLastRolledValue(5);
        assertFalse(testDice.isPair());
    }

    @Test
    void testIsPairOfSixShouldReturnTrueAndFalseAsExpected() {
        testDice.getDice().get(0).setLastRolledValue(6);
        testDice.getDice().get(1).setLastRolledValue(6);
        assertTrue(testDice.isPairOfSix());

        testDice.getDice().get(0).setLastRolledValue(5);
        testDice.getDice().get(1).setLastRolledValue(5);
        assertFalse(testDice.isPairOfSix());
    }

    @Test
    void testGetDiceShouldReturnDiceAsExpected() {
        assertNotNull(testDice.getDice());
        assertFalse(testDice.getDice().isEmpty());
    }
}
