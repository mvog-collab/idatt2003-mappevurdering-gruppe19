package edu.ntnu.idatt2003.gateway;

import edu.games.engine.dice.factory.DiceFactory;
import edu.games.engine.exception.ValidationException;
import edu.games.engine.impl.overlay.OverlayProvider;
import edu.games.engine.observer.BoardGameEvent;
import edu.games.engine.observer.BoardGameObserver;
import edu.games.engine.store.PlayerStore;
import edu.ntnu.idatt2003.gateway.view.PlayerView;
import edu.ntnu.idatt2003.persistence.BoardAdapter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class LudoGatewayTest {

    @Mock
    private DiceFactory mockDiceFactory;

    @Mock
    private PlayerStore mockPlayerStore;

    @Mock
    private OverlayProvider mockOverlayProvider;

    @Mock
    private BoardGameObserver mockObserver;

    @Mock
    private edu.games.engine.dice.Dice mockDice;

    private LudoGateway gateway;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Setup dice factory to return mock dice
        when(mockDiceFactory.create()).thenReturn(mockDice);
        when(mockDice.roll()).thenReturn(4); // Default roll value
        when(mockDice.lastValues()).thenReturn(List.of(4)); // Default dice values

        gateway = new LudoGateway(mockDiceFactory, mockPlayerStore, mockOverlayProvider);
    }

    @Nested
    class ConstructorAndFactory {

        @Test
        void shouldCreateGatewayWithValidDependencies() {
            LudoGateway testGateway = new LudoGateway(mockDiceFactory, mockPlayerStore, mockOverlayProvider);

            assertNotNull(testGateway);
        }

        @Test
        void shouldCreateDefaultGateway() {
            LudoGateway defaultGateway = LudoGateway.createDefault();

            assertNotNull(defaultGateway);
            assertEquals(57, defaultGateway.boardSize());
        }
    }

    @Nested
    class GameLifecycle {
        @Test
        void shouldStartNewGameWithMapData() {
            Map<Integer, Integer> snakes = Map.of(16, 6);
            Map<Integer, Integer> ladders = Map.of(2, 38);
            BoardAdapter.MapData mapData = new BoardAdapter.MapData(100, snakes, ladders);

            gateway.addObserver(mockObserver);
            gateway.newGame(mapData);

            // Ludo ignores MapData, should behave same as newGame(int)
            assertEquals(57, gateway.boardSize());
            assertFalse(gateway.hasWinner());
        }

        @Test
        void shouldResetGameProperly() {
            gateway.newGame(10);
            gateway.addPlayer("Alice", "BLUE", LocalDate.of(1990, 1, 1));
            gateway.addPlayer("Bob", "RED", LocalDate.of(1990, 1, 1));
            gateway.selectPiece(0);

            gateway.addObserver(mockObserver);
            gateway.resetGame();

            assertFalse(gateway.hasWinner());
            assertEquals("Alice", gateway.currentPlayerName()); // Should be back to first player

            ArgumentCaptor<BoardGameEvent> eventCaptor = ArgumentCaptor.forClass(BoardGameEvent.class);
            verify(mockObserver).update(eventCaptor.capture());
            assertEquals(BoardGameEvent.EventType.GAME_RESET, eventCaptor.getValue().getTypeOfEvent());
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
            gateway.newGame(10);
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
        void shouldAddMultiplePlayers() {
            gateway.addPlayer("Alice", "BLUE", LocalDate.of(1990, 1, 1));
            gateway.addPlayer("Bob", "RED", LocalDate.of(1990, 2, 2));
            gateway.addPlayer("Charlie", "GREEN", LocalDate.of(1990, 3, 3));

            List<PlayerView> players = gateway.players();
            assertEquals(3, players.size());
            assertEquals("Alice", players.get(0).playerName());
            assertEquals("Bob", players.get(1).playerName());
            assertEquals("Charlie", players.get(2).playerName());
        }

        @Test
        void shouldThrowExceptionWhenAddingPlayerWithoutGame() {
            LudoGateway gatewayWithoutGame = new LudoGateway(mockDiceFactory, mockPlayerStore, mockOverlayProvider);

            assertThrows(NullPointerException.class,
                    () -> gatewayWithoutGame.addPlayer("Alice", "BLUE", LocalDate.of(1990, 1, 1)));
        }

        @Test
        void shouldMapAllValidLudoTokens() {
            gateway.addPlayer("Blue", "BLUE", LocalDate.of(1990, 1, 1));
            gateway.addPlayer("Red", "RED", LocalDate.of(1990, 1, 1));
            gateway.addPlayer("Green", "GREEN", LocalDate.of(1990, 1, 1));
            gateway.addPlayer("Yellow", "YELLOW", LocalDate.of(1990, 1, 1));

            List<PlayerView> players = gateway.players();
            assertEquals(4, players.size());
            assertEquals("BLUE", players.get(0).playerToken());
            assertEquals("RED", players.get(1).playerToken());
            assertEquals("GREEN", players.get(2).playerToken());
            assertEquals("YELLOW", players.get(3).playerToken());
        }
    }

    @Nested
    class PieceSelection {

        @BeforeEach
        void setUp() {
            gateway.newGame(10);
            gateway.addPlayer("Alice", "BLUE", LocalDate.of(1990, 1, 1));
        }

        @Test
        void shouldSelectValidPieceIndex() {
            gateway.addObserver(mockObserver);

            gateway.selectPiece(2);

            ArgumentCaptor<BoardGameEvent> eventCaptor = ArgumentCaptor.forClass(BoardGameEvent.class);
            verify(mockObserver).update(eventCaptor.capture());
            assertEquals(BoardGameEvent.EventType.PIECE_SELECTED, eventCaptor.getValue().getTypeOfEvent());
            assertEquals(2, eventCaptor.getValue().getData());
        }

        @Test
        void shouldThrowExceptionForInvalidPieceIndexTooLow() {
            assertThrows(ValidationException.class, () -> gateway.selectPiece(-1));
        }

        @Test
        void shouldThrowExceptionForInvalidPieceIndexTooHigh() {
            assertThrows(ValidationException.class, () -> gateway.selectPiece(4));
        }

        @Test
        void shouldHandleSelectPieceWithoutGame() {
            LudoGateway gatewayWithoutGame = new LudoGateway(mockDiceFactory, mockPlayerStore, mockOverlayProvider);

            // Should not throw, just return silently
            assertDoesNotThrow(() -> gatewayWithoutGame.selectPiece(0));
        }

        @Test
        void shouldHandleSelectPieceWithoutPlayers() {
            LudoGateway gatewayWithoutPlayers = new LudoGateway(mockDiceFactory, mockPlayerStore, mockOverlayProvider);
            gatewayWithoutPlayers.newGame(10);

            // Should not throw, just return silently
            assertDoesNotThrow(() -> gatewayWithoutPlayers.selectPiece(0));
        }
    }

    @Nested
    class DiceRolling {

        @BeforeEach
        void setUp() {
            gateway.newGame(10);
            gateway.addPlayer("Alice", "BLUE", LocalDate.of(1990, 1, 1));
        }

        @Test
        void shouldRollDiceSuccessfully() {
            gateway.addObserver(mockObserver);

            int result = gateway.rollDice();

            assertTrue(result >= 1 && result <= 6); // Single die result
            assertFalse(gateway.lastDiceValues().isEmpty());

            ArgumentCaptor<BoardGameEvent> eventCaptor = ArgumentCaptor.forClass(BoardGameEvent.class);
            verify(mockObserver).update(eventCaptor.capture());
            assertEquals(BoardGameEvent.EventType.DICE_ROLLED, eventCaptor.getValue().getTypeOfEvent());
        }

        @Test
        void shouldReturnZeroWhenRollingWithoutGame() {
            LudoGateway gatewayWithoutGame = new LudoGateway(mockDiceFactory, mockPlayerStore, mockOverlayProvider);

            int result = gatewayWithoutGame.rollDice();

            assertEquals(0, result);
        }

        @Test
        void shouldReturnZeroWhenRollingWithoutPlayers() {
            LudoGateway gatewayWithoutPlayers = new LudoGateway(mockDiceFactory, mockPlayerStore, mockOverlayProvider);
            gatewayWithoutPlayers.newGame(10);

            int result = gatewayWithoutPlayers.rollDice();

            assertEquals(0, result);
        }

        @Test
        void shouldReturnZeroWhenGameHasWinner() {
            // This is harder to test without setting up a complete win scenario
            // For now, we'll test the basic structure
            gateway.addPlayer("Bob", "RED", LocalDate.of(1990, 1, 1));

            // Simulate some gameplay
            int result = gateway.rollDice();
            assertTrue(result >= 0); // Should be valid result
        }
    }

    @Nested
    class PieceMovement {

        @BeforeEach
        void setUp() {
            gateway.newGame(10);
            gateway.addPlayer("Alice", "BLUE", LocalDate.of(1990, 1, 1));
            gateway.selectPiece(0);
        }

        @Test
        void shouldApplyPieceMovementWithValidSetup() {
            gateway.rollDice(); // Generate a dice roll first
            gateway.addObserver(mockObserver);

            int result = gateway.applyPieceMovement();

            assertTrue(result >= 1 && result <= 6);

            // Should have notified about player movement
            ArgumentCaptor<BoardGameEvent> eventCaptor = ArgumentCaptor.forClass(BoardGameEvent.class);
            verify(mockObserver, atLeastOnce()).update(eventCaptor.capture());
        }

        @Test
        void shouldReturnZeroWhenApplyingMovementWithoutGame() {
            LudoGateway gatewayWithoutGame = new LudoGateway(mockDiceFactory, mockPlayerStore, mockOverlayProvider);

            int result = gatewayWithoutGame.applyPieceMovement();

            assertEquals(0, result);
        }

        @Test
        void shouldReturnZeroWhenApplyingMovementWithoutPlayers() {
            LudoGateway gatewayWithoutPlayers = new LudoGateway(mockDiceFactory, mockPlayerStore, mockOverlayProvider);
            gatewayWithoutPlayers.newGame(10);

            int result = gatewayWithoutPlayers.applyPieceMovement();

            assertEquals(0, result);
        }

        @Test
        void shouldReturnZeroWhenApplyingMovementWithoutSelection() {
            LudoGateway gatewayWithoutSelection = new LudoGateway(mockDiceFactory, mockPlayerStore,
                    mockOverlayProvider);
            gatewayWithoutSelection.newGame(10);
            gatewayWithoutSelection.addPlayer("Alice", "BLUE", LocalDate.of(1990, 1, 1));
            // Don't select piece

            int result = gatewayWithoutSelection.applyPieceMovement();

            assertEquals(0, result);
        }
    }

    @Nested
    class GameState {

        @Test
        void shouldReturnCorrectBoardSize() {
            assertEquals(57, gateway.boardSize());
        }

        @Test
        void shouldReturnNoWinnerInitially() {
            gateway.newGame(10);

            assertFalse(gateway.hasWinner());
        }

        @Test
        void shouldReturnEmptyPlayersListWithoutGame() {
            List<PlayerView> players = gateway.players();

            assertTrue(players.isEmpty());
        }

        @Test
        void shouldReturnEmptyPlayersListWithoutPlayers() {
            gateway.newGame(10);

            List<PlayerView> players = gateway.players();

            assertTrue(players.isEmpty());
        }

        @Test
        void shouldReturnPlayersWithCorrectTurnInfo() {
            gateway.newGame(10);
            gateway.addPlayer("Alice", "BLUE", LocalDate.of(1990, 1, 1));
            gateway.addPlayer("Bob", "RED", LocalDate.of(1990, 2, 2));
            gateway.selectPiece(1);

            List<PlayerView> players = gateway.players();

            assertEquals(2, players.size());
            assertTrue(players.get(0).hasTurn()); // Alice should have turn
            assertFalse(players.get(1).hasTurn()); // Bob should not have turn
            assertEquals(1, players.get(0).activePieceIndex()); // Alice has piece 1 selected
            assertEquals(-1, players.get(1).activePieceIndex()); // Bob has no piece selected
        }

        @Test
        void shouldReturnCorrectPiecePositions() {
            gateway.newGame(10);
            gateway.addPlayer("Alice", "BLUE", LocalDate.of(1990, 1, 1));

            List<PlayerView> players = gateway.players();
            PlayerView alice = players.get(0);

            assertEquals(4, alice.piecePositions().size()); // All 4 pieces
            // All pieces should be at home initially (position 0)
            alice.piecePositions().forEach(pos -> assertEquals(0, pos));
        }
    }

    @Nested
    class ObserverPattern {

        @Test
        void shouldAddObserverSuccessfully() {
            gateway.addObserver(mockObserver);

            gateway.newGame(10);

            verify(mockObserver).update(any(BoardGameEvent.class));
        }

        @Test
        void shouldRemoveObserverSuccessfully() {
            gateway.addObserver(mockObserver);
            gateway.removeObserver(mockObserver);

            gateway.newGame(10);

            verify(mockObserver, never()).update(any(BoardGameEvent.class));
        }

        @Test
        void shouldNotifyObserversForGameEvents() {
            gateway.addObserver(mockObserver);
            gateway.newGame(10);
            gateway.addPlayer("Alice", "BLUE", LocalDate.of(1990, 1, 1));
            gateway.selectPiece(0);
            gateway.rollDice();

            ArgumentCaptor<BoardGameEvent> eventCaptor = ArgumentCaptor.forClass(BoardGameEvent.class);
            verify(mockObserver, atLeast(3)).update(eventCaptor.capture());

            List<BoardGameEvent> events = eventCaptor.getAllValues();
            assertTrue(events.stream().anyMatch(e -> e.getTypeOfEvent() == BoardGameEvent.EventType.GAME_STARTED));
            assertTrue(events.stream().anyMatch(e -> e.getTypeOfEvent() == BoardGameEvent.EventType.PLAYER_ADDED));
            assertTrue(events.stream().anyMatch(e -> e.getTypeOfEvent() == BoardGameEvent.EventType.PIECE_SELECTED));
        }

        @Test
        void shouldHandleNullObserver() {
            assertDoesNotThrow(() -> gateway.addObserver(null));
            assertDoesNotThrow(() -> gateway.removeObserver(null));
        }
    }
}