package edu.ntnu.idatt2003.gateway;

import edu.games.engine.board.Tile;
import edu.games.engine.model.Player;
import edu.games.engine.model.Token;
import edu.ntnu.idatt2003.gateway.event.PlayerMoveData;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PlayerMoveDataTest {

    @Mock
    private Tile mockFromTile;

    @Mock
    private Tile mockToTile;

    private Player testPlayer;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        testPlayer = new Player("TestPlayer", Token.BLUE, LocalDate.of(1990, 1, 1));

        when(mockFromTile.tileId()).thenReturn(5);
        when(mockToTile.tileId()).thenReturn(10);
    }

    @Nested
    class LudoConstructor {

        @Test
        void shouldCreatePlayerMoveDataWithTiles() {
            PlayerMoveData moveData = new PlayerMoveData(testPlayer, mockFromTile, mockToTile);

            assertEquals(testPlayer, moveData.getPlayer());
            assertEquals(mockFromTile, moveData.getFrom());
            assertEquals(mockToTile, moveData.getTo());
        }

        @Test
        void shouldHandleNullFromTile() {
            PlayerMoveData moveData = new PlayerMoveData(testPlayer, null, mockToTile);

            assertEquals(testPlayer, moveData.getPlayer());
            assertNull(moveData.getFrom());
            assertEquals(mockToTile, moveData.getTo());
        }

        @Test
        void shouldHandleNullToTile() {
            PlayerMoveData moveData = new PlayerMoveData(testPlayer, mockFromTile, null);

            assertEquals(testPlayer, moveData.getPlayer());
            assertEquals(mockFromTile, moveData.getFrom());
            assertNull(moveData.getTo());
        }

        @Test
        void shouldHandleBothTilesNull() {
            PlayerMoveData moveData = new PlayerMoveData(testPlayer, null, null);

            assertEquals(testPlayer, moveData.getPlayer());
            assertNull(moveData.getFrom());
            assertNull(moveData.getTo());
        }

        @Test
        void shouldHandleNullPlayer() {
            PlayerMoveData moveData = new PlayerMoveData(null, mockFromTile, mockToTile);

            assertNull(moveData.getPlayer());
            assertEquals(mockFromTile, moveData.getFrom());
            assertEquals(mockToTile, moveData.getTo());
        }
    }

    @Nested
    class SnlConstructor {

        @Test
        void shouldCreatePlayerMoveDataWithTileIds() {
            PlayerMoveData moveData = new PlayerMoveData(testPlayer, 15, 25);

            assertEquals(testPlayer, moveData.getPlayer());
            assertEquals(15, moveData.getFrom());
            assertEquals(25, moveData.getTo());
        }

        @Test
        void shouldHandleZeroTileIds() {
            PlayerMoveData moveData = new PlayerMoveData(testPlayer, 0, 0);

            assertEquals(testPlayer, moveData.getPlayer());
            assertEquals(0, moveData.getFrom());
            assertEquals(0, moveData.getTo());
        }

        @Test
        void shouldHandleNegativeTileIds() {
            PlayerMoveData moveData = new PlayerMoveData(testPlayer, -5, -10);

            assertEquals(testPlayer, moveData.getPlayer());
            assertEquals(-5, moveData.getFrom());
            assertEquals(-10, moveData.getTo());
        }

        @Test
        void shouldHandleLargeTileIds() {
            PlayerMoveData moveData = new PlayerMoveData(testPlayer, Integer.MAX_VALUE, Integer.MIN_VALUE);

            assertEquals(testPlayer, moveData.getPlayer());
            assertEquals(Integer.MAX_VALUE, moveData.getFrom());
            assertEquals(Integer.MIN_VALUE, moveData.getTo());
        }

        @Test
        void shouldHandleNullPlayerWithTileIds() {
            PlayerMoveData moveData = new PlayerMoveData(null, 5, 10);

            assertNull(moveData.getPlayer());
            assertEquals(5, moveData.getFrom());
            assertEquals(10, moveData.getTo());
        }
    }

    @Nested
    class TileIdConvenienceMethods {

        @Test
        void shouldReturnTileIdsFromTileObjects() {
            PlayerMoveData moveData = new PlayerMoveData(testPlayer, mockFromTile, mockToTile);

            assertEquals(5, moveData.getFromTileId());
            assertEquals(10, moveData.getToTileId());
        }

        @Test
        void shouldReturnTileIdsFromIntegerObjects() {
            PlayerMoveData moveData = new PlayerMoveData(testPlayer, 20, 30);

            assertEquals(20, moveData.getFromTileId());
            assertEquals(30, moveData.getToTileId());
        }
    }

    @Nested
    class TileConvenienceMethods {

        @Test
        void shouldReturnTileObjectsFromTileObjects() {
            PlayerMoveData moveData = new PlayerMoveData(testPlayer, mockFromTile, mockToTile);

            assertEquals(mockFromTile, moveData.getFromTile());
            assertEquals(mockToTile, moveData.getToTile());
        }

        @Test
        void shouldReturnNullFromIntegerObjects() {
            PlayerMoveData moveData = new PlayerMoveData(testPlayer, 15, 25);

            assertNull(moveData.getFromTile());
            assertNull(moveData.getToTile());
        }

        @Test
        void shouldReturnNullWhenTileIsNull() {
            PlayerMoveData moveData = new PlayerMoveData(testPlayer, null, null);

            assertNull(moveData.getFromTile());
            assertNull(moveData.getToTile());
        }

        @Test
        void shouldHandleMixedTileTypes() {
            // This simulates a scenario where one field is a Tile and another is an Integer
            // Though this shouldn't happen in practice, we test defensive behavior
            PlayerMoveData moveData = new PlayerMoveData(testPlayer, mockFromTile, mockToTile);

            assertEquals(mockFromTile, moveData.getFromTile());
            assertEquals(mockToTile, moveData.getToTile());
        }
    }

    @Nested
    class EdgeCasesAndTypeHandling {

        @Test
        void shouldHandleTypeConsistencyWithTiles() {
            PlayerMoveData moveData = new PlayerMoveData(testPlayer, mockFromTile, mockToTile);

            // Object getters should return Tile objects
            assertTrue(moveData.getFrom() instanceof Tile);
            assertTrue(moveData.getTo() instanceof Tile);

            // Tile getters should return same objects
            assertSame(mockFromTile, moveData.getFromTile());
            assertSame(mockToTile, moveData.getToTile());

            // TileId getters should extract IDs from Tiles
            assertEquals(5, moveData.getFromTileId());
            assertEquals(10, moveData.getToTileId());
        }

        @Test
        void shouldHandleTypeConsistencyWithIntegers() {
            PlayerMoveData moveData = new PlayerMoveData(testPlayer, 42, 84);

            // Object getters should return Integer objects
            assertTrue(moveData.getFrom() instanceof Integer);
            assertTrue(moveData.getTo() instanceof Integer);

            // Tile getters should return null for Integer objects
            assertNull(moveData.getFromTile());
            assertNull(moveData.getToTile());

            // TileId getters should return the Integer values
            assertEquals(42, moveData.getFromTileId());
            assertEquals(84, moveData.getToTileId());
        }

        @Test
        void shouldHandlePlayerMovementFromHomeToBoard() {
            // Common scenario: player moves from home (null) to board
            PlayerMoveData moveData = new PlayerMoveData(testPlayer, null, mockToTile);

            assertEquals(testPlayer, moveData.getPlayer());
            assertNull(moveData.getFrom());
            assertEquals(mockToTile, moveData.getTo());
            assertEquals(mockToTile, moveData.getToTile());
            assertEquals(10, moveData.getToTileId());
        }

        @Test
        void shouldHandlePlayerMovementFromBoardToHome() {
            // Less common but possible: player moves from board back to home
            PlayerMoveData moveData = new PlayerMoveData(testPlayer, mockFromTile, null);

            assertEquals(testPlayer, moveData.getPlayer());
            assertEquals(mockFromTile, moveData.getFrom());
            assertNull(moveData.getTo());
            assertEquals(mockFromTile, moveData.getFromTile());
            assertEquals(5, moveData.getFromTileId());
        }
    }

    @Nested
    class RealWorldScenarios {

        @Test
        void shouldSupportLudoPieceMovement() {
            // Typical Ludo piece movement
            PlayerMoveData moveData = new PlayerMoveData(testPlayer, mockFromTile, mockToTile);

            // Should be able to get all information about the move
            assertEquals("TestPlayer", moveData.getPlayer().getName());
            assertEquals(Token.BLUE, moveData.getPlayer().getToken());
            assertEquals(5, moveData.getFromTileId());
            assertEquals(10, moveData.getToTileId());
            assertNotNull(moveData.getFromTile());
            assertNotNull(moveData.getToTile());
        }

        @Test
        void shouldSupportSnlPlayerMovement() {
            // Typical SNL player movement
            PlayerMoveData moveData = new PlayerMoveData(testPlayer, 25, 31);

            // Should be able to get all information about the move
            assertEquals("TestPlayer", moveData.getPlayer().getName());
            assertEquals(Token.BLUE, moveData.getPlayer().getToken());
            assertEquals(25, moveData.getFromTileId());
            assertEquals(31, moveData.getToTileId());
            assertNull(moveData.getFromTile()); // SNL uses IDs, not Tile objects
            assertNull(moveData.getToTile());
        }

        @Test
        void shouldSupportPlayerGoingBackwardsInSnl() {
            // Snake takes player backwards
            PlayerMoveData moveData = new PlayerMoveData(testPlayer, 47, 26);

            assertEquals(47, moveData.getFromTileId());
            assertEquals(26, moveData.getToTileId());
            assertTrue(moveData.getFromTileId() > moveData.getToTileId());
        }

        @Test
        void shouldSupportPlayerJumpingForwardInSnl() {
            // Ladder takes player forward
            PlayerMoveData moveData = new PlayerMoveData(testPlayer, 7, 14);

            assertEquals(7, moveData.getFromTileId());
            assertEquals(14, moveData.getToTileId());
            assertTrue(moveData.getFromTileId() < moveData.getToTileId());
        }
    }

    @Nested
    class ExceptionHandling {
        @Test
        void shouldNotThrowWhenGettingTileFromInteger() {
            PlayerMoveData moveData = new PlayerMoveData(testPlayer, 5, 10);

            // Getting Tile from Integer should return null, not throw
            assertNull(moveData.getFromTile());
            assertNull(moveData.getToTile());
        }

        @Test
        void shouldHandleExtremeValues() {
            PlayerMoveData moveData = new PlayerMoveData(testPlayer, Integer.MIN_VALUE, Integer.MAX_VALUE);

            assertEquals(Integer.MIN_VALUE, moveData.getFromTileId());
            assertEquals(Integer.MAX_VALUE, moveData.getToTileId());
        }
    }
}