package edu.ntnu.idatt2003.models;

import static org.junit.jupiter.api.Assertions.*;

import edu.ntnu.idatt2003.game_logic.TileAction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

public class PlayerTest {

    private Player player;
    private Tile tile1;
    private Tile tile2;
    private DummyTileAction dummyAction;

    @BeforeEach
    void setUp() {
        player = new Player("Martha", LocalDate.of(2004, 1, 19));
        tile1 = new Tile(0);
        tile2 = new Tile(1);
        dummyAction = new DummyTileAction();
    }

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("Player constructor should create a valid player")
        void testValidConstructor() {
            Player p = new Player("Edvard", LocalDate.of(2003, 3, 27));
            assertNotNull(p);
            assertEquals("Edvard", p.getName());
            assertEquals(LocalDate.of(2003, 3, 27), p.getBirthday());
        }

        @Test
        @DisplayName("Player constructor should throw exception for null or empty name")
        void testConstructorInvalidName() {
            assertThrows(IllegalArgumentException.class, () -> new Player(null, LocalDate.of(2000, 1, 1)));
            assertThrows(IllegalArgumentException.class, () -> new Player("", LocalDate.of(2000, 1, 1)));
        }

        @Test
        @DisplayName("Player constructor should throw exception for birthday in the future")
        void testConstructorFutureBirthday() {
            LocalDate futureDate = LocalDate.now().plusDays(1);
            assertThrows(IllegalArgumentException.class, () -> new Player("TestUser", futureDate));
        }
    }

    @Nested
    @DisplayName("Tile Placement Tests")
    class TilePlacementTests {

        @Test
        @DisplayName("placeOnTile should set current tile and land the player on the tile")
        void testPlaceOnTile() {
            player.placeOnTile(tile1);
            assertEquals(tile1, player.getCurrentTile());

            // Place on a new tile; the player should move from the first to the second tile.
            player.placeOnTile(tile2);
            assertEquals(tile2, player.getCurrentTile());
        }
    }

    @Nested
    @DisplayName("Move Tests")
    class MoveTests {

        private Tile createChainOfTiles(int count) {
            Tile first = new Tile(0);
            Tile current = first;
            for (int i = 1; i < count; i++) {
                Tile next = new Tile(i);
                current.setNextTile(next);
                current = next;
            }
            return first;
        }

        @Test
        @DisplayName("move should throw exception when steps are less than 2")
        void testMoveInvalidSteps() {
            player.placeOnTile(tile1);
            assertThrows(IllegalArgumentException.class, () -> player.move(1));
        }

        @Test
        @DisplayName("move should update the player's current tile correctly")
        void testMoveValidSteps() {
            Tile chainStart = createChainOfTiles(5);
            player.placeOnTile(chainStart);
            // Moving 3 steps should take the player from tile 0 to tile 3.
            player.move(3);
            assertEquals(3, player.getCurrentTile().getTileId());
        }

        @Test
        @DisplayName("move should apply the tile action if present on the target tile")
        void testMoveWithTileAction() {
            Tile chainStart = createChainOfTiles(4);
            // Set a dummy action on tile 3.
            Tile targetTile = chainStart;
            for (int i = 0; i < 3; i++) {
                targetTile = targetTile.getNextTile();
            }
            targetTile.setAction(dummyAction);
            player.placeOnTile(chainStart);
            player.move(3);
            // After moving, the dummy action should have been applied.
            assertTrue(dummyAction.isApplied());
        }
    }

    @Nested
    @DisplayName("setCurrentTile Tests")
    class SetCurrentTileTests {

        @Test
        @DisplayName("setCurrentTile should update the current tile when valid")
        void testSetCurrentTileValid() {
            player.setCurrentTile(tile1);
            assertEquals(tile1, player.getCurrentTile());
        }

        @Test
        @DisplayName("setCurrentTile should throw exception when null is passed")
        void testSetCurrentTileNull() {
            assertThrows(IllegalArgumentException.class, () -> player.setCurrentTile(null));
        }
    }

    // Dummy implementation of TileAction for testing purposes.
    private static class DummyTileAction implements TileAction {
        private boolean applied = false;

        @Override
        public void applyAction(Player player) {
            applied = true;
        }

        @Override
        public int getActionPosition() {
            return 0;
        }

        public boolean isApplied() {
            return applied;
        }
    }
}
