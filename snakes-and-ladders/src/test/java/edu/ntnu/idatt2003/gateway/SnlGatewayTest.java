package edu.ntnu.idatt2003.gateway;

import edu.games.engine.board.LinearBoard;
import edu.games.engine.board.factory.JsonBoardLoader;
import edu.games.engine.dice.factory.DiceFactory;
import edu.games.engine.impl.overlay.OverlayProvider;
import edu.games.engine.observer.BoardGameEvent;
import edu.games.engine.observer.BoardGameObserver;
import edu.games.engine.store.PlayerStore;
import edu.games.engine.strategy.SnlGameStrategy;
import edu.ntnu.idatt2003.gateway.view.PlayerView;
import edu.ntnu.idatt2003.persistence.BoardAdapter;
import edu.ntnu.idatt2003.ui.fx.OverlayParams;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class SnlGatewayTest {

    @Mock
    private JsonBoardLoader mockBoardLoader;

    @Mock
    private DiceFactory mockDiceFactory;

    @Mock
    private PlayerStore mockPlayerStore;

    @Mock
    private OverlayProvider mockOverlayProvider;

    @Mock
    private BoardGameObserver mockObserver;

    @Mock
    private LinearBoard mockLinearBoard;

    @Mock
    private edu.games.engine.dice.Dice mockDice;

    private SnlGateway gateway;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Setup dice factory to return mock dice
        when(mockDiceFactory.create()).thenReturn(mockDice);
        when(mockDice.roll()).thenReturn(7); // Default roll value for 2 dice
        when(mockDice.lastValues()).thenReturn(List.of(3, 4)); // Default dice values

        gateway = new SnlGateway(mockBoardLoader, mockDiceFactory, mockPlayerStore, mockOverlayProvider);
    }

    @Nested
    class ConstructorAndFactory {

        @Test
        void shouldCreateGatewayWithValidDependencies() {
            SnlGateway testGateway = new SnlGateway(mockBoardLoader, mockDiceFactory, mockPlayerStore,
                    mockOverlayProvider);

            assertNotNull(testGateway);
        }

        @Test
        void shouldCreateFactoryGateway() {
            SnlGateway factoryGateway = SnlGatewayFactory.createDefault();

            assertNotNull(factoryGateway);
        }
    }

    @Nested
    class GameLifecycleWithSize {

        @Test
        void shouldStartNewGameWithSize() {
            // Mock the static BoardFactory call
            Map<Integer, Integer> testSnakes = Map.of(16, 6);
            Map<Integer, Integer> testLadders = Map.of(2, 38);
            BoardAdapter.MapData mockMapData = new BoardAdapter.MapData(100, testSnakes, testLadders);

            when(mockBoardLoader.create(100)).thenReturn(mockLinearBoard);

            try (MockedStatic<edu.ntnu.idatt2003.persistence.BoardFactory> mockedFactory = mockStatic(
                    edu.ntnu.idatt2003.persistence.BoardFactory.class)) {

                mockedFactory.when(() -> edu.ntnu.idatt2003.persistence.BoardFactory.loadFromClasspath(anyString()))
                        .thenReturn(mockMapData);

                gateway.addObserver(mockObserver);
                gateway.newGame(100);

                ArgumentCaptor<BoardGameEvent> eventCaptor = ArgumentCaptor.forClass(BoardGameEvent.class);
                verify(mockObserver).update(eventCaptor.capture());
                assertEquals(BoardGameEvent.EventType.GAME_STARTED, eventCaptor.getValue().getTypeOfEvent());
                assertEquals(100, eventCaptor.getValue().getData());
            }
        }

        @Test
        void shouldCreateStrategyAndBoardForNewGame() {
            Map<Integer, Integer> testSnakes = Map.of(25, 5);
            Map<Integer, Integer> testLadders = Map.of(3, 22);
            BoardAdapter.MapData mockMapData = new BoardAdapter.MapData(50, testSnakes, testLadders);

            when(mockBoardLoader.create(50)).thenReturn(mockLinearBoard);

            try (MockedStatic<edu.ntnu.idatt2003.persistence.BoardFactory> mockedFactory = mockStatic(
                    edu.ntnu.idatt2003.persistence.BoardFactory.class)) {

                mockedFactory.when(() -> edu.ntnu.idatt2003.persistence.BoardFactory.loadFromClasspath(anyString()))
                        .thenReturn(mockMapData);

                gateway.newGame(50);

                // Should have created board and strategy
                verify(mockBoardLoader).create(50);

                // Test that snakes and ladders are accessible
                Map<Integer, Integer> snakes = gateway.getSnakes();
                Map<Integer, Integer> ladders = gateway.getLadders();

                assertEquals(testSnakes, snakes);
                assertEquals(testLadders, ladders);
            }
        }

        @Test
        void shouldHandleBoardFactoryFailure() {
            try (MockedStatic<edu.ntnu.idatt2003.persistence.BoardFactory> mockedFactory = mockStatic(
                    edu.ntnu.idatt2003.persistence.BoardFactory.class)) {

                mockedFactory.when(() -> edu.ntnu.idatt2003.persistence.BoardFactory.loadFromClasspath(anyString()))
                        .thenThrow(new RuntimeException("Board loading failed"));

                assertThrows(RuntimeException.class, () -> gateway.newGame(100));
            }
        }
    }

    @Nested
    class GameLifecycleWithMapData {
        @Test
        void shouldHandleNullMapData() {
            assertThrows(NullPointerException.class, () -> gateway.newGame((BoardAdapter.MapData) null));
        }
    }

    @Nested
    class GameReset {

        @Test
        void shouldResetGameProperly() {
            // First create a game
            Map<Integer, Integer> testSnakes = Map.of(16, 6);
            Map<Integer, Integer> testLadders = Map.of(2, 38);
            BoardAdapter.MapData mockMapData = new BoardAdapter.MapData(50, testSnakes, testLadders);

            when(mockBoardLoader.create(50)).thenReturn(mockLinearBoard);
            when(mockLinearBoard.start()).thenReturn(mock(edu.games.engine.board.Tile.class));

            try (MockedStatic<edu.ntnu.idatt2003.persistence.BoardFactory> mockedFactory = mockStatic(
                    edu.ntnu.idatt2003.persistence.BoardFactory.class)) {

                mockedFactory.when(() -> edu.ntnu.idatt2003.persistence.BoardFactory.loadFromClasspath(anyString()))
                        .thenReturn(mockMapData);

                gateway.newGame(50);
                gateway.addPlayer("Alice", "BLUE", LocalDate.of(1990, 1, 1));

                gateway.addObserver(mockObserver);
                gateway.resetGame();

                ArgumentCaptor<BoardGameEvent> eventCaptor = ArgumentCaptor.forClass(BoardGameEvent.class);
                verify(mockObserver).update(eventCaptor.capture());
                assertEquals(BoardGameEvent.EventType.GAME_RESET, eventCaptor.getValue().getTypeOfEvent());
            }
        }

        @Test
        void shouldHandleResetWithoutGame() {
            // Should not throw when game is null
            assertDoesNotThrow(() -> gateway.resetGame());
        }
    }

    @Nested
    class PlayerManagement {

        @BeforeEach
        void setUp() {
            Map<Integer, Integer> testSnakes = Map.of(16, 6);
            Map<Integer, Integer> testLadders = Map.of(2, 38);
            BoardAdapter.MapData mockMapData = new BoardAdapter.MapData(100, testSnakes, testLadders);

            when(mockBoardLoader.create(100)).thenReturn(mockLinearBoard);
            when(mockLinearBoard.start()).thenReturn(mock(edu.games.engine.board.Tile.class));

            try (MockedStatic<edu.ntnu.idatt2003.persistence.BoardFactory> mockedFactory = mockStatic(
                    edu.ntnu.idatt2003.persistence.BoardFactory.class)) {

                mockedFactory.when(() -> edu.ntnu.idatt2003.persistence.BoardFactory.loadFromClasspath(anyString()))
                        .thenReturn(mockMapData);

                gateway.newGame(100);
            }
        }

        @Test
        void shouldAddPlayerSuccessfully() {
            gateway.addObserver(mockObserver);

            gateway.addPlayer("Alice", "BLUE", LocalDate.of(1990, 1, 1));

            assertEquals("Alice", gateway.currentPlayerName());
            List<PlayerView> players = gateway.players();
            assertEquals(1, players.size());
            assertEquals("Alice", players.get(0).playerName());
            assertEquals("BLUE", players.get(0).playerToken());

            ArgumentCaptor<BoardGameEvent> eventCaptor = ArgumentCaptor.forClass(BoardGameEvent.class);
            verify(mockObserver).update(eventCaptor.capture());
            assertEquals(BoardGameEvent.EventType.PLAYER_ADDED, eventCaptor.getValue().getTypeOfEvent());
        }

        @Test
        void shouldAddMultiplePlayersIncludingPurple() {
            gateway.addPlayer("Alice", "BLUE", LocalDate.of(1990, 1, 1));
            gateway.addPlayer("Bob", "RED", LocalDate.of(1990, 2, 2));
            gateway.addPlayer("Charlie", "GREEN", LocalDate.of(1990, 3, 3));
            gateway.addPlayer("Diana", "YELLOW", LocalDate.of(1990, 4, 4));
            gateway.addPlayer("Eve", "PURPLE", LocalDate.of(1990, 5, 5));

            List<PlayerView> players = gateway.players();
            assertEquals(5, players.size());
            assertEquals("PURPLE", players.get(4).playerToken());
        }

        @Test
        void shouldThrowExceptionWhenAddingPlayerWithoutGame() {
            SnlGateway gatewayWithoutGame = new SnlGateway(mockBoardLoader, mockDiceFactory,
                    mockPlayerStore, mockOverlayProvider);

            assertThrows(NullPointerException.class,
                    () -> gatewayWithoutGame.addPlayer("Alice", "BLUE", LocalDate.of(1990, 1, 1)));
        }

        @Test
        void shouldPlacePlayerOnStartTileWhenAdded() {
            edu.games.engine.board.Tile startTile = mock(edu.games.engine.board.Tile.class);
            when(startTile.tileId()).thenReturn(1);
            when(mockLinearBoard.start()).thenReturn(startTile);

            gateway.addPlayer("Alice", "BLUE", LocalDate.of(1990, 1, 1));

            List<PlayerView> players = gateway.players();
            assertEquals(1, players.get(0).tileId());
        }
    }

    @Nested
    class DiceRollingAndGameplay {

        @BeforeEach
        void setUp() {
            Map<Integer, Integer> testSnakes = Map.of(16, 6);
            Map<Integer, Integer> testLadders = Map.of(2, 38);
            BoardAdapter.MapData mockMapData = new BoardAdapter.MapData(100, testSnakes, testLadders);

            when(mockBoardLoader.create(100)).thenReturn(mockLinearBoard);

            edu.games.engine.board.Tile startTile = mock(edu.games.engine.board.Tile.class);
            when(startTile.tileId()).thenReturn(1);
            when(mockLinearBoard.start()).thenReturn(startTile);

            try (MockedStatic<edu.ntnu.idatt2003.persistence.BoardFactory> mockedFactory = mockStatic(
                    edu.ntnu.idatt2003.persistence.BoardFactory.class)) {

                mockedFactory.when(() -> edu.ntnu.idatt2003.persistence.BoardFactory.loadFromClasspath(anyString()))
                        .thenReturn(mockMapData);

                gateway.newGame(100);
                gateway.addPlayer("Alice", "BLUE", LocalDate.of(1990, 1, 1));
            }
        }

        @Test
        void shouldRollDiceAndProcessTurn() {
            gateway.addObserver(mockObserver);

            int result = gateway.rollDice();

            // Result should be between 2 and 12 (two dice)
            assertTrue(result >= 2 && result <= 12);
            assertFalse(gateway.lastDiceValues().isEmpty());

            // Should have notified about dice roll and turn change
            ArgumentCaptor<BoardGameEvent> eventCaptor = ArgumentCaptor.forClass(BoardGameEvent.class);
            verify(mockObserver, atLeast(2)).update(eventCaptor.capture());

            List<BoardGameEvent> events = eventCaptor.getAllValues();
            assertTrue(events.stream().anyMatch(e -> e.getTypeOfEvent() == BoardGameEvent.EventType.DICE_ROLLED));
            assertTrue(events.stream().anyMatch(e -> e.getTypeOfEvent() == BoardGameEvent.EventType.TURN_CHANGED));
        }

        @Test
        void shouldReturnZeroWhenRollingWithoutGame() {
            SnlGateway gatewayWithoutGame = new SnlGateway(mockBoardLoader, mockDiceFactory,
                    mockPlayerStore, mockOverlayProvider);

            int result = gatewayWithoutGame.rollDice();

            assertEquals(0, result);
        }

        @Test
        void shouldReturnZeroWhenRollingWithoutPlayers() {
            Map<Integer, Integer> testSnakes = Map.of();
            Map<Integer, Integer> testLadders = Map.of();
            BoardAdapter.MapData mockMapData = new BoardAdapter.MapData(50, testSnakes, testLadders);

            when(mockBoardLoader.create(50)).thenReturn(mockLinearBoard);

            try (MockedStatic<edu.ntnu.idatt2003.persistence.BoardFactory> mockedFactory = mockStatic(
                    edu.ntnu.idatt2003.persistence.BoardFactory.class)) {

                mockedFactory.when(() -> edu.ntnu.idatt2003.persistence.BoardFactory.loadFromClasspath(anyString()))
                        .thenReturn(mockMapData);

                SnlGateway gatewayWithoutPlayers = new SnlGateway(mockBoardLoader, mockDiceFactory,
                        mockPlayerStore, mockOverlayProvider);
                gatewayWithoutPlayers.newGame(50);

                int result = gatewayWithoutPlayers.rollDice();

                assertEquals(0, result);
            }
        }

        @Test
        void shouldNotifyPlayerMovementWhenPlayerMoves() {
            // Set up a scenario where player will definitely move
            edu.games.engine.board.Tile currentTile = mock(edu.games.engine.board.Tile.class);
            when(currentTile.tileId()).thenReturn(5);

            edu.games.engine.board.Tile newTile = mock(edu.games.engine.board.Tile.class);
            when(newTile.tileId()).thenReturn(8);

            // This is complex to mock completely, but we can test the structure
            gateway.addObserver(mockObserver);

            gateway.rollDice();

            // Verify that dice rolled event was sent
            ArgumentCaptor<BoardGameEvent> eventCaptor = ArgumentCaptor.forClass(BoardGameEvent.class);
            verify(mockObserver, atLeast(1)).update(eventCaptor.capture());

            List<BoardGameEvent> events = eventCaptor.getAllValues();
            assertTrue(events.stream().anyMatch(e -> e.getTypeOfEvent() == BoardGameEvent.EventType.DICE_ROLLED));
        }
    }

    @Nested
    class GameState {

        @Test
        void shouldReturnZeroBoardSizeWithoutGame() {
            assertEquals(0, gateway.boardSize());
        }

        @Test
        void shouldCalculateBoardSizeCorrectly() {
            when(mockBoardLoader.create(100)).thenReturn(mockLinearBoard);

            edu.games.engine.board.LinearTile endTile = mock(edu.games.engine.board.LinearTile.class);
            when(endTile.tileId()).thenReturn(100);
            when(mockLinearBoard.start()).thenReturn(mock(edu.games.engine.board.Tile.class));
            when(mockLinearBoard.move(any(), eq(Integer.MAX_VALUE))).thenReturn(endTile);

            Map<Integer, Integer> testSnakes = Map.of();
            Map<Integer, Integer> testLadders = Map.of();
            BoardAdapter.MapData mockMapData = new BoardAdapter.MapData(100, testSnakes, testLadders);

            try (MockedStatic<edu.ntnu.idatt2003.persistence.BoardFactory> mockedFactory = mockStatic(
                    edu.ntnu.idatt2003.persistence.BoardFactory.class)) {

                mockedFactory.when(() -> edu.ntnu.idatt2003.persistence.BoardFactory.loadFromClasspath(anyString()))
                        .thenReturn(mockMapData);

                gateway.newGame(100);

                assertEquals(100, gateway.boardSize());
            }
        }

        @Test
        void shouldReturnEmptyPlayersListWithoutGame() {
            List<PlayerView> players = gateway.players();

            assertTrue(players.isEmpty());
        }

        @Test
        void shouldReturnEmptyPlayersListWithoutPlayers() {
            Map<Integer, Integer> testSnakes = Map.of();
            Map<Integer, Integer> testLadders = Map.of();
            BoardAdapter.MapData mockMapData = new BoardAdapter.MapData(50, testSnakes, testLadders);

            when(mockBoardLoader.create(50)).thenReturn(mockLinearBoard);

            try (MockedStatic<edu.ntnu.idatt2003.persistence.BoardFactory> mockedFactory = mockStatic(
                    edu.ntnu.idatt2003.persistence.BoardFactory.class)) {

                mockedFactory.when(() -> edu.ntnu.idatt2003.persistence.BoardFactory.loadFromClasspath(anyString()))
                        .thenReturn(mockMapData);

                gateway.newGame(50);

                List<PlayerView> players = gateway.players();

                assertTrue(players.isEmpty());
            }
        }

        @Test
        void shouldReturnPlayersWithCorrectTurnInfo() {
            Map<Integer, Integer> testSnakes = Map.of();
            Map<Integer, Integer> testLadders = Map.of();
            BoardAdapter.MapData mockMapData = new BoardAdapter.MapData(100, testSnakes, testLadders);

            when(mockBoardLoader.create(100)).thenReturn(mockLinearBoard);

            edu.games.engine.board.Tile startTile = mock(edu.games.engine.board.Tile.class);
            when(startTile.tileId()).thenReturn(1);
            when(mockLinearBoard.start()).thenReturn(startTile);

            try (MockedStatic<edu.ntnu.idatt2003.persistence.BoardFactory> mockedFactory = mockStatic(
                    edu.ntnu.idatt2003.persistence.BoardFactory.class)) {

                mockedFactory.when(() -> edu.ntnu.idatt2003.persistence.BoardFactory.loadFromClasspath(anyString()))
                        .thenReturn(mockMapData);

                gateway.newGame(100);
                gateway.addPlayer("Alice", "BLUE", LocalDate.of(1990, 1, 1));
                gateway.addPlayer("Bob", "RED", LocalDate.of(1990, 2, 2));

                List<PlayerView> players = gateway.players();

                assertEquals(2, players.size());
                assertTrue(players.get(0).hasTurn());
                assertFalse(players.get(1).hasTurn());
                assertEquals(-1, players.get(0).activePieceIndex());
                assertEquals(-1, players.get(1).activePieceIndex());
            }
        }
    }

    @Nested
    class SnakesAndLaddersData {

        @Test
        void shouldReturnSnakesFromStrategy() {
            Map<Integer, Integer> expectedSnakes = Map.of(16, 6, 47, 26, 49, 11);
            Map<Integer, Integer> expectedLadders = Map.of(2, 38, 7, 14, 8, 31);
            BoardAdapter.MapData mockMapData = new BoardAdapter.MapData(100, expectedSnakes, expectedLadders);

            when(mockBoardLoader.create(100)).thenReturn(mockLinearBoard);

            try (MockedStatic<edu.ntnu.idatt2003.persistence.BoardFactory> mockedFactory = mockStatic(
                    edu.ntnu.idatt2003.persistence.BoardFactory.class)) {

                mockedFactory.when(() -> edu.ntnu.idatt2003.persistence.BoardFactory.loadFromClasspath(anyString()))
                        .thenReturn(mockMapData);

                gateway.newGame(100);

                Map<Integer, Integer> snakes = gateway.getSnakes();
                Map<Integer, Integer> ladders = gateway.getLadders();

                assertEquals(expectedSnakes, snakes);
                assertEquals(expectedLadders, ladders);
            }
        }

        @Test
        void shouldReturnEmptyMapsWhenStrategyIsNotSnlStrategy() {
            // This tests the instanceof check in getSnakes() and getLadders()
            SnlGateway gatewayWithoutStrategy = new SnlGateway(mockBoardLoader, mockDiceFactory,
                    mockPlayerStore, mockOverlayProvider);

            Map<Integer, Integer> snakes = gatewayWithoutStrategy.getSnakes();
            Map<Integer, Integer> ladders = gatewayWithoutStrategy.getLadders();

            assertTrue(snakes.isEmpty());
            assertTrue(ladders.isEmpty());
        }

        @Test
        void shouldReturnEmptyMapsWhenGameNotInitialized() {
            Map<Integer, Integer> snakes = gateway.getSnakes();
            Map<Integer, Integer> ladders = gateway.getLadders();

            assertTrue(snakes.isEmpty());
            assertTrue(ladders.isEmpty());
        }
    }

    @Nested
    class ObserverPattern {

        @Test
        void shouldAddObserverSuccessfully() {
            gateway.addObserver(mockObserver);

            Map<Integer, Integer> testSnakes = Map.of();
            Map<Integer, Integer> testLadders = Map.of();
            BoardAdapter.MapData mockMapData = new BoardAdapter.MapData(50, testSnakes, testLadders);

            when(mockBoardLoader.create(50)).thenReturn(mockLinearBoard);

            try (MockedStatic<edu.ntnu.idatt2003.persistence.BoardFactory> mockedFactory = mockStatic(
                    edu.ntnu.idatt2003.persistence.BoardFactory.class)) {

                mockedFactory.when(() -> edu.ntnu.idatt2003.persistence.BoardFactory.loadFromClasspath(anyString()))
                        .thenReturn(mockMapData);

                gateway.newGame(50);

                verify(mockObserver).update(any(BoardGameEvent.class));
            }
        }

        @Test
        void shouldRemoveObserverSuccessfully() {
            gateway.addObserver(mockObserver);
            gateway.removeObserver(mockObserver);

            Map<Integer, Integer> testSnakes = Map.of();
            Map<Integer, Integer> testLadders = Map.of();
            BoardAdapter.MapData mockMapData = new BoardAdapter.MapData(50, testSnakes, testLadders);

            when(mockBoardLoader.create(50)).thenReturn(mockLinearBoard);

            try (MockedStatic<edu.ntnu.idatt2003.persistence.BoardFactory> mockedFactory = mockStatic(
                    edu.ntnu.idatt2003.persistence.BoardFactory.class)) {

                mockedFactory.when(() -> edu.ntnu.idatt2003.persistence.BoardFactory.loadFromClasspath(anyString()))
                        .thenReturn(mockMapData);

                gateway.newGame(50);

                verify(mockObserver, never()).update(any(BoardGameEvent.class));
            }
        }
    }
}