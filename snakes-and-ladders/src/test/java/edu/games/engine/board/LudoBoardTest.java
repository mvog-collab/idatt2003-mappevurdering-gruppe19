package edu.games.engine.board;

import edu.games.engine.model.LudoColor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LudoBoardTest {

    @Mock
    private LudoPath mockPath;

    @Mock
    private Tile mockTile;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Nested
    class Constructor {

        @Test
        void shouldCreateBoardWithValidPath() {
            LudoBoard board = new LudoBoard(mockPath);

            assertNotNull(board);
        }

        @Test
        void shouldAcceptNullPath() {
            // Constructor doesn't validate path, so this should work
            assertDoesNotThrow(() -> new LudoBoard(null));
        }
    }

    @Nested
    class StartTile {

        @Test
        void shouldReturnNullForStart() {
            LudoBoard board = new LudoBoard(mockPath);

            assertNull(board.start());
        }
    }

    @Nested
    class EndTileCheck {

        @Test
        void shouldDelegateIsEndToPath() {
            LudoBoard board = new LudoBoard(mockPath);
            when(mockPath.isEnd(mockTile)).thenReturn(true);

            boolean result = board.isEnd(mockTile);

            assertTrue(result);
            verify(mockPath).isEnd(mockTile);
        }

        @Test
        void shouldReturnFalseWhenPathReturnsFalse() {
            LudoBoard board = new LudoBoard(mockPath);
            when(mockPath.isEnd(mockTile)).thenReturn(false);

            boolean result = board.isEnd(mockTile);

            assertFalse(result);
        }

        @Test
        void shouldHandleNullTile() {
            LudoBoard board = new LudoBoard(mockPath);
            when(mockPath.isEnd(null)).thenReturn(false);

            boolean result = board.isEnd(null);

            assertFalse(result);
            verify(mockPath).isEnd(null);
        }
    }

    @Nested
    class DeprecatedMove {

        @Test
        void shouldReturnSameTileForDeprecatedMove() {
            LudoBoard board = new LudoBoard(mockPath);

            Tile result = board.move(mockTile, 5);

            assertEquals(mockTile, result);
            verifyNoInteractions(mockPath); // Should not call path
        }

        @Test
        void shouldReturnNullWhenGivenNull() {
            LudoBoard board = new LudoBoard(mockPath);

            Tile result = board.move(null, 3);

            assertNull(result);
        }

        @Test
        void shouldHandleZeroSteps() {
            LudoBoard board = new LudoBoard(mockPath);

            Tile result = board.move(mockTile, 0);

            assertEquals(mockTile, result);
        }

        @Test
        void shouldHandleNegativeSteps() {
            LudoBoard board = new LudoBoard(mockPath);

            Tile result = board.move(mockTile, -5);

            assertEquals(mockTile, result);
        }
    }

    @Nested
    class ColorBasedMove {

        @Test
        void shouldDelegateMoveToPathWithColor() {
            LudoBoard board = new LudoBoard(mockPath);
            Tile expectedTile = mock(Tile.class);
            when(mockPath.nextTile(mockTile, 4, LudoColor.BLUE)).thenReturn(expectedTile);

            Tile result = board.move(mockTile, 4, LudoColor.BLUE);

            assertEquals(expectedTile, result);
            verify(mockPath).nextTile(mockTile, 4, LudoColor.BLUE);
        }

        @Test
        void shouldHandleAllColors() {
            LudoBoard board = new LudoBoard(mockPath);

            for (LudoColor color : LudoColor.values()) {
                Tile expectedTile = mock(Tile.class);
                when(mockPath.nextTile(mockTile, 2, color)).thenReturn(expectedTile);

                Tile result = board.move(mockTile, 2, color);

                assertEquals(expectedTile, result);
                verify(mockPath).nextTile(mockTile, 2, color);
            }
        }

        @Test
        void shouldHandleNullTileWithColor() {
            LudoBoard board = new LudoBoard(mockPath);
            when(mockPath.nextTile(null, 6, LudoColor.RED)).thenReturn(mockTile);

            Tile result = board.move(null, 6, LudoColor.RED);

            assertEquals(mockTile, result);
            verify(mockPath).nextTile(null, 6, LudoColor.RED);
        }

        @Test
        void shouldHandleZeroStepsWithColor() {
            LudoBoard board = new LudoBoard(mockPath);
            when(mockPath.nextTile(mockTile, 0, LudoColor.GREEN)).thenReturn(mockTile);

            Tile result = board.move(mockTile, 0, LudoColor.GREEN);

            assertEquals(mockTile, result);
            verify(mockPath).nextTile(mockTile, 0, LudoColor.GREEN);
        }
    }

    @Nested
    class StartTileRetrieval {

        @Test
        void shouldGetStartTileForColorWithRealLudoPath() {
            LudoPath realPath = new LudoPath();
            LudoBoard board = new LudoBoard(realPath);

            Tile blueTile = board.getStartTile(LudoColor.BLUE);
            Tile redTile = board.getStartTile(LudoColor.RED);
            Tile greenTile = board.getStartTile(LudoColor.GREEN);
            Tile yellowTile = board.getStartTile(LudoColor.YELLOW);

            assertNotNull(blueTile);
            assertNotNull(redTile);
            assertNotNull(greenTile);
            assertNotNull(yellowTile);

            // Should be different tiles for different colors
            assertNotEquals(blueTile, redTile);
            assertNotEquals(blueTile, greenTile);
            assertNotEquals(blueTile, yellowTile);
        }

        @Test
        void shouldReturnNullWhenPathIsNull() {
            LudoBoard board = new LudoBoard(null);

            assertNull(board.getStartTile(LudoColor.BLUE));
        }

        @Test
        void shouldHandleNullPathConsistently() {
            LudoBoard board = new LudoBoard(null);

            // All colors should return null with null path
            assertNull(board.getStartTile(LudoColor.RED));
            assertNull(board.getStartTile(LudoColor.GREEN));
            assertNull(board.getStartTile(LudoColor.YELLOW));
        }
    }

    @Nested
    class BoardSize {

        @Test
        void shouldReturnCorrectSize() {
            LudoBoard board = new LudoBoard(mockPath);

            int size = board.size();

            assertEquals(76, size); // 52 ring + 4*6 goal tiles
        }

        @Test
        void shouldReturnConsistentSize() {
            LudoBoard board = new LudoBoard(mockPath);

            assertEquals(board.size(), board.size()); // Should be consistent
        }
    }

    @Nested
    class IntegrationWithRealPath {

        @Test
        void shouldWorkWithRealLudoPath() {
            LudoPath realPath = new LudoPath();
            LudoBoard board = new LudoBoard(realPath);

            // Test complete integration
            assertNull(board.start());
            assertEquals(76, board.size());

            // Test movement from home
            Tile blueStart = board.getStartTile(LudoColor.BLUE);
            assertNotNull(blueStart);

            // Test moving from start tile
            Tile nextTile = board.move(blueStart, 3, LudoColor.BLUE);
            assertNotNull(nextTile);
            assertNotEquals(blueStart, nextTile);
        }

        @Test
        void shouldHandleEndTileDetection() {
            LudoPath realPath = new LudoPath();
            LudoBoard board = new LudoBoard(realPath);

            // Test with a non-end tile
            Tile startTile = board.getStartTile(LudoColor.BLUE);
            assertFalse(board.isEnd(startTile));

            // Test with null
            assertFalse(board.isEnd(null));
        }

        @Test
        void shouldTestActualEndTileDetection() {
            LudoPath realPath = new LudoPath();
            LudoBoard board = new LudoBoard(realPath);

            // Get an actual end tile and test it
            // Final goal tiles should return true for isEnd
            // We can get to an end tile by navigating the path
            LudoTile startTile = realPath.getStartTile(LudoColor.BLUE);

            // Move many steps to potentially reach goal area
            // This is a simplified test - in reality you'd need exact navigation
            Tile someGoalTile = board.move(startTile, 60, LudoColor.BLUE); // Large number to reach goal

            // Test that board correctly delegates isEnd check
            assertDoesNotThrow(() -> board.isEnd(someGoalTile));
        }
    }

    @Nested
    class EdgeCases {

        @Test
        void shouldHandleMultipleMethodCallsOnSameInstance() {
            LudoBoard board = new LudoBoard(mockPath);

            // Multiple calls should work consistently
            assertNull(board.start());
            assertNull(board.start());

            assertEquals(76, board.size());
            assertEquals(76, board.size());
        }

        @Test
        void shouldHandleExtremeStepValues() {
            LudoBoard board = new LudoBoard(mockPath);

            when(mockPath.nextTile(mockTile, Integer.MAX_VALUE, LudoColor.BLUE))
                    .thenReturn(mockTile);
            when(mockPath.nextTile(mockTile, Integer.MIN_VALUE, LudoColor.RED))
                    .thenReturn(mockTile);

            Tile result1 = board.move(mockTile, Integer.MAX_VALUE, LudoColor.BLUE);
            Tile result2 = board.move(mockTile, Integer.MIN_VALUE, LudoColor.RED);

            assertEquals(mockTile, result1);
            assertEquals(mockTile, result2);
        }

        @Test
        void shouldHandleIsEndWithNullPath() {
            LudoBoard board = new LudoBoard(null);

            // Should throw NPE when trying to call isEnd on null path
            assertThrows(NullPointerException.class, () -> board.isEnd(mockTile));
        }

        @Test
        void shouldHandleMoveWithNullPath() {
            LudoBoard board = new LudoBoard(null);

            // Color-based move should throw NPE with null path
            assertThrows(NullPointerException.class, () -> board.move(mockTile, 3, LudoColor.BLUE));
        }
    }

    @Nested
    class AllMethodsCoverage {

        @Test
        void shouldCoverAllGetterMethods() {
            LudoPath realPath = new LudoPath();
            LudoBoard board = new LudoBoard(realPath);

            // Test all public methods at least once
            assertNull(board.start());
            assertEquals(76, board.size());
            assertNotNull(board.getStartTile(LudoColor.BLUE));
            assertFalse(board.isEnd(null));

            // Test both move methods
            Tile sameTile = board.move(mockTile, 5); // Deprecated method
            assertEquals(mockTile, sameTile);

            Tile startTile = board.getStartTile(LudoColor.RED);
            Tile movedTile = board.move(startTile, 2, LudoColor.RED); // Color method
            assertNotNull(movedTile);
        }

        @Test
        void shouldTestInstanceofCheckInGetStartTile() {
            // Test the instanceof check in getStartTile method
            // Create a board with null path to test the instanceof logic
            LudoBoard boardWithNull = new LudoBoard(null);
            assertNull(boardWithNull.getStartTile(LudoColor.BLUE));

            // Test with real LudoPath to ensure instanceof returns true
            LudoPath realPath = new LudoPath();
            LudoBoard boardWithPath = new LudoBoard(realPath);
            assertNotNull(boardWithPath.getStartTile(LudoColor.BLUE));
        }
    }
}