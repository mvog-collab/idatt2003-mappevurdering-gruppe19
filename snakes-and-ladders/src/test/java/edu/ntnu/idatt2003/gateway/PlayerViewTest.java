package edu.ntnu.idatt2003.gateway;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import edu.ntnu.idatt2003.gateway.view.PlayerView;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PlayerViewTest {

    @Nested
    class LudoConstructor {

        @Test
        void shouldCreatePlayerViewForLudo() {
            List<Integer> positions = List.of(0, 5, 10, 0);
            LocalDate birthday = LocalDate.of(1990, 5, 15);

            PlayerView view = new PlayerView("Alice", "BLUE", positions, birthday, true, 1);

            assertEquals("Alice", view.playerName());
            assertEquals("BLUE", view.playerToken());
            assertEquals(positions, view.piecePositions());
            assertEquals(birthday, view.birthday());
            assertTrue(view.hasTurn());
            assertEquals(1, view.activePieceIndex());
            assertEquals(5, view.tileId()); // First non-zero position
        }

        @Test
        void shouldHandleAllPiecesAtHome() {
            List<Integer> positions = List.of(0, 0, 0, 0);
            LocalDate birthday = LocalDate.of(1985, 12, 25);

            PlayerView view = new PlayerView("Bob", "RED", positions, birthday, false, -1);

            assertEquals("Bob", view.playerName());
            assertEquals("RED", view.playerToken());
            assertEquals(positions, view.piecePositions());
            assertEquals(birthday, view.birthday());
            assertFalse(view.hasTurn());
            assertEquals(-1, view.activePieceIndex());
            assertEquals(0, view.tileId()); // No pieces on board
        }

        @Test
        void shouldSelectFirstNonZeroPosition() {
            List<Integer> positions = List.of(0, 0, 25, 50);
            LocalDate birthday = LocalDate.of(1995, 8, 10);

            PlayerView view = new PlayerView("Charlie", "GREEN", positions, birthday, true, 2);

            assertEquals(25, view.tileId()); // First non-zero is 25, not 50
        }

        @Test
        void shouldHandleEmptyPositionsList() {
            List<Integer> emptyPositions = List.of();
            LocalDate birthday = LocalDate.of(2000, 1, 1);

            PlayerView view = new PlayerView("Dana", "YELLOW", emptyPositions, birthday, false, -1);

            assertEquals("Dana", view.playerName());
            assertEquals("YELLOW", view.playerToken());
            assertTrue(view.piecePositions().isEmpty());
            assertEquals(birthday, view.birthday());
            assertFalse(view.hasTurn());
            assertEquals(-1, view.activePieceIndex());
            assertEquals(0, view.tileId()); // Empty list results in 0
        }

        @Test
        void shouldHandleNullPositionsList() {
            LocalDate birthday = LocalDate.of(1980, 3, 20);

            PlayerView view = new PlayerView("Eve", "PURPLE", null, birthday, true, 0);

            assertEquals("Eve", view.playerName());
            assertEquals("PURPLE", view.playerToken());
            assertNull(view.piecePositions());
            assertEquals(birthday, view.birthday());
            assertTrue(view.hasTurn());
            assertEquals(0, view.activePieceIndex());
            assertEquals(0, view.tileId()); // Null list results in 0
        }
    }

    @Nested
    class SnlConstructor {

        @Test
        void shouldCreatePlayerViewForSnl() {
            LocalDate birthday = LocalDate.of(1992, 7, 4);

            PlayerView view = new PlayerView("Frank", "BLUE", 42, birthday, true);

            assertEquals("Frank", view.playerName());
            assertEquals("BLUE", view.playerToken());
            assertEquals(List.of(42), view.piecePositions());
            assertEquals(birthday, view.birthday());
            assertTrue(view.hasTurn());
            assertEquals(-1, view.activePieceIndex()); // SNL doesn't use piece selection
            assertEquals(42, view.tileId());
        }

        @Test
        void shouldCreatePlayerViewForSnlWithZeroPosition() {
            LocalDate birthday = LocalDate.of(1988, 11, 30);

            PlayerView view = new PlayerView("Grace", "RED", 0, birthday, false);

            assertEquals("Grace", view.playerName());
            assertEquals("RED", view.playerToken());
            assertEquals(List.of(0), view.piecePositions());
            assertEquals(birthday, view.birthday());
            assertFalse(view.hasTurn());
            assertEquals(-1, view.activePieceIndex());
            assertEquals(0, view.tileId());
        }

        @Test
        void shouldCreatePlayerViewForSnlWithLargePosition() {
            LocalDate birthday = LocalDate.of(1975, 2, 14);

            PlayerView view = new PlayerView("Henry", "GREEN", 100, birthday, true);

            assertEquals("Henry", view.playerName());
            assertEquals("GREEN", view.playerToken());
            assertEquals(List.of(100), view.piecePositions());
            assertEquals(birthday, view.birthday());
            assertTrue(view.hasTurn());
            assertEquals(-1, view.activePieceIndex());
            assertEquals(100, view.tileId());
        }
    }

    @Nested
    class FullConstructor {

        @Test
        void shouldCreatePlayerViewWithAllParameters() {
            List<Integer> positions = List.of(15, 20, 25, 30);
            LocalDate birthday = LocalDate.of(1993, 9, 12);

            PlayerView view = new PlayerView("Ivy", "YELLOW", positions, birthday, false, 2, 99);

            assertEquals("Ivy", view.playerName());
            assertEquals("YELLOW", view.playerToken());
            assertEquals(positions, view.piecePositions());
            assertEquals(birthday, view.birthday());
            assertFalse(view.hasTurn());
            assertEquals(2, view.activePieceIndex());
            assertEquals(99, view.tileId()); // Explicitly set, not derived
        }

        @Test
        void shouldAllowTileIdToOverrideCalculatedValue() {
            List<Integer> positions = List.of(0, 0, 50, 0);
            LocalDate birthday = LocalDate.of(1987, 6, 18);

            PlayerView view = new PlayerView("Jack", "PURPLE", positions, birthday, true, 2, 777);

            assertEquals(777, view.tileId()); // Explicit value, not 50 from positions
        }
    }

    @Nested
    class RecordBehavior {

        @Test
        void shouldImplementEqualsCorrectly() {
            List<Integer> positions = List.of(1, 2, 3, 4);
            LocalDate birthday = LocalDate.of(1990, 1, 1);

            PlayerView view1 = new PlayerView("Test", "BLUE", positions, birthday, true, 0);
            PlayerView view2 = new PlayerView("Test", "BLUE", positions, birthday, true, 0);

            assertEquals(view1, view2);
        }

        @Test
        void shouldImplementHashCodeCorrectly() {
            List<Integer> positions = List.of(1, 2, 3, 4);
            LocalDate birthday = LocalDate.of(1990, 1, 1);

            PlayerView view1 = new PlayerView("Test", "BLUE", positions, birthday, true, 0);
            PlayerView view2 = new PlayerView("Test", "BLUE", positions, birthday, true, 0);

            assertEquals(view1.hashCode(), view2.hashCode());
        }

        @Test
        void shouldImplementToStringCorrectly() {
            List<Integer> positions = List.of(5, 10);
            LocalDate birthday = LocalDate.of(1995, 12, 31);

            PlayerView view = new PlayerView("ToString", "RED", positions, birthday, false, 1);

            String toString = view.toString();
            assertNotNull(toString);
            assertTrue(toString.contains("ToString"));
            assertTrue(toString.contains("RED"));
            assertTrue(toString.contains("false"));
        }

        @Test
        void shouldNotBeEqualWithDifferentValues() {
            LocalDate birthday = LocalDate.of(1990, 1, 1);

            PlayerView view1 = new PlayerView("Alice", "BLUE", List.of(1, 2), birthday, true, 0);
            PlayerView view2 = new PlayerView("Bob", "BLUE", List.of(1, 2), birthday, true, 0);

            assertNotEquals(view1, view2);
        }
    }

    @Nested
    class EdgeCasesAndNullHandling {

        @Test
        void shouldHandleNullPlayerName() {
            LocalDate birthday = LocalDate.of(1990, 1, 1);

            PlayerView view = new PlayerView(null, "BLUE", List.of(1), birthday, true, 0);

            assertNull(view.playerName());
            assertEquals("BLUE", view.playerToken());
        }

        @Test
        void shouldHandleNullPlayerToken() {
            LocalDate birthday = LocalDate.of(1990, 1, 1);

            PlayerView view = new PlayerView("Test", null, List.of(1), birthday, true, 0);

            assertEquals("Test", view.playerName());
            assertNull(view.playerToken());
        }

        @Test
        void shouldHandleNullBirthday() {
            PlayerView view = new PlayerView("Test", "BLUE", List.of(1), null, true, 0);

            assertEquals("Test", view.playerName());
            assertNull(view.birthday());
        }

        @Test
        void shouldHandleNegativeTileId() {
            LocalDate birthday = LocalDate.of(1990, 1, 1);

            PlayerView view = new PlayerView("Test", "BLUE", -5, birthday, true);

            assertEquals(-5, view.tileId());
            assertEquals(List.of(-5), view.piecePositions());
        }

        @Test
        void shouldHandleNegativeActivePieceIndex() {
            List<Integer> positions = List.of(1, 2, 3, 4);
            LocalDate birthday = LocalDate.of(1990, 1, 1);

            PlayerView view = new PlayerView("Test", "BLUE", positions, birthday, true, -10);

            assertEquals(-10, view.activePieceIndex());
        }
    }

    @Nested
    class BackwardsCompatibility {

        @Test
        void shouldMaintainCompatibilityBetweenConstructors() {
            LocalDate birthday = LocalDate.of(1990, 1, 1);

            // SNL constructor
            PlayerView snlView = new PlayerView("Test", "BLUE", 42, birthday, true);

            // Should behave the same as full constructor
            PlayerView fullView = new PlayerView("Test", "BLUE", List.of(42), birthday, true, -1, 42);

            assertEquals(snlView.playerName(), fullView.playerName());
            assertEquals(snlView.playerToken(), fullView.playerToken());
            assertEquals(snlView.piecePositions(), fullView.piecePositions());
            assertEquals(snlView.birthday(), fullView.birthday());
            assertEquals(snlView.hasTurn(), fullView.hasTurn());
            assertEquals(snlView.activePieceIndex(), fullView.activePieceIndex());
            assertEquals(snlView.tileId(), fullView.tileId());
        }

        @Test
        void shouldHandleLudoToSnlCompatibility() {
            List<Integer> positions = List.of(0, 15, 0, 30);
            LocalDate birthday = LocalDate.of(1990, 1, 1);

            PlayerView ludoView = new PlayerView("Test", "BLUE", positions, birthday, true, 1);

            // tileId should be first non-zero position for compatibility
            assertEquals(15, ludoView.tileId());
            assertEquals(positions, ludoView.piecePositions());
        }
    }
}