package edu.ntnu.idatt2003.models;

import static org.junit.jupiter.api.Assertions.*;

import edu.ntnu.idatt2003.game_logic.TileAction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class TileTest {

    private Tile tile;
    private DummyTileAction dummyAction;

    @BeforeEach
    void setUp() {
        // Create a tile with a valid tileId (non-negative)
        tile = new Tile(1);
        dummyAction = new DummyTileAction();
    }

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("Tile constructor should create a tile with valid tileId")
        void testTileConstructorValid() {
            Tile newTile = new Tile(2);
            assertNotNull(newTile);
            assertEquals(2, newTile.getTileId());
        }

        @Test
        @DisplayName("Tile constructor should throw exception for negative tileId")
        void testTileConstructorInvalid() {
            assertThrows(IllegalArgumentException.class, () -> new Tile(-1));
        }
    }

    @Nested
    @DisplayName("Player Landing and Removal Tests")
    class PlayerTests {

        private Player player;

        @BeforeEach
        void initPlayer() {
            player = new Player("TestPlayer", java.time.LocalDate.of(2000, 1, 1));
        }

        @Test
        @DisplayName("landPlayer should add a player to the tile")
        void testLandPlayer() {
            // Ensure that no exception is thrown.
            assertDoesNotThrow(() -> tile.landPlayer(player));
        }

        @Test
        @DisplayName("landPlayer should throw exception when given a null player")
        void testLandPlayerNull() {
            assertThrows(IllegalArgumentException.class, () -> tile.landPlayer(null));
        }

        @Test
        @DisplayName("removePlayerFromTile should remove a player from the tile")
        void testRemovePlayerFromTile() {
            // First add the player, then remove
            tile.landPlayer(player);
            assertDoesNotThrow(() -> tile.removePlayerFromTile(player));
        }

        @Test
        @DisplayName("removePlayerFromTile should throw exception when given a null player")
        void testRemovePlayerNull() {
            assertThrows(IllegalArgumentException.class, () -> tile.removePlayerFromTile(null));
        }
    }

    @Nested
    @DisplayName("Next Tile Tests")
    class NextTileTests {

        @Test
        @DisplayName("setNextTile and getNextTile should work correctly")
        void testSetAndGetNextTile() {
            Tile nextTile = new Tile(2);
            tile.setNextTile(nextTile);
            assertEquals(nextTile, tile.getNextTile());
        }

        @Test
        @DisplayName("setNextTile should throw exception when null is passed")
        void testSetNextTileNull() {
            assertThrows(IllegalArgumentException.class, () -> tile.setNextTile(null));
        }
    }

    @Nested
    @DisplayName("TileAction Tests")
    class TileActionTests {

        @Test
        @DisplayName("setAction and getAction should work correctly")
        void testSetAndGetAction() {
            tile.setAction(dummyAction);
            assertEquals(dummyAction, tile.getAction());
        }
    }

    // Dummy implementation for TileAction for testing purposes.
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