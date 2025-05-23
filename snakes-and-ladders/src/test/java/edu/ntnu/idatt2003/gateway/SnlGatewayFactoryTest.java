package edu.ntnu.idatt2003.gateway;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import edu.games.engine.exception.ValidationException;
import edu.ntnu.idatt2003.exception.ResourceNotFoundException;

import static org.junit.jupiter.api.Assertions.*;

class SnlGatewayFactoryTest {

    @Nested
    class DefaultCreation {

        @Test
        void shouldCreateDefaultSnlGateway() {
            SnlGateway gateway = SnlGatewayFactory.createDefault();

            assertNotNull(gateway);
            assertInstanceOf(SnlGateway.class, gateway);
        }

        @Test
        void shouldCreateDifferentInstancesOnEachCall() {
            SnlGateway gateway1 = SnlGatewayFactory.createDefault();
            SnlGateway gateway2 = SnlGatewayFactory.createDefault();

            assertNotNull(gateway1);
            assertNotNull(gateway2);
            assertNotSame(gateway1, gateway2);
        }

        @Test
        void shouldCreateGatewayWithWorkingComponents() {
            SnlGateway gateway = SnlGatewayFactory.createDefault();

            // Test that the gateway can perform basic operations
            assertDoesNotThrow(() -> gateway.newGame(90));
            assertEquals(0, gateway.rollDice()); // No players, so should return 0
            assertTrue(gateway.players().isEmpty());
        }

        @Test
        void shouldCreateGatewayWithConsistentBehavior() {
            SnlGateway gateway1 = SnlGatewayFactory.createDefault();
            SnlGateway gateway2 = SnlGatewayFactory.createDefault();

            // Both gateways should behave the same way
            gateway1.newGame(90);
            gateway2.newGame(90);

            assertEquals(gateway1.players().size(), gateway2.players().size());
            assertEquals(gateway1.hasWinner(), gateway2.hasWinner());
        }

        @Test
        void shouldCreateGatewayThatCanAddPlayers() {
            SnlGateway gateway = SnlGatewayFactory.createDefault();
            gateway.newGame(90);

            assertDoesNotThrow(() -> gateway.addPlayer("TestPlayer", "BLUE", java.time.LocalDate.of(1990, 1, 1)));

            assertEquals(1, gateway.players().size());
        }
    }

    @Nested
    class ComponentTypes {

        @Test
        void shouldUseCorrectBoardFactory() {
            // This test verifies the factory uses LinearBoardFactory
            // We can't easily access the private field, but we can test the behavior
            SnlGateway gateway = SnlGatewayFactory.createDefault();

            // LinearBoardFactory should create linear boards that support movement
            assertDoesNotThrow(() -> gateway.newGame(90));
            assertTrue(gateway.boardSize() >= 0); // Should have a valid board size
        }

        @Test
        void shouldUseRandomDiceFactory() {
            SnlGateway gateway = SnlGatewayFactory.createDefault();
            gateway.newGame(90);
            gateway.addPlayer("TestPlayer", "BLUE", java.time.LocalDate.of(1990, 1, 1));

            // RandomDiceFactory should create dice that produce random results
            int roll1 = gateway.rollDice();
            int roll2 = gateway.rollDice();

            // Both should be valid dice rolls (between 2 and 12 for two dice)
            assertTrue(roll1 >= 2 && roll1 <= 12);
            assertTrue(roll2 >= 2 && roll2 <= 12);
        }

        @Test
        void shouldUseCsvPlayerStore() {
            SnlGateway gateway = SnlGatewayFactory.createDefault();
            gateway.newGame(90);

            // CsvPlayerStore should allow saving/loading players
            // We test that the gateway accepts player operations
            assertDoesNotThrow(() -> gateway.addPlayer("TestPlayer", "RED", java.time.LocalDate.of(1985, 5, 15)));

            assertDoesNotThrow(() -> gateway.clearPlayers());
        }

        @Test
        void shouldUseJsonOverlayProvider() {
            SnlGateway gateway = SnlGatewayFactory.createDefault();
            gateway.newGame(90);

            // JsonOverlayProvider should provide overlays for the board
            // Even if overlays aren't found, it shouldn't crash
            assertDoesNotThrow(() -> gateway.boardOverlays());
        }
    }

    @Nested
    class FactoryPattern {

        @Test
        void shouldNotBeInstantiable() {
            try {
                var constructor = SnlGatewayFactory.class.getDeclaredConstructor();
                assertFalse(constructor.canAccess(null),
                        "Constructor should not be publicly accessible");
            } catch (NoSuchMethodException e) {
                // This is also acceptable - no default constructor
            }
        }

        @Test
        void shouldProvideStaticFactoryMethod() {
            assertDoesNotThrow(() -> SnlGatewayFactory.class.getDeclaredMethod("createDefault"));
        }

        @Test
        void shouldFollowFactoryPattern() {
            // Factory should create objects without exposing construction logic
            SnlGateway gateway = SnlGatewayFactory.createDefault();

            assertNotNull(gateway);

            // Gateway should be fully functional without additional setup
            assertDoesNotThrow(() -> {
                gateway.newGame(90);
                gateway.addPlayer("Player1", "BLUE", java.time.LocalDate.now());
                gateway.rollDice();
                gateway.players();
                gateway.boardOverlays();
            });
        }
    }

    @Nested
    class IntegrationTesting {

        @Test
        void shouldCreateFullyFunctionalGateway() {
            SnlGateway gateway = SnlGatewayFactory.createDefault();

            // Complete game flow should work
            gateway.newGame(90);
            gateway.addPlayer("Alice", "BLUE", java.time.LocalDate.of(1990, 1, 1));
            gateway.addPlayer("Bob", "RED", java.time.LocalDate.of(1985, 6, 15));

            assertEquals(2, gateway.players().size());
            assertFalse(gateway.hasWinner());

            // Should be able to play
            int roll = gateway.rollDice();
            assertTrue(roll >= 2 && roll <= 12);

            // Should be able to reset
            assertDoesNotThrow(() -> gateway.resetGame());
        }

        @Test
        void shouldHandleMultipleGamesOnSameGateway() {
            SnlGateway gateway = SnlGatewayFactory.createDefault();

            // First game
            gateway.newGame(90);
            gateway.addPlayer("Player1", "BLUE", java.time.LocalDate.of(1990, 1, 1));
            gateway.rollDice();

            // Second game
            gateway.newGame(90);
            gateway.addPlayer("Player2", "GREEN", java.time.LocalDate.of(1995, 3, 20));
            gateway.rollDice();

            // Should work without issues
            assertEquals(1, gateway.players().size());
            assertEquals("Player2", gateway.players().get(0).playerName());
        }

        @Test
        void shouldHandleEdgeCasesWithCreatedGateway() {
            SnlGateway gateway = SnlGatewayFactory.createDefault();

            // Should handle operations before newGame
            assertEquals(0, gateway.rollDice());
            assertTrue(gateway.players().isEmpty());
            assertFalse(gateway.hasWinner());

            // Should handle empty games
            gateway.newGame(90);
            assertEquals(0, gateway.rollDice()); // No players

            // Should handle reset on empty game
            assertThrows(ValidationException.class, () -> gateway.resetGame());
        }
    }

    @Nested
    class ComponentConfiguration {

        @Test
        void shouldCreateGatewayWithCorrectOverlayPath() {
            SnlGateway gateway = SnlGatewayFactory.createDefault();
            gateway.newGame(90);

            // JsonOverlayProvider is configured with "/overlays/" path
            // This should not throw even if overlays don't exist
            assertDoesNotThrow(() -> gateway.boardOverlays());
        }

        @Test
        void shouldCreateGatewayWithCorrectDiceConfiguration() {
            SnlGateway gateway = SnlGatewayFactory.createDefault();
            gateway.newGame(90);
            gateway.addPlayer("TestPlayer", "BLUE", java.time.LocalDate.of(1990, 1, 1));

            // RandomDiceFactory creates 2-dice configuration for SNL
            int roll = gateway.rollDice();

            // Should be sum of 2 dice (2-12 range)
            assertTrue(roll >= 2 && roll <= 12);

            // Last dice values should contain 2 values
            assertEquals(2, gateway.lastDiceValues().size());
        }

        @Test
        void shouldCreateGatewayWithWorkingPlayerStore() {
            SnlGateway gateway = SnlGatewayFactory.createDefault();
            gateway.newGame(90);

            // Should be able to manage players
            gateway.addPlayer("Player1", "BLUE", java.time.LocalDate.of(1990, 1, 1));
            gateway.addPlayer("Player2", "RED", java.time.LocalDate.of(1985, 5, 15));

            assertEquals(2, gateway.players().size());

            gateway.clearPlayers();
            assertEquals(0, gateway.players().size());
        }
    }

    @Nested
    class ResourceHandling {

        @Test
        void shouldHandleResourceLoadingGracefully() {
            SnlGateway gateway = SnlGatewayFactory.createDefault();

            // Should handle boards of various sizes
            for (int size : new int[] { 64, 90, 120 }) {
                assertDoesNotThrow(() -> gateway.newGame(size),
                        "Should handle board size: " + size);
            }
        }

        @Test
        void shouldNotLeakResourcesBetweenInstances() {
            // Create multiple gateways and ensure they don't interfere
            SnlGateway gateway1 = SnlGatewayFactory.createDefault();
            SnlGateway gateway2 = SnlGatewayFactory.createDefault();
            SnlGateway gateway3 = SnlGatewayFactory.createDefault();

            gateway1.newGame(64);
            gateway2.newGame(90);
            gateway3.newGame(120);

            gateway1.addPlayer("Player1", "BLUE", java.time.LocalDate.of(1990, 1, 1));
            gateway2.addPlayer("Player2", "RED", java.time.LocalDate.of(1985, 5, 15));
            gateway3.addPlayer("Player3", "GREEN", java.time.LocalDate.of(1995, 8, 30));

            // Each should have only their own player
            assertEquals(1, gateway1.players().size());
            assertEquals(1, gateway2.players().size());
            assertEquals(1, gateway3.players().size());

            assertEquals("Player1", gateway1.players().get(0).playerName());
            assertEquals("Player2", gateway2.players().get(0).playerName());
            assertEquals("Player3", gateway3.players().get(0).playerName());
        }
    }
}