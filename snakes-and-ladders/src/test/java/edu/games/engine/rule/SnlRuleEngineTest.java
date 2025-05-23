package edu.games.engine.rule;

import edu.games.engine.board.LinearBoard;
import edu.games.engine.board.Tile;
import edu.games.engine.impl.DefaultGame;
import edu.games.engine.model.Player;
import edu.games.engine.model.Token;
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

class SnlRuleEngineTest {

    private SnlRuleEngine ruleEngine;
    private Map<Integer, Integer> snakes;
    private Map<Integer, Integer> ladders;

    @Mock
    private DefaultGame mockGame;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        snakes = new HashMap<>();
        snakes.put(16, 6); // Snake from 16 to 6
        snakes.put(47, 26); // Snake from 47 to 26
        snakes.put(49, 11); // Snake from 49 to 11

        ladders = new HashMap<>();
        ladders.put(2, 38); // Ladder from 2 to 38
        ladders.put(7, 14); // Ladder from 7 to 14
        ladders.put(8, 31); // Ladder from 8 to 31

        ruleEngine = new SnlRuleEngine(snakes, ladders);
    }

    @Nested
    class ExtraTurnRules {

        @Test
        void shouldGrantExtraTurnForDoubleOnes() {
            Player player = createTestPlayer("Alice", Token.BLUE);
            List<Integer> diceValues = Arrays.asList(1, 1);

            boolean result = ruleEngine.grantsExtraTurn(player, diceValues, mockGame);

            assertTrue(result);
        }

        @Test
        void shouldGrantExtraTurnForDoubleTwos() {
            Player player = createTestPlayer("Bob", Token.RED);
            List<Integer> diceValues = Arrays.asList(2, 2);

            boolean result = ruleEngine.grantsExtraTurn(player, diceValues, mockGame);

            assertTrue(result);
        }

        @Test
        void shouldNotGrantExtraTurnForDoubleSixes() {
            Player player = createTestPlayer("Diana", Token.YELLOW);
            List<Integer> diceValues = Arrays.asList(6, 6);

            boolean result = ruleEngine.grantsExtraTurn(player, diceValues, mockGame);

            assertFalse(result);
        }

        @Test
        void shouldNotGrantExtraTurnForNonDoubles() {
            Player player = createTestPlayer("Eve", Token.PURPLE);
            List<Integer> diceValues = Arrays.asList(3, 5);

            boolean result = ruleEngine.grantsExtraTurn(player, diceValues, mockGame);

            assertFalse(result);
        }

        @Test
        void shouldNotGrantExtraTurnForSingleDie() {
            Player player = createTestPlayer("Frank", Token.BLUE);
            List<Integer> diceValues = List.of(4);

            boolean result = ruleEngine.grantsExtraTurn(player, diceValues, mockGame);

            assertFalse(result);
        }

        @Test
        void shouldNotGrantExtraTurnForEmptyDiceValues() {
            Player player = createTestPlayer("Henry", Token.GREEN);
            List<Integer> diceValues = List.of();

            boolean result = ruleEngine.grantsExtraTurn(player, diceValues, mockGame);

            assertFalse(result);
        }
    }

    @Nested
    class PostLandingEffectsWithRealBoard {

        private LinearBoard realBoard;
        private Player currentPlayer;
        private Player opponentPlayer;
        private List<Player> allPlayers;

        @BeforeEach
        void setUp() {
            // Use a real LinearBoard instead of mocking
            realBoard = new LinearBoard(100);
            currentPlayer = createTestPlayer("Current", Token.BLUE);
            opponentPlayer = createTestPlayer("Opponent", Token.RED);
            allPlayers = Arrays.asList(currentPlayer, opponentPlayer);

            when(mockGame.getBoard()).thenReturn(realBoard);
            when(mockGame.getPlayers()).thenReturn(allPlayers);
        }

        @Test
        void shouldMovePlayerDownSnake() {
            // Place player on snake head tile
            Tile snakeHeadTile = realBoard.tile(16);
            currentPlayer.moveTo(snakeHeadTile);

            ruleEngine.applyPostLandingEffects(currentPlayer, null, snakeHeadTile, mockGame);

            // Player should now be on snake tail tile
            assertEquals(6, currentPlayer.getCurrentTile().tileId());
        }

        @Test
        void shouldMovePlayerUpLadder() {
            // Place player on ladder bottom tile
            Tile ladderBottomTile = realBoard.tile(2);
            currentPlayer.moveTo(ladderBottomTile);

            ruleEngine.applyPostLandingEffects(currentPlayer, null, ladderBottomTile, mockGame);

            // Player should now be on ladder top tile
            assertEquals(38, currentPlayer.getCurrentTile().tileId());
        }

        @Test
        void shouldBumpOpponentAfterLadder() {
            // Setup: current player lands on ladder bottom (2), should go to top (38)
            // Opponent is already on tile 38
            Tile ladderBottomTile = realBoard.tile(2);
            Tile ladderTopTile = realBoard.tile(38);

            opponentPlayer.moveTo(ladderTopTile);
            currentPlayer.moveTo(ladderBottomTile);

            ruleEngine.applyPostLandingEffects(currentPlayer, null, ladderBottomTile, mockGame);

            // Current player should be on ladder top, opponent sent to start
            assertEquals(38, currentPlayer.getCurrentTile().tileId());
            assertEquals(realBoard.start(), opponentPlayer.getCurrentTile());
        }

        @Test
        void shouldNotBumpFromStartTile() {
            // Place opponent on start tile
            opponentPlayer.moveTo(realBoard.start());

            // Current player lands on start tile
            ruleEngine.applyPostLandingEffects(currentPlayer, null, realBoard.start(), mockGame);

            // Opponent should remain on start (safe tile)
            assertEquals(realBoard.start(), opponentPlayer.getCurrentTile());
        }

        @Test
        void shouldHandleRegularTileWithNoBumping() {
            // Place player on regular tile with no snake/ladder
            Tile regularTile = realBoard.tile(10);
            currentPlayer.moveTo(regularTile);

            ruleEngine.applyPostLandingEffects(currentPlayer, null, regularTile, mockGame);

            // Player should remain on same tile
            assertEquals(regularTile, currentPlayer.getCurrentTile());
        }
    }

    @Nested
    class PostLandingEffectsWithBehaviorVerification {

        private Player currentPlayer;

        @BeforeEach
        void setUp() {
            currentPlayer = createTestPlayer("TestPlayer", Token.BLUE);
        }

        @Test
        void shouldHandleNullInputsGracefully() {
            // Test that method doesn't throw with null inputs
            assertDoesNotThrow(() -> ruleEngine.applyPostLandingEffects(null, null, null, null));

            assertDoesNotThrow(() -> ruleEngine.applyPostLandingEffects(currentPlayer, null, null, mockGame));
        }

        @Test
        void shouldNotFailWithNonLinearBoard() {
            when(mockGame.getBoard()).thenReturn(null);

            assertDoesNotThrow(
                    () -> ruleEngine.applyPostLandingEffects(currentPlayer, null, mock(Tile.class), mockGame));
        }
    }

    @Nested
    class WinConditionsWithRealBoard {

        private LinearBoard realBoard;

        @BeforeEach
        void setUp() {
            realBoard = new LinearBoard(100);
            when(mockGame.getBoard()).thenReturn(realBoard);
        }

        @Test
        void shouldReturnTrueWhenPlayerReachesEndTile() {
            Player winner = createTestPlayer("Winner", Token.BLUE);
            Tile endTile = realBoard.tile(100); // Last tile

            winner.moveTo(endTile);

            boolean hasWon = ruleEngine.hasWon(winner, mockGame);

            assertTrue(hasWon);
        }

        @Test
        void shouldReturnFalseWhenPlayerNotAtEnd() {
            Player notWinner = createTestPlayer("NotWinner", Token.RED);
            Tile middleTile = realBoard.tile(50);

            notWinner.moveTo(middleTile);

            boolean hasWon = ruleEngine.hasWon(notWinner, mockGame);

            assertFalse(hasWon);
        }

        @Test
        void shouldReturnFalseWhenPlayerAtStart() {
            Player atStart = createTestPlayer("AtStart", Token.GREEN);
            atStart.moveTo(realBoard.start());

            boolean hasWon = ruleEngine.hasWon(atStart, mockGame);

            assertFalse(hasWon);
        }
    }

    @Nested
    class WinConditionsEdgeCases {

        @Test
        void shouldReturnFalseForNullPlayer() {
            boolean hasWon = ruleEngine.hasWon(null, mockGame);

            assertFalse(hasWon);
        }

        @Test
        void shouldReturnFalseWhenPlayerCurrentTileIsNull() {
            Player noTile = createTestPlayer("NoTile", Token.YELLOW);
            // Player's current tile is null by default

            boolean hasWon = ruleEngine.hasWon(noTile, mockGame);

            assertFalse(hasWon);
        }

        @Test
        void shouldReturnFalseWhenGameIsNull() {
            Player player = createTestPlayer("Player", Token.PURPLE);

            boolean hasWon = ruleEngine.hasWon(player, null);

            assertFalse(hasWon);
        }

        @Test
        void shouldReturnFalseWhenBoardIsNull() {
            Player player = createTestPlayer("Player", Token.BLUE);
            when(mockGame.getBoard()).thenReturn(null);

            boolean hasWon = ruleEngine.hasWon(player, mockGame);

            assertFalse(hasWon);
        }
    }

    @Nested
    class EdgeCasesWithIntegrationTesting {

        @Test
        void shouldHandleTileWithBothSnakeAndLadder() {
            // Test the rule engine's behavior when same tile has both
            Map<Integer, Integer> conflictSnakes = Map.of(10, 5);
            Map<Integer, Integer> conflictLadders = Map.of(10, 15);

            SnlRuleEngine conflictEngine = new SnlRuleEngine(conflictSnakes, conflictLadders);
            LinearBoard realBoard = new LinearBoard(50);

            when(mockGame.getBoard()).thenReturn(realBoard);
            when(mockGame.getPlayers()).thenReturn(List.of());

            Player player = createTestPlayer("TestPlayer", Token.RED);
            Tile conflictTile = realBoard.tile(10);
            player.moveTo(conflictTile);

            // Test that it doesn't crash and moves somewhere reasonable
            assertDoesNotThrow(() -> conflictEngine.applyPostLandingEffects(player, null, conflictTile, mockGame));

            // Should have moved to either snake destination (5) or ladder destination (15)
            int finalPosition = player.getCurrentTile().tileId();
            assertTrue(finalPosition == 5 || finalPosition == 15,
                    "Player should be at snake (5) or ladder (15) destination, but was at: " + finalPosition);
        }

        @Test
        void shouldHandleEmptySnakesAndLadders() {
            SnlRuleEngine emptyEngine = new SnlRuleEngine(Map.of(), Map.of());
            LinearBoard realBoard = new LinearBoard(20);

            when(mockGame.getBoard()).thenReturn(realBoard);
            when(mockGame.getPlayers()).thenReturn(List.of());

            Player player = createTestPlayer("TestPlayer", Token.BLUE);
            Tile regularTile = realBoard.tile(10);
            player.moveTo(regularTile);

            emptyEngine.applyPostLandingEffects(player, null, regularTile, mockGame);

            // Player should remain on same tile (no snakes or ladders)
            assertEquals(regularTile, player.getCurrentTile());
        }
    }

    private Player createTestPlayer(String name, Token token) {
        return new Player(name, token, LocalDate.of(1990, 1, 1));
    }
}