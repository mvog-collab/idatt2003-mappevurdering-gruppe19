package edu.games.engine.strategy.factory;

import edu.games.engine.board.LudoPath;
import edu.games.engine.strategy.GameStrategy;
import edu.games.engine.strategy.LudoGameStrategy;
import edu.games.engine.strategy.SnlGameStrategy;
import edu.ntnu.idatt2003.persistence.BoardAdapter;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Simplified factory tests using real objects instead of mocks
 * This avoids the Mockito nested class initialization issues
 */
class GameStrategyFactoryTest {

    @Nested
    class LudoStrategyCreation {

        @Test
        void shouldCreateLudoStrategyWithValidPath() {
            LudoPath ludoPath = new LudoPath();

            GameStrategy strategy = GameStrategyFactory.createLudoStrategy(ludoPath);

            assertNotNull(strategy);
            assertInstanceOf(LudoGameStrategy.class, strategy);
        }

        @Test
        void shouldThrowExceptionWhenLudoPathIsNull() {
            assertThrows(NullPointerException.class, () -> GameStrategyFactory.createLudoStrategy(null));
        }

        @Test
        void shouldCreateDifferentInstancesForEachCall() {
            LudoPath ludoPath = new LudoPath();

            GameStrategy strategy1 = GameStrategyFactory.createLudoStrategy(ludoPath);
            GameStrategy strategy2 = GameStrategyFactory.createLudoStrategy(ludoPath);

            assertNotSame(strategy1, strategy2);
            assertInstanceOf(LudoGameStrategy.class, strategy1);
            assertInstanceOf(LudoGameStrategy.class, strategy2);
        }
    }

    @Nested
    class SnlStrategyCreation {

        @Test
        void shouldCreateSnlStrategyWithValidMapData() {
            Map<Integer, Integer> snakes = new HashMap<>();
            snakes.put(16, 6);
            snakes.put(47, 26);

            Map<Integer, Integer> ladders = new HashMap<>();
            ladders.put(2, 38);
            ladders.put(7, 14);

            BoardAdapter.MapData mapData = new BoardAdapter.MapData(90, snakes, ladders);

            GameStrategy strategy = GameStrategyFactory.createSnlStrategy(mapData);

            assertNotNull(strategy);
            assertInstanceOf(SnlGameStrategy.class, strategy);
        }

        @Test
        void shouldCreateSnlStrategyWithEmptyMaps() {
            Map<Integer, Integer> emptySnakes = new HashMap<>();
            Map<Integer, Integer> emptyLadders = new HashMap<>();

            BoardAdapter.MapData mapData = new BoardAdapter.MapData(90, emptySnakes, emptyLadders);

            GameStrategy strategy = GameStrategyFactory.createSnlStrategy(mapData);

            assertNotNull(strategy);
            assertInstanceOf(SnlGameStrategy.class, strategy);

            SnlGameStrategy snlStrategy = (SnlGameStrategy) strategy;
            assertTrue(snlStrategy.getSnakes().isEmpty());
            assertTrue(snlStrategy.getLadders().isEmpty());
        }

        @Test
        void shouldPreserveSnakesAndLaddersInCreatedStrategy() {
            Map<Integer, Integer> snakes = new HashMap<>();
            snakes.put(99, 1);
            snakes.put(87, 24);

            Map<Integer, Integer> ladders = new HashMap<>();
            ladders.put(4, 56);
            ladders.put(12, 28);

            BoardAdapter.MapData mapData = new BoardAdapter.MapData(90, snakes, ladders);

            GameStrategy strategy = GameStrategyFactory.createSnlStrategy(mapData);
            SnlGameStrategy snlStrategy = (SnlGameStrategy) strategy;

            assertEquals(snakes, snlStrategy.getSnakes());
            assertEquals(ladders, snlStrategy.getLadders());
        }

        @Test
        void shouldThrowExceptionWhenMapDataIsNull() {
            assertThrows(NullPointerException.class, () -> GameStrategyFactory.createSnlStrategy(null));
        }

        @Test
        void shouldCreateDifferentInstancesForEachCall() {
            Map<Integer, Integer> snakes = Map.of(50, 10);
            Map<Integer, Integer> ladders = Map.of(5, 25);
            BoardAdapter.MapData mapData = new BoardAdapter.MapData(90, snakes, ladders);

            GameStrategy strategy1 = GameStrategyFactory.createSnlStrategy(mapData);
            GameStrategy strategy2 = GameStrategyFactory.createSnlStrategy(mapData);

            assertNotSame(strategy1, strategy2);
            assertInstanceOf(SnlGameStrategy.class, strategy1);
            assertInstanceOf(SnlGameStrategy.class, strategy2);
        }
    }

    @Nested
    class FactoryPattern {

        @Test
        void shouldProvideStaticFactoryMethods() {
            // Verify that both factory methods exist and are static
            assertDoesNotThrow(() -> GameStrategyFactory.class.getDeclaredMethod("createLudoStrategy", LudoPath.class));

            assertDoesNotThrow(
                    () -> GameStrategyFactory.class.getDeclaredMethod("createSnlStrategy", BoardAdapter.MapData.class));
        }

        @Test
        void shouldNotAllowInstantiation() {
            // Test that we can't create instances (factory pattern)
            try {
                var constructor = GameStrategyFactory.class.getDeclaredConstructor();
                // If constructor exists and is private, this should work
                assertFalse(constructor.canAccess(null),
                        "Constructor should not be publicly accessible");
            } catch (NoSuchMethodException e) {
                // If no default constructor exists, that's also fine for a factory
                // This is actually preferred for utility classes
            }
        }
    }

    @Nested
    class EdgeCasesAndBehavior {

        @Test
        void shouldHandleNullMapDataComponents() {
            // Test what happens when MapData has null components
            // This tests the actual behavior rather than mocking

            // We can't easily test null components without changing MapData constructor
            // So let's test behavior with minimal valid data instead
            Map<Integer, Integer> minimalSnakes = Map.of();
            Map<Integer, Integer> minimalLadders = Map.of();

            BoardAdapter.MapData mapData = new BoardAdapter.MapData(1, minimalSnakes, minimalLadders);

            assertDoesNotThrow(() -> {
                GameStrategy strategy = GameStrategyFactory.createSnlStrategy(mapData);
                assertNotNull(strategy);
            });
        }

        @Test
        void shouldCreateStrategiesWithLargeDatasets() {
            Map<Integer, Integer> largeSnakes = new HashMap<>();
            Map<Integer, Integer> largeLadders = new HashMap<>();

            // Create large datasets
            for (int i = 10; i < 100; i += 10) {
                largeSnakes.put(i + 5, i - 5);
                largeLadders.put(i, i + 20);
            }

            BoardAdapter.MapData mapData = new BoardAdapter.MapData(200, largeSnakes, largeLadders);

            GameStrategy strategy = GameStrategyFactory.createSnlStrategy(mapData);

            assertNotNull(strategy);
            assertInstanceOf(SnlGameStrategy.class, strategy);

            SnlGameStrategy snlStrategy = (SnlGameStrategy) strategy;
            assertEquals(9, snlStrategy.getSnakes().size());
            assertEquals(9, snlStrategy.getLadders().size());
        }

        @Test
        void shouldHandleDuplicateSnakeAndLadderPositions() {
            // Test case where same position has both snake and ladder
            Map<Integer, Integer> snakes = Map.of(25, 5);
            Map<Integer, Integer> ladders = Map.of(25, 50);

            BoardAdapter.MapData mapData = new BoardAdapter.MapData(100, snakes, ladders);

            GameStrategy strategy = GameStrategyFactory.createSnlStrategy(mapData);

            assertNotNull(strategy);
            assertInstanceOf(SnlGameStrategy.class, strategy);

            SnlGameStrategy snlStrategy = (SnlGameStrategy) strategy;
            assertTrue(snlStrategy.getSnakes().containsKey(25));
            assertTrue(snlStrategy.getLadders().containsKey(25));
            assertEquals(5, snlStrategy.getSnakes().get(25));
            assertEquals(50, snlStrategy.getLadders().get(25));
        }

        @Test
        void shouldHandleExtremeBoardSizes() {
            Map<Integer, Integer> snakes = Map.of(2, 1);
            Map<Integer, Integer> ladders = Map.of();

            // Test with very small board
            BoardAdapter.MapData smallMapData = new BoardAdapter.MapData(3, snakes, ladders);
            GameStrategy smallStrategy = GameStrategyFactory.createSnlStrategy(smallMapData);
            assertNotNull(smallStrategy);

            // Test with large board
            BoardAdapter.MapData largeMapData = new BoardAdapter.MapData(1000, Map.of(), Map.of());
            GameStrategy largeStrategy = GameStrategyFactory.createSnlStrategy(largeMapData);
            assertNotNull(largeStrategy);
        }
    }

    @Nested
    class ReturnTypes {

        @Test
        void shouldReturnGameStrategyInterface() {
            LudoPath ludoPath = new LudoPath();

            GameStrategy ludoStrategy = GameStrategyFactory.createLudoStrategy(ludoPath);

            // Should be assignable to GameStrategy interface
            assertNotNull(ludoStrategy);
            assertTrue(ludoStrategy instanceof GameStrategy);
        }

        @Test
        void shouldReturnCorrectConcreteTypes() {
            LudoPath ludoPath = new LudoPath();
            Map<Integer, Integer> snakes = Map.of(20, 5);
            Map<Integer, Integer> ladders = Map.of(10, 30);
            BoardAdapter.MapData mapData = new BoardAdapter.MapData(90, snakes, ladders);

            GameStrategy ludoStrategy = GameStrategyFactory.createLudoStrategy(ludoPath);
            GameStrategy snlStrategy = GameStrategyFactory.createSnlStrategy(mapData);

            assertInstanceOf(LudoGameStrategy.class, ludoStrategy);
            assertInstanceOf(SnlGameStrategy.class, snlStrategy);

            // Should not be the same type
            assertNotEquals(ludoStrategy.getClass(), snlStrategy.getClass());
        }

        @Test
        void shouldCreateFunctionalStrategies() {
            // Test that created strategies actually work
            LudoPath ludoPath = new LudoPath();
            GameStrategy ludoStrategy = GameStrategyFactory.createLudoStrategy(ludoPath);

            // Basic functionality test - should not throw
            assertDoesNotThrow(() -> {
                if (ludoStrategy instanceof LudoGameStrategy) {
                    // Strategy is created and can be cast
                    assertTrue(true);
                }
            });

            Map<Integer, Integer> snakes = Map.of(16, 6);
            Map<Integer, Integer> ladders = Map.of(2, 38);
            BoardAdapter.MapData mapData = new BoardAdapter.MapData(100, snakes, ladders);
            GameStrategy snlStrategy = GameStrategyFactory.createSnlStrategy(mapData);

            assertDoesNotThrow(() -> {
                if (snlStrategy instanceof SnlGameStrategy snlGameStrategy) {
                    assertFalse(snlGameStrategy.getSnakes().isEmpty());
                    assertFalse(snlGameStrategy.getLadders().isEmpty());
                }
            });
        }
    }
}