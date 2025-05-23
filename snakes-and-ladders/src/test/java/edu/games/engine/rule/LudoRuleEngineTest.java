package edu.games.engine.rule;

import edu.games.engine.board.LudoPath;
import edu.games.engine.board.Tile;
import edu.games.engine.impl.DefaultGame;
import edu.games.engine.model.LudoColor;
import edu.games.engine.model.Player;
import edu.games.engine.model.PlayerPiece;
import edu.games.engine.model.Token;
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

class LudoRuleEngineTest {

    private LudoRuleEngine ruleEngine;
    private LudoPath ludoPath;

    @Mock
    private DefaultGame mockGame;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        ludoPath = new LudoPath();
        ruleEngine = new LudoRuleEngine(ludoPath);
    }

    @Nested
    class ExtraTurnRules {

        @Test
        void shouldGrantExtraTurnWhenFirstDieShowsSix() {
            Player player = createTestPlayer("Alice", Token.BLUE);
            List<Integer> diceValues = Arrays.asList(6, 3);

            boolean result = ruleEngine.grantsExtraTurn(player, diceValues, mockGame);

            assertTrue(result);
        }

        @Test
        void shouldNotGrantExtraTurnWhenFirstDieIsNotSix() {
            Player player = createTestPlayer("Bob", Token.RED);
            List<Integer> diceValues = Arrays.asList(4, 2);

            boolean result = ruleEngine.grantsExtraTurn(player, diceValues, mockGame);

            assertFalse(result);
        }

        @Test
        void shouldNotGrantExtraTurnWhenDiceValuesEmpty() {
            Player player = createTestPlayer("Charlie", Token.GREEN);
            List<Integer> diceValues = List.of();

            boolean result = ruleEngine.grantsExtraTurn(player, diceValues, mockGame);

            assertFalse(result);
        }

        @Test
        void shouldGrantExtraTurnWithSingleSix() {
            Player player = createTestPlayer("Diana", Token.YELLOW);
            List<Integer> diceValues = List.of(6);

            boolean result = ruleEngine.grantsExtraTurn(player, diceValues, mockGame);

            assertTrue(result);
        }
    }

    @Nested
    class PostLandingEffects {

        private Player currentPlayer;
        private Player opponentPlayer;
        private List<Player> allPlayers;

        @BeforeEach
        void setUp() {
            currentPlayer = createTestPlayer("Current", Token.BLUE);
            opponentPlayer = createTestPlayer("Opponent", Token.RED);
            allPlayers = Arrays.asList(currentPlayer, opponentPlayer);

            when(mockGame.getPlayers()).thenReturn(allPlayers);
        }

        @Test
        void shouldBumpOpponentPieceFromMainRingTile() {
            Tile mainRingTile = ludoPath.ring().get(10); // Tile 11
            PlayerPiece currentPiece = currentPlayer.getPiece(0);
            PlayerPiece opponentPiece = opponentPlayer.getPiece(0);

            // Place opponent piece on the same tile
            opponentPiece.moveTo(mainRingTile);

            ruleEngine.applyPostLandingEffects(currentPlayer, currentPiece, mainRingTile, mockGame);

            assertTrue(opponentPiece.isAtHome());
        }

        @Test
        void shouldNotBumpOpponentPieceFromGoalTile() {
            Tile goalTile = ludoPath.goals().get(LudoColor.BLUE).get(0); // Blue goal tile
            PlayerPiece currentPiece = currentPlayer.getPiece(0);
            PlayerPiece opponentPiece = opponentPlayer.getPiece(0);

            // Place opponent piece on goal tile (shouldn't be possible but test defensive
            // code)
            opponentPiece.moveTo(goalTile);

            ruleEngine.applyPostLandingEffects(currentPlayer, currentPiece, goalTile, mockGame);

            // Opponent piece should remain (goal tiles don't allow bumping)
            assertFalse(opponentPiece.isAtHome());
        }

        @Test
        void shouldHandleNullPlayerGracefully() {
            Tile mainRingTile = ludoPath.ring().get(10);
            PlayerPiece piece = currentPlayer.getPiece(0);

            assertDoesNotThrow(() -> ruleEngine.applyPostLandingEffects(null, piece, mainRingTile, mockGame));
        }

        @Test
        void shouldHandleNullPieceGracefully() {
            Tile mainRingTile = ludoPath.ring().get(10);

            assertDoesNotThrow(() -> ruleEngine.applyPostLandingEffects(currentPlayer, null, mainRingTile, mockGame));
        }

        @Test
        void shouldHandleNullTileGracefully() {
            PlayerPiece piece = currentPlayer.getPiece(0);

            assertDoesNotThrow(() -> ruleEngine.applyPostLandingEffects(currentPlayer, piece, null, mockGame));
        }

        @Test
        void shouldHandleNullGameGracefully() {
            Tile mainRingTile = ludoPath.ring().get(10);
            PlayerPiece piece = currentPlayer.getPiece(0);

            assertDoesNotThrow(() -> ruleEngine.applyPostLandingEffects(currentPlayer, piece, mainRingTile, null));
        }
    }

    @Nested
    class WinConditions {

        @Test
        void shouldReturnTrueWhenAllPiecesInFinalGoal() {
            Player bluePlayer = createTestPlayer("Winner", Token.BLUE);
            Tile finalGoalTile = ludoPath.goals().get(LudoColor.BLUE).get(5); // Final blue goal tile

            // Move all pieces to final goal
            for (PlayerPiece piece : bluePlayer.getPieces()) {
                piece.moveTo(finalGoalTile);
            }

            boolean hasWon = ruleEngine.hasWon(bluePlayer, mockGame);

            assertTrue(hasWon);
        }

        @Test
        void shouldReturnFalseWhenNotAllPiecesInFinalGoal() {
            Player redPlayer = createTestPlayer("NotWinner", Token.RED);
            Tile finalGoalTile = ludoPath.goals().get(LudoColor.RED).get(5);
            Tile penultimateGoalTile = ludoPath.goals().get(LudoColor.RED).get(4);

            // Move 3 pieces to final goal, 1 to penultimate
            for (int i = 0; i < 3; i++) {
                redPlayer.getPiece(i).moveTo(finalGoalTile);
            }
            redPlayer.getPiece(3).moveTo(penultimateGoalTile);

            boolean hasWon = ruleEngine.hasWon(redPlayer, mockGame);

            assertFalse(hasWon);
        }

        @Test
        void shouldReturnFalseWhenPiecesAtHome() {
            Player greenPlayer = createTestPlayer("AtHome", Token.GREEN);
            // All pieces start at home (null tile)

            boolean hasWon = ruleEngine.hasWon(greenPlayer, mockGame);

            assertFalse(hasWon);
        }

        @Test
        void shouldReturnFalseWhenPiecesOnMainRing() {
            Player yellowPlayer = createTestPlayer("OnRing", Token.YELLOW);
            Tile ringTile = ludoPath.ring().get(20);

            // Place all pieces on main ring
            for (PlayerPiece piece : yellowPlayer.getPieces()) {
                piece.moveTo(ringTile);
            }

            boolean hasWon = ruleEngine.hasWon(yellowPlayer, mockGame);

            assertFalse(hasWon);
        }

        @Test
        void shouldReturnFalseForNullPlayer() {
            boolean hasWon = ruleEngine.hasWon(null, mockGame);

            assertFalse(hasWon);
        }

        @Test
        void shouldReturnFalseWhenPiecesInWrongColorGoal() {
            Player bluePlayer = createTestPlayer("WrongGoal", Token.BLUE);
            Tile redFinalGoal = ludoPath.goals().get(LudoColor.RED).get(5);

            // Place pieces in red goal instead of blue
            for (PlayerPiece piece : bluePlayer.getPieces()) {
                piece.moveTo(redFinalGoal);
            }

            boolean hasWon = ruleEngine.hasWon(bluePlayer, mockGame);

            assertFalse(hasWon);
        }
    }

    @Nested
    class EdgeCases {

        @Test
        void shouldHandleMultipleOpponentsOnSameTile() {
            Player currentPlayer = createTestPlayer("Current", Token.BLUE);
            Player opponent1 = createTestPlayer("Opponent1", Token.RED);
            Player opponent2 = createTestPlayer("Opponent2", Token.GREEN);
            List<Player> allPlayers = Arrays.asList(currentPlayer, opponent1, opponent2);

            when(mockGame.getPlayers()).thenReturn(allPlayers);

            Tile mainRingTile = ludoPath.ring().get(15);
            PlayerPiece currentPiece = currentPlayer.getPiece(0);
            PlayerPiece opponent1Piece = opponent1.getPiece(0);
            PlayerPiece opponent2Piece = opponent2.getPiece(0);

            // Place both opponents on same tile
            opponent1Piece.moveTo(mainRingTile);
            opponent2Piece.moveTo(mainRingTile);

            ruleEngine.applyPostLandingEffects(currentPlayer, currentPiece, mainRingTile, mockGame);

            // Both opponent pieces should be sent home
            assertTrue(opponent1Piece.isAtHome());
            assertTrue(opponent2Piece.isAtHome());
        }

        @Test
        void shouldNotBumpOwnPieces() {
            Player currentPlayer = createTestPlayer("SelfTest", Token.BLUE);
            List<Player> allPlayers = List.of(currentPlayer);

            when(mockGame.getPlayers()).thenReturn(allPlayers);

            Tile mainRingTile = ludoPath.ring().get(25);
            PlayerPiece piece1 = currentPlayer.getPiece(0);
            PlayerPiece piece2 = currentPlayer.getPiece(1);

            // Place own piece on the tile
            piece2.moveTo(mainRingTile);

            ruleEngine.applyPostLandingEffects(currentPlayer, piece1, mainRingTile, mockGame);

            // Own piece should not be bumped
            assertFalse(piece2.isAtHome());
            assertEquals(mainRingTile, piece2.getCurrentTile());
        }
    }

    private Player createTestPlayer(String name, Token token) {
        return new Player(name, token, LocalDate.of(1990, 1, 1));
    }
}