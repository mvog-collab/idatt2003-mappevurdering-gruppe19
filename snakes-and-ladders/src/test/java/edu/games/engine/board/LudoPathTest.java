package edu.games.engine.board;

import edu.games.engine.model.LudoColor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class LudoPathTest {

  private LudoPath ludoPath;

  @BeforeEach
  void setUp() {
    ludoPath = new LudoPath();
  }

  @Nested
  class Construction {

    @Test
    void shouldCreatePathWithCorrectRingSize() {
      List<LudoRingTile> ring = ludoPath.ring();

      assertEquals(52, ring.size());
    }

    @Test
    void shouldCreateRingTilesWithCorrectIds() {
      List<LudoRingTile> ring = ludoPath.ring();

      for (int i = 0; i < 52; i++) {
        assertEquals(i + 1, ring.get(i).tileId());
      }
    }

    @Test
    void shouldLinkRingTilesInCircle() {
      List<LudoRingTile> ring = ludoPath.ring();

      // Check that each tile links to the next
      for (int i = 0; i < 51; i++) {
        assertEquals(ring.get(i + 1), ring.get(i).next(null));
      }

      // Check that last tile links back to first (circular)
      assertEquals(ring.get(0), ring.get(51).next(null));
    }

    @Test
    void shouldCreateGoalLanesForAllColors() {
      Map<LudoColor, List<LudoGoalTile>> goals = ludoPath.goals();

      assertEquals(4, goals.size());
      assertTrue(goals.containsKey(LudoColor.BLUE));
      assertTrue(goals.containsKey(LudoColor.RED));
      assertTrue(goals.containsKey(LudoColor.GREEN));
      assertTrue(goals.containsKey(LudoColor.YELLOW));
    }

    @Test
    void shouldCreateSixGoalTilesPerColor() {
      Map<LudoColor, List<LudoGoalTile>> goals = ludoPath.goals();

      for (LudoColor color : LudoColor.values()) {
        assertEquals(6, goals.get(color).size());
      }
    }

    @Test
    void shouldCreateGoalTilesWithCorrectIds() {
      Map<LudoColor, List<LudoGoalTile>> goals = ludoPath.goals();

      // Blue: 53-58, Red: 59-64, Green: 65-70, Yellow: 71-76
      assertEquals(53, goals.get(LudoColor.BLUE).get(0).tileId());
      assertEquals(58, goals.get(LudoColor.BLUE).get(5).tileId());

      assertEquals(59, goals.get(LudoColor.RED).get(0).tileId());
      assertEquals(64, goals.get(LudoColor.RED).get(5).tileId());

      assertEquals(65, goals.get(LudoColor.GREEN).get(0).tileId());
      assertEquals(70, goals.get(LudoColor.GREEN).get(5).tileId());

      assertEquals(71, goals.get(LudoColor.YELLOW).get(0).tileId());
      assertEquals(76, goals.get(LudoColor.YELLOW).get(5).tileId());
    }

    @Test
    void shouldLinkGoalTilesCorrectly() {
      Map<LudoColor, List<LudoGoalTile>> goals = ludoPath.goals();

      for (LudoColor color : LudoColor.values()) {
        List<LudoGoalTile> lane = goals.get(color);

        // Check that tiles 0-4 link to next tile
        for (int i = 0; i < 5; i++) {
          assertEquals(lane.get(i + 1), lane.get(i).next(null));
        }

        // Check that last tile (5) has no next (end)
        assertNull(lane.get(5).next(null));
      }
    }
  }

  @Nested
  class StartTileRetrieval {

    @Test
    void shouldReturnCorrectStartTileForEachColor() {
      LudoTile blueStart = ludoPath.getStartTile(LudoColor.BLUE);
      LudoTile redStart = ludoPath.getStartTile(LudoColor.RED);
      LudoTile greenStart = ludoPath.getStartTile(LudoColor.GREEN);
      LudoTile yellowStart = ludoPath.getStartTile(LudoColor.YELLOW);

      assertEquals(1, blueStart.tileId()); // Ring index 0 = tile 1
      assertEquals(14, redStart.tileId()); // Ring index 13 = tile 14
      assertEquals(27, greenStart.tileId()); // Ring index 26 = tile 27
      assertEquals(40, yellowStart.tileId()); // Ring index 39 = tile 40
    }

    @Test
    void shouldReturnDifferentStartTilesForDifferentColors() {
      LudoTile blueStart = ludoPath.getStartTile(LudoColor.BLUE);
      LudoTile redStart = ludoPath.getStartTile(LudoColor.RED);
      LudoTile greenStart = ludoPath.getStartTile(LudoColor.GREEN);
      LudoTile yellowStart = ludoPath.getStartTile(LudoColor.YELLOW);

      assertNotEquals(blueStart, redStart);
      assertNotEquals(blueStart, greenStart);
      assertNotEquals(blueStart, yellowStart);
      assertNotEquals(redStart, greenStart);
      assertNotEquals(redStart, yellowStart);
      assertNotEquals(greenStart, yellowStart);
    }
  }

  @Nested
  class StartMethod {

    @Test
    void shouldReturnNullForStart() {
      assertNull(ludoPath.start());
    }
  }

  @Nested
  class MovementFromHome {

    @Test
    void shouldMoveFromHomeWithSix() {
      for (LudoColor color : LudoColor.values()) {
        Tile result = ludoPath.nextTile(null, 6, color);

        assertNotNull(result);
        assertEquals(ludoPath.getStartTile(color), result);
      }
    }

    @Test
    void shouldNotMoveFromHomeWithoutSix() {
      for (LudoColor color : LudoColor.values()) {
        for (int steps = 1; steps <= 5; steps++) {
          Tile result = ludoPath.nextTile(null, steps, color);
          assertNull(result);
        }
      }
    }

    @Test
    void shouldNotMoveFromHomeWithZeroSteps() {
      for (LudoColor color : LudoColor.values()) {
        Tile result = ludoPath.nextTile(null, 0, color);
        assertNull(result);
      }
    }

    @Test
    void shouldNotMoveFromHomeWithNegativeSteps() {
      for (LudoColor color : LudoColor.values()) {
        Tile result = ludoPath.nextTile(null, -1, color);
        assertNull(result);
      }
    }
  }

  @Nested
  class RingMovement {

    @Test
    void shouldMoveOnRingTiles() {
      LudoTile blueStart = ludoPath.getStartTile(LudoColor.BLUE); // Tile 1

      Tile result = ludoPath.nextTile(blueStart, 3, LudoColor.BLUE);

      assertNotNull(result);
      assertEquals(4, result.tileId()); // 1 + 3 = 4
    }

    @Test
    void shouldHandleZeroStepsOnRing() {
      LudoTile blueStart = ludoPath.getStartTile(LudoColor.BLUE);

      Tile result = ludoPath.nextTile(blueStart, 0, LudoColor.BLUE);

      assertEquals(blueStart, result);
    }
  }

  @Nested
  class GoalEntryMovement {

    @Test
    void shouldEnterBlueGoalFromPreEntryTile() {
      List<LudoRingTile> ring = ludoPath.ring();
      LudoTile tile52 = ring.get(51); // Tile 52 (pre-entry for blue)

      Tile result = ludoPath.nextTile(tile52, 1, LudoColor.BLUE);

      assertNotNull(result);
      assertEquals(53, result.tileId()); // First blue goal tile
    }

    @Test
    void shouldEnterRedGoalFromPreEntryTile() {
      List<LudoRingTile> ring = ludoPath.ring();
      LudoTile tile13 = ring.get(12); // Tile 13 (pre-entry for red)

      Tile result = ludoPath.nextTile(tile13, 1, LudoColor.RED);

      assertNotNull(result);
      assertEquals(59, result.tileId()); // First red goal tile
    }

    @Test
    void shouldEnterGreenGoalFromPreEntryTile() {
      List<LudoRingTile> ring = ludoPath.ring();
      LudoTile tile26 = ring.get(25); // Tile 26 (pre-entry for green)

      Tile result = ludoPath.nextTile(tile26, 1, LudoColor.GREEN);

      assertNotNull(result);
      assertEquals(65, result.tileId()); // First green goal tile
    }

    @Test
    void shouldEnterYellowGoalFromPreEntryTile() {
      List<LudoRingTile> ring = ludoPath.ring();
      LudoTile tile39 = ring.get(38); // Tile 39 (pre-entry for yellow)

      Tile result = ludoPath.nextTile(tile39, 1, LudoColor.YELLOW);

      assertNotNull(result);
      assertEquals(71, result.tileId()); // First yellow goal tile
    }

    @Test
    void shouldNotEnterWrongColorGoal() {
      List<LudoRingTile> ring = ludoPath.ring();
      LudoTile tile52 = ring.get(51); // Tile 52 (pre-entry for blue)

      // Red player should not enter blue goal
      Tile result = ludoPath.nextTile(tile52, 1, LudoColor.RED);

      assertNotNull(result);
      assertEquals(1, result.tileId()); // Should wrap to tile 1, not enter blue goal
    }
  }

  @Nested
  class GoalLaneMovement {

    @Test
    void shouldMoveWithinGoalLane() {
      Map<LudoColor, List<LudoGoalTile>> goals = ludoPath.goals();
      LudoGoalTile firstBlueGoal = goals.get(LudoColor.BLUE).get(0); // Tile 53

      Tile result = ludoPath.nextTile(firstBlueGoal, 2, LudoColor.BLUE);

      assertNotNull(result);
      assertEquals(55, result.tileId()); // 53 + 2 = 55
    }

    @Test
    void shouldStopAtEndOfGoalLane() {
      Map<LudoColor, List<LudoGoalTile>> goals = ludoPath.goals();
      LudoGoalTile lastBlueGoal = goals.get(LudoColor.BLUE).get(5); // Tile 58

      Tile result = ludoPath.nextTile(lastBlueGoal, 3, LudoColor.BLUE);

      assertEquals(lastBlueGoal, result); // Should not move past end
    }
  }

  @Nested
  class EndTileDetection {

    @Test
    void shouldDetectEndTilesForAllColors() {
      Map<LudoColor, List<LudoGoalTile>> goals = ludoPath.goals();

      for (LudoColor color : LudoColor.values()) {
        LudoGoalTile endTile = goals.get(color).get(5); // Last tile in goal
        assertTrue(ludoPath.isEnd(endTile));
      }
    }

    @Test
    void shouldNotDetectNonEndTilesAsEnd() {
      // Test ring tiles
      List<LudoRingTile> ring = ludoPath.ring();
      for (LudoRingTile tile : ring) {
        assertFalse(ludoPath.isEnd(tile));
      }

      // Test non-final goal tiles
      Map<LudoColor, List<LudoGoalTile>> goals = ludoPath.goals();
      for (LudoColor color : LudoColor.values()) {
        for (int i = 0; i < 5; i++) { // Tiles 0-4, not the final one
          assertFalse(ludoPath.isEnd(goals.get(color).get(i)));
        }
      }
    }

    @Test
    void shouldNotDetectNullAsEnd() {
      assertFalse(ludoPath.isEnd(null));
    }

    @Test
    void shouldNotDetectNonLudoTileAsEnd() {
      Tile regularTile = new Tile() {
        @Override
        public int tileId() {
          return 999;
        }
      };

      assertFalse(ludoPath.isEnd(regularTile));
    }
  }

  @Nested
  class DataAccess {

    @Test
    void shouldReturnCopyOfRing() {
      List<LudoRingTile> ring = ludoPath.ring();

      assertEquals(52, ring.size());

      // Should be a copy, not the original
      assertThrows(UnsupportedOperationException.class, () -> ring.add(new LudoRingTile(99)));
    }

    @Test
    void shouldReturnUnmodifiableGoalsMap() {
      Map<LudoColor, List<LudoGoalTile>> goals = ludoPath.goals();

      assertEquals(4, goals.size());

      // Should be unmodifiable
      assertThrows(UnsupportedOperationException.class, () -> goals.put(LudoColor.BLUE, null));
    }

    @Test
    void shouldReturnSameRingContentOnMultipleCalls() {
      List<LudoRingTile> ring1 = ludoPath.ring();
      List<LudoRingTile> ring2 = ludoPath.ring();

      assertEquals(ring1.size(), ring2.size());
      for (int i = 0; i < ring1.size(); i++) {
        assertEquals(ring1.get(i).tileId(), ring2.get(i).tileId());
      }
    }

    @Test
    void shouldReturnSameGoalsContentOnMultipleCalls() {
      Map<LudoColor, List<LudoGoalTile>> goals1 = ludoPath.goals();
      Map<LudoColor, List<LudoGoalTile>> goals2 = ludoPath.goals();

      assertEquals(goals1.size(), goals2.size());
      for (LudoColor color : LudoColor.values()) {
        assertEquals(goals1.get(color).size(), goals2.get(color).size());
      }
    }
  }

  @Nested
  class EdgeCasesAndComplexScenarios {

    @Test
    void shouldHandleMovementFromLastRingTileToFirstRingTile() {
      List<LudoRingTile> ring = ludoPath.ring();
      LudoTile tile52 = ring.get(51); // Last ring tile

      // Non-blue player should wrap around normally
      Tile result = ludoPath.nextTile(tile52, 1, LudoColor.RED);

      assertEquals(1, result.tileId()); // Should wrap to first tile
    }

    @Test
    void shouldHandleMovementFromGoalBackToGoal() {
      Map<LudoColor, List<LudoGoalTile>> goals = ludoPath.goals();
      LudoGoalTile secondBlueGoal = goals.get(LudoColor.BLUE).get(1); // Tile 54

      Tile result = ludoPath.nextTile(secondBlueGoal, 1, LudoColor.BLUE);

      assertEquals(55, result.tileId()); // Should move to tile 55
    }

    @Test
    void shouldHandleNegativeStepsOnRingTiles() {
      LudoTile blueStart = ludoPath.getStartTile(LudoColor.BLUE);

      Tile result = ludoPath.nextTile(blueStart, -1, LudoColor.BLUE);

      assertEquals(blueStart, result); // Negative steps should result in no movement
    }

    @Test
    void shouldHandleExtremelyLargeSteps() {
      LudoTile blueStart = ludoPath.getStartTile(LudoColor.BLUE);

      // This should eventually complete but not crash
      Tile result = ludoPath.nextTile(blueStart, 1000, LudoColor.BLUE);

      assertNotNull(result); // Should still return a valid tile
    }
  }

  @Nested
  class AllColorsIntegration {

    @Test
    void shouldHandleCompleteGameScenarioForAllColors() {
      for (LudoColor color : LudoColor.values()) {
        // Start from home with 6
        Tile startTile = ludoPath.nextTile(null, 6, color);
        assertNotNull(startTile);
        assertEquals(ludoPath.getStartTile(color), startTile);

        // Move a few steps on ring
        Tile midTile = ludoPath.nextTile(startTile, 10, color);
        assertNotNull(midTile);

        // Eventually reach end (this is a simplified test)
        Map<LudoColor, List<LudoGoalTile>> goals = ludoPath.goals();
        LudoGoalTile endTile = goals.get(color).get(5);
        assertTrue(ludoPath.isEnd(endTile));
      }
    }
  }
}