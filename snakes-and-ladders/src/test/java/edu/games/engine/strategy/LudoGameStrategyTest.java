package edu.games.engine.strategy;

import edu.games.engine.board.LudoBoard;
import edu.games.engine.board.LudoPath;
import edu.games.engine.board.Tile;
import edu.games.engine.dice.Dice;
import edu.games.engine.exception.ValidationException;
import edu.games.engine.impl.DefaultGame;
import edu.games.engine.model.LudoColor;
import edu.games.engine.model.Player;
import edu.games.engine.model.PlayerPiece;
import edu.games.engine.model.Token;
import edu.games.engine.rule.LudoRuleEngine;
import edu.games.engine.rule.RuleEngine;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Alternative Ludo strategy tests that avoid null handling issues
 * and focus on real behavior testing
 */
class LudoGameStrategyTest {

    private LudoGameStrategy strategy;

    @Mock
    private RuleEngine mockRuleEngine;

    @Mock
    private DefaultGame mockGame;

    @Mock
    private Dice mockDice;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        strategy = new LudoGameStrategy(mockRuleEngine);
        when(mockGame.getDice()).thenReturn(mockDice);
    }

    @Nested
    class GameInitializationWithRealObjects {

        @Test
        void shouldMoveAllPiecesToHomeWhenInitializing() {
            Player player1 = createTestPlayer("Alice", Token.BLUE);
            Player player2 = createTestPlayer("Bob", Token.RED);
            List<Player> players = Arrays.asList(player1, player2);

            when(mockGame.getPlayers()).thenReturn(players);

            // Move some pieces to board first
            Tile someTile = mock(Tile.class);
            player1.getPiece(0).moveTo(someTile);
            player2.getPiece(1).moveTo(someTile);

            // Verify pieces are not at home initially
            assertFalse(player1.getPiece(0).isAtHome());
            assertFalse(player2.getPiece(1).isAtHome());

            strategy.initializeGame(mockGame);

            // All pieces should be back home (null tile)
            assertTrue(player1.getPiece(0).isAtHome());
            assertTrue(player1.getPiece(1).isAtHome());
            assertTrue(player1.getPiece(2).isAtHome());
            assertTrue(player1.getPiece(3).isAtHome());

            assertTrue(player2.getPiece(0).isAtHome());
            assertTrue(player2.getPiece(1).isAtHome());
            assertTrue(player2.getPiece(2).isAtHome());
            assertTrue(player2.getPiece(3).isAtHome());
        }

        @Test
        void shouldHandleEmptyPlayerList() {
            when(mockGame.getPlayers()).thenReturn(List.of());

            assertDoesNotThrow(() -> strategy.initializeGame(mockGame));
        }

        @Test
        void shouldInitializeGameWithSinglePlayer() {
            Player singlePlayer = createTestPlayer("Solo", Token.GREEN);
            when(mockGame.getPlayers()).thenReturn(List.of(singlePlayer));

            // Place pieces on board
            Tile someTile = mock(Tile.class);
            singlePlayer.getPiece(0).moveTo(someTile);
            singlePlayer.getPiece(2).moveTo(someTile);

            strategy.initializeGame(mockGame);

            // All pieces should be home
            for (int i = 0; i < 4; i++) {
                assertTrue(singlePlayer.getPiece(i).isAtHome());
            }
        }
    }

    @Nested
    class DiceRollProcessing {

        @Test
        void shouldDelegateToRuleEngineForExtraTurn() {
            Player player = createTestPlayer("Alice", Token.BLUE);
            List<Integer> diceValues = List.of(6);

            when(mockDice.lastValues()).thenReturn(diceValues);
            when(mockRuleEngine.grantsExtraTurn(player, diceValues, mockGame)).thenReturn(true);

            boolean result = strategy.processDiceRoll(player, 6, mockGame);

            assertTrue(result);
            verify(mockRuleEngine).grantsExtraTurn(player, diceValues, mockGame);
        }

        @Test
        void shouldReturnFalseWhenRuleEngineReturnsFalse() {
            Player player = createTestPlayer("Bob", Token.RED);
            List<Integer> diceValues = List.of(4);

            when(mockDice.lastValues()).thenReturn(diceValues);
            when(mockRuleEngine.grantsExtraTurn(player, diceValues, mockGame)).thenReturn(false);

            boolean result = strategy.processDiceRoll(player, 4, mockGame);

            assertFalse(result);
        }

        @Test
        void shouldHandleDifferentDiceValues() {
            Player player = createTestPlayer("Test", Token.YELLOW);

            // Test multiple dice values
            when(mockDice.lastValues()).thenReturn(List.of(1, 6));
            when(mockRuleEngine.grantsExtraTurn(any(), any(), any())).thenReturn(true);

            boolean result = strategy.processDiceRoll(player, 7, mockGame);

            assertTrue(result);
            verify(mockRuleEngine).grantsExtraTurn(player, List.of(1, 6), mockGame);
        }
    }

    @Nested
    class PieceMovementWithRealBoard {

        private LudoBoard realBoard;
        private Player bluePlayer;

        @BeforeEach
        void setUp() {
            LudoPath ludoPath = new LudoPath();
            realBoard = new LudoBoard(ludoPath);
            bluePlayer = createTestPlayer("BluePlayer", Token.BLUE);

            when(mockGame.getBoard()).thenReturn(realBoard);
        }

        @Test
        void shouldMoveFromHomeWithSix() {
            // Piece starts at home (null tile)
            PlayerPiece piece = bluePlayer.getPiece(0);
            assertTrue(piece.isAtHome());

            Tile result = strategy.movePiece(bluePlayer, 0, 6, mockGame);

            assertNotNull(result);
            assertEquals(realBoard.getStartTile(LudoColor.BLUE), result);
        }

        @Test
        void shouldNotMoveFromHomeWithoutSix() {
            PlayerPiece piece = bluePlayer.getPiece(0);
            assertTrue(piece.isAtHome());

            Tile result = strategy.movePiece(bluePlayer, 0, 4, mockGame);

            assertNull(result);
        }

        @Test
        void shouldMoveOnBoardPieceNormally() {
            PlayerPiece piece = bluePlayer.getPiece(0);
            Tile startTile = realBoard.getStartTile(LudoColor.BLUE);
            piece.moveTo(startTile);

            Tile result = strategy.movePiece(bluePlayer, 0, 3, mockGame);

            assertNotNull(result);
            assertNotEquals(startTile, result);
        }

        @Test
        void shouldHandleDifferentColorTokens() {
            Player redPlayer = createTestPlayer("RedPlayer", Token.RED);

            Tile result = strategy.movePiece(redPlayer, 0, 6, mockGame);

            assertEquals(realBoard.getStartTile(LudoColor.RED), result);
        }
    }

    @Nested
    class ValidationAndErrorHandling {

        private LudoBoard realBoard;

        @BeforeEach
        void setUp() {
            realBoard = new LudoBoard(new LudoPath());
            when(mockGame.getBoard()).thenReturn(realBoard);
        }

        @Test
        void shouldThrowExceptionForInvalidPieceIndex() {
            Player player = createTestPlayer("Test", Token.BLUE);

            assertThrows(ValidationException.class, () -> strategy.movePiece(player, -1, 6, mockGame));

            assertThrows(ValidationException.class, () -> strategy.movePiece(player, 4, 6, mockGame));
        }

        @Test
        void shouldThrowExceptionForNullPlayer() {
            assertThrows(ValidationException.class, () -> strategy.movePiece(null, 0, 6, mockGame));
        }

        @Test
        void shouldThrowExceptionForNullGame() {
            Player player = createTestPlayer("Test", Token.BLUE);

            assertThrows(ValidationException.class, () -> strategy.movePiece(player, 0, 6, null));
        }

        @Test
        void shouldThrowExceptionForNonLudoBoard() {
            Player player = createTestPlayer("Test", Token.BLUE);
            when(mockGame.getBoard()).thenReturn(mock(edu.games.engine.board.Board.class));

            assertThrows(ValidationException.class, () -> strategy.movePiece(player, 0, 6, mockGame));
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
        void shouldDelegateToRuleEngineForSpecialRules() {
            Player player = createTestPlayer("Player", Token.BLUE);
            PlayerPiece piece = player.getPiece(0);
            Tile destinationTile = mock(Tile.class);

            strategy.applySpecialRules(player, piece, destinationTile, mockGame);

            verify(mockRuleEngine).applyPostLandingEffects(player, piece, destinationTile, mockGame);
        }

        @Test
        void shouldPassThroughAllParameters() {
            Player player = createTestPlayer("Test", Token.RED);
            PlayerPiece piece = player.getPiece(2);
            Tile tile = mock(Tile.class);

            strategy.applySpecialRules(player, piece, tile, mockGame);

            verify(mockRuleEngine).applyPostLandingEffects(player, piece, tile, mockGame);
        }
    }

    @Nested
    class ComprehensiveBehaviorTests {

        @Test
        void shouldHandleAllTokenColors() {
            LudoBoard realBoard = new LudoBoard(new LudoPath());
            when(mockGame.getBoard()).thenReturn(realBoard);

            for (Token token : new Token[] { Token.BLUE, Token.RED, Token.GREEN, Token.YELLOW }) {
                Player player = createTestPlayer("Player", token);
                LudoColor expectedColor = LudoColor.valueOf(token.name());

                Tile result = strategy.movePiece(player, 0, 6, mockGame);

                assertEquals(realBoard.getStartTile(expectedColor), result);
            }
        }

        @Test
        void shouldHandleAllPieceIndices() {
            Player player = createTestPlayer("Player", Token.BLUE);
            when(mockGame.getBoard()).thenReturn(new LudoBoard(new LudoPath()));

            for (int i = 0; i < 4; i++) {
                final int pieceIndex = i;
                assertDoesNotThrow(() -> strategy.movePiece(player, pieceIndex, 6, mockGame));
            }
        }

        @Test
        void shouldHandleZeroDiceValue() {
            Player player = createTestPlayer("Player", Token.RED);
            LudoBoard realBoard = new LudoBoard(new LudoPath());
            when(mockGame.getBoard()).thenReturn(realBoard);

            PlayerPiece piece = player.getPiece(0);
            Tile startTile = realBoard.getStartTile(LudoColor.RED);
            piece.moveTo(startTile);

            Tile result = strategy.movePiece(player, 0, 0, mockGame);

            // Should return the same tile (no movement)
            assertEquals(startTile, result);
        }
    }

    @Nested
    class IntegrationWithRealRuleEngine {

        @Test
        void shouldWorkWithRealRuleEngine() {
            // Test with actual rule engine to ensure integration works
            LudoPath ludoPath = new LudoPath();
            LudoRuleEngine realRuleEngine = new LudoRuleEngine(ludoPath);
            LudoGameStrategy realStrategy = new LudoGameStrategy(realRuleEngine);
            LudoBoard realBoard = new LudoBoard(ludoPath);

            when(mockGame.getBoard()).thenReturn(realBoard);
            when(mockDice.lastValues()).thenReturn(List.of(6));

            Player player = createTestPlayer("Integration", Token.BLUE);

            // Test dice roll processing
            boolean extraTurn = realStrategy.processDiceRoll(player, 6, mockGame);
            assertTrue(extraTurn); // Should get extra turn for rolling 6

            // Test piece movement
            Tile result = realStrategy.movePiece(player, 0, 6, mockGame);
            assertNotNull(result);
            assertEquals(realBoard.getStartTile(LudoColor.BLUE), result);
        }
    }

    private Player createTestPlayer(String name, Token token) {
        return new Player(name, token, LocalDate.of(1990, 1, 1));
    }
}