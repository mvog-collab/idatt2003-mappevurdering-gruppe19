package edu.ntnu.idatt2003.models;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class BoardTest {

    private Board board;

    @BeforeEach
    void setUp() {
        board = new Board(10);
    }

    @Nested
    @DisplayName("Constructor and Size Tests")
    class ConstructorTests {

        @Test
        @DisplayName("Board should be created with the correct size")
        void testBoardSize() {
            assertEquals(10, board.getSize());
        }
    }

    @Nested
    @DisplayName("addTile Tests")
    class AddTileTests {

        @Test
        @DisplayName("addTile should add a tile to the board")
        void testAddTile() {
            Tile tile = new Tile(0);
            board.addTile(tile);
            assertEquals(tile, board.getTile(0));
        }

        @Test
        @DisplayName("addTile should throw exception when null is passed")
        void testAddTileNull() {
            assertThrows(IllegalArgumentException.class, () -> board.addTile(null));
        }

        @Test
        @DisplayName("addTile should set the nextTile of the previous tile correctly")
        void testSetNextTileOnAdd() {
            Tile tile0 = new Tile(0);
            Tile tile1 = new Tile(1);
            board.addTile(tile0);
            board.addTile(tile1);
            assertEquals(tile1, tile0.getNextTile());
        }
    }

    @Nested
    @DisplayName("getTile Tests")
    class GetTileTests {

        @Test
        @DisplayName("getTile should return null for a non-existing tile")
        void testGetNonExistingTile() {
            assertNull(board.getTile(5));
        }
    }
}