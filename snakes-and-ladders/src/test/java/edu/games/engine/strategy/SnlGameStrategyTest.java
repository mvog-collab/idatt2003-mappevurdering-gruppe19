package edu.games.engine.strategy;

import edu.games.engine.board.Board;
import edu.games.engine.board.Tile;
import edu.games.engine.dice.Dice;
import edu.games.engine.impl.DefaultGame;
import edu.games.engine.model.Player;
import edu.games.engine.model.PlayerPiece;
import edu.games.engine.model.Token;
import edu.games.engine.rule.RuleEngine;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SnlGameStrategyTest {

    private SnlGameStrategy strategy;
    private Map<Integer, Integer> snakes;
    private Map<Integer, Integer> ladders;

    @Mock
    private RuleEngine mockRuleEngine;

    @Mock
    private DefaultGame mockGame;

    @Mock
    private Board mockBoard;

    @Mock
    private Dice mockDice;

    @Mock
    private Tile mockStartTile;

    @Mock
    private Tile mockDestinationTile;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        snakes = new HashMap<>();
        snakes.put(16, 6);
        snakes.put(47, 26);

        ladders = new HashMap<>();
        ladders.put(2, 38);
        ladders.put(7, 14);

        strategy = new SnlGameStrategy(mockRuleEngine, snakes, ladders);

        when(mockGame.getBoard()).thenReturn(mockBoard);
        when(mockGame.getDice()).thenReturn(mockDice);
        when(mockBoard.start()).thenReturn(mockStartTile);
    }

    @Nested
    class GameInitialization {

        @Test
        void shouldMoveAllPlayersToStartWhenInitializing() {
            Player player1 = createTestPlayer("Alice", Token.BLUE);
            Player player2 = createTestPlayer("Bob", Token.RED);
            List<Player> players = Arrays.asList(player1, player2);

            when(mockGame.getPlayers()).thenReturn(players);

            strategy.initializeGame(mockGame);

            assertEquals(mockStartTile, player1.getCurrentTile());
            assertEquals(mockStartTile, player2.getCurrentTile());
        }

        @Test
        void shouldHandleEmptyPlayerList() {
            when(mockGame.getPlayers()).thenReturn(List.of());

            assertDoesNotThrow(() -> strategy.initializeGame(mockGame));
        }

        @Test
        void shouldHandleNullGame() {
            assertDoesNotThrow(() -> strategy.initializeGame(null));
        }

        @Test
        void shouldHandleNullBoard() {
            Player player = createTestPlayer("Alice", Token.BLUE);
            when(mockGame.getPlayers()).thenReturn(List.of(player));
            when(mockGame.getBoard()).thenReturn(null);

            assertDoesNotThrow(() -> strategy.initializeGame(mockGame));
        }
    }

    @Nested
    class DiceRollProcessing {

        @Test
        void shouldDelegateToRuleEngineForExtraTurn() {
            Player player = createTestPlayer("Alice", Token.BLUE);
            List<Integer> diceValues = Arrays.asList(3, 3);

            when(mockDice.lastValues()).thenReturn(diceValues);
            when(mockRuleEngine.grantsExtraTurn(player, diceValues, mockGame)).thenReturn(true);

            boolean result = strategy.processDiceRoll(player, 6, mockGame);

            assertTrue(result);
            verify(mockRuleEngine).grantsExtraTurn(player, diceValues, mockGame);
        }

        @Test
        void shouldReturnFalseWhenRuleEngineReturnsFalse() {
            Player player = createTestPlayer("Bob", Token.RED);
            List<Integer> diceValues = Arrays.asList(3, 5);

            when(mockDice.lastValues()).thenReturn(diceValues);
            when(mockRuleEngine.grantsExtraTurn(player, diceValues, mockGame)).thenReturn(false);

            boolean result = strategy.processDiceRoll(player, 8, mockGame);

            assertFalse(result);
        }
    }

    @Nested
    class PieceMovement {

        private Player player;
        private Tile currentTile;

        @BeforeEach
        void setUp() {
            player = createTestPlayer("Player", Token.BLUE);
            currentTile = mock(Tile.class);
            player.moveTo(currentTile);
        }

        @Test
        void shouldNotMoveWhenDiceRollIsTwelve() {
            when(mockBoard.move(currentTile, 0)).thenReturn(currentTile);

            Tile result = strategy.movePiece(player, 0, 12, mockGame);

            assertEquals(currentTile, result);
            verify(mockBoard).move(currentTile, 0);
        }

        @Test
        void shouldMoveNormallyForNonTwelveRoll() {
            when(mockBoard.move(currentTile, 8)).thenReturn(mockDestinationTile);

            Tile result = strategy.movePiece(player, 0, 8, mockGame);

            assertEquals(mockDestinationTile, result);
            verify(mockBoard).move(currentTile, 8);
        }

        @Test
        void shouldReturnNullWhenPlayerIsNull() {
            Tile result = strategy.movePiece(null, 0, 6, mockGame);

            assertNull(result);
            verifyNoInteractions(mockBoard);
        }

        @Test
        void shouldReturnNullWhenGameIsNull() {
            Tile result = strategy.movePiece(player, 0, 6, null);

            assertNull(result);
            verifyNoInteractions(mockBoard);
        }

        @Test
        void shouldReturnNullWhenPlayerCurrentTileIsNull() {
            Player newPlayer = createTestPlayer("NewPlayer", Token.GREEN);
            // newPlayer.getCurrentTile() returns null by default

            Tile result = strategy.movePiece(newPlayer, 0, 6, mockGame);

            assertNull(result);
            verifyNoInteractions(mockBoard);
        }

        @Test
        void shouldReturnNullWhenBoardIsNull() {
            when(mockGame.getBoard()).thenReturn(null);

            Tile result = strategy.movePiece(player, 0, 6, mockGame);

            assertNull(result);
        }

        @Test
        void shouldIgnorePieceIndex() {
            // SnL doesn't use piece index since player is the piece
            when(mockBoard.move(currentTile, 5)).thenReturn(mockDestinationTile);

            Tile result1 = strategy.movePiece(player, 0, 5, mockGame);
            Tile result2 = strategy.movePiece(player, 999, 5, mockGame);

            assertEquals(mockDestinationTile, result1);
            assertEquals(mockDestinationTile, result2);
        }

        @Test
        void shouldHandleZeroDiceValue() {
            when(mockBoard.move(currentTile, 0)).thenReturn(currentTile);

            Tile result = strategy.movePiece(player, 0, 0, mockGame);

            assertEquals(currentTile, result);
        }

        @Test
        void shouldHandleNegativeDiceValue() {
            // Should still pass to board.move() and let board handle validation
            when(mockBoard.move(currentTile, -1)).thenReturn(currentTile);

            Tile result = strategy.movePiece(player, 0, -1, mockGame);

            assertEquals(currentTile, result);
            verify(mockBoard).move(currentTile, -1);
        }
    }

    @Nested
    class WinConditionChecking {

        @Test
        void shouldDelegateToRuleEngineForWinCheck() {
            Player player = createTestPlayer("Winner", Token.GREEN);
            when(mockRuleEngine.hasWon(player, mockGame)).thenReturn(true);

            boolean result = strategy.checkWinCondition(player, mockGame);

            assertTrue(result);
            verify(mockRuleEngine).hasWon(player, mockGame);
        }

        @Test
        void shouldReturnFalseWhenRuleEngineReturnsFalse() {
            Player player = createTestPlayer("NotWinner", Token.YELLOW);
            when(mockRuleEngine.hasWon(player, mockGame)).thenReturn(false);

            boolean result = strategy.checkWinCondition(player, mockGame);

            assertFalse(result);
        }
    }

    @Nested
    class SpecialRulesApplication {

        @Test
        void shouldDelegateToRuleEngineIgnoringPiece() {
            Player player = createTestPlayer("Player", Token.BLUE);
            PlayerPiece piece = player.getPiece(0); // This will be ignored
            Tile destinationTile = mock(Tile.class);

            strategy.applySpecialRules(player, piece, destinationTile, mockGame);

            // Piece parameter should be null when passed to rule engine
            verify(mockRuleEngine).applyPostLandingEffects(player, null, destinationTile, mockGame);
        }

        @Test
        void shouldHandleNullValues() {
            assertDoesNotThrow(() -> strategy.applySpecialRules(null, null, null, null));

            verify(mockRuleEngine).applyPostLandingEffects(null, null, null, null);
        }
    }

    @Nested
    class MapDataAccess {

        @Test
        void shouldReturnCopyOfSnakesMap() {
            Map<Integer, Integer> returnedSnakes = strategy.getSnakes();

            assertEquals(snakes, returnedSnakes);
            assertNotSame(snakes, returnedSnakes); // Should be a copy
        }

        @Test
        void shouldReturnCopyOfLaddersMap() {
            Map<Integer, Integer> returnedLadders = strategy.getLadders();

            assertEquals(ladders, returnedLadders);
            assertNotSame(ladders, returnedLadders); // Should be a copy
        }

        @Test
        void shouldReturnImmutableSnakesMap() {
            Map<Integer, Integer> returnedSnakes = strategy.getSnakes();

            assertThrows(UnsupportedOperationException.class, () -> returnedSnakes.put(99, 1));
        }

        @Test
        void shouldReturnImmutableLaddersMap() {
            Map<Integer, Integer> returnedLadders = strategy.getLadders();

            assertThrows(UnsupportedOperationException.class, () -> returnedLadders.put(99, 100));
        }
    }

    @Nested
    class EdgeCases {

        @Test
        void shouldHandleEmptySnakesAndLadders() {
            SnlGameStrategy emptyStrategy = new SnlGameStrategy(
                    mockRuleEngine, new HashMap<>(), new HashMap<>());

            assertTrue(emptyStrategy.getSnakes().isEmpty());
            assertTrue(emptyStrategy.getLadders().isEmpty());
        }

        @Test
        void shouldHandleMaximumDiceValue() {
            Player player = createTestPlayer("Player", Token.BLUE);
            Tile currentTile = mock(Tile.class);
            player.moveTo(currentTile);

            when(mockBoard.move(currentTile, Integer.MAX_VALUE)).thenReturn(mockDestinationTile);

            Tile result = strategy.movePiece(player, 0, Integer.MAX_VALUE, mockGame);

            assertEquals(mockDestinationTile, result);
        }

        @Test
        void shouldHandleSpecialDiceValueTwelve() {
            Player player = createTestPlayer("Player", Token.RED);
            Tile currentTile = mock(Tile.class);
            player.moveTo(currentTile);

            // Should move 0 steps when dice is 12
            when(mockBoard.move(currentTile, 0)).thenReturn(currentTile);

            Tile result = strategy.movePiece(player, 0, 12, mockGame);

            assertEquals(currentTile, result);
            verify(mockBoard).move(currentTile, 0);
        }

        @Test
        void shouldInitializeWithNullMaps() {
            // Test defensive programming - constructor should handle null maps
            assertDoesNotThrow(() -> new SnlGameStrategy(mockRuleEngine, null, null));
        }
    }

    private Player createTestPlayer(String name, Token token) {
        return new Player(name, token, LocalDate.of(1990, 1, 1));
    }
}