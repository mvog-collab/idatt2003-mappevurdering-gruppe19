package edu.games.engine.board;

import edu.games.engine.model.LudoColor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class LudoPathTest {

  private LudoPath path;

  @BeforeEach
  void setUp() {
    path = new LudoPath();
  }

  @Test
  void shouldReturnCorrectStartTileForEachColor() {
    for (LudoColor color : LudoColor.values()) {
      LudoTile startTile = path.getStartTile(color);
      assertNotNull(startTile, "Start tile should not be null for color " + color);
      assertEquals(expectedStartId(color), startTile.tileId());
    }
  }

  @Test
  void shouldReturnNullFromHomeIfNotRollingSix() {
    for (LudoColor color : LudoColor.values()) {
      Tile result = path.nextTile(null, 5, color);
      assertNull(result, "Should remain at home if not rolling 6 for color " + color);
    }
  }

  @Test
  void shouldEnterBoardFromHomeIfRollingSix() {
    for (LudoColor color : LudoColor.values()) {
      Tile result = path.nextTile(null, 6, color);
      assertNotNull(result, "Should enter board if rolling 6 for color " + color);
      assertEquals(expectedStartId(color), result.tileId());
    }
  }

  @Test
  void shouldTraverseRingTilesCorrectly() {
    LudoTile tile = path.getStartTile(LudoColor.BLUE);
    Tile result = path.nextTile(tile, 4, LudoColor.BLUE);
    assertEquals(tile.tileId() + 4, result.tileId());
  }

  @Test
  void shouldEnterGoalPathAtCorrectRingPosition() {
    LudoTile tile = path.ring().get(12);
    Tile result = path.nextTile(tile, 1, LudoColor.RED);
    assertTrue(result instanceof LudoGoalTile);
    assertEquals(59, result.tileId(), "Should enter goal tile at ring position");
  }

  @Test
  void shouldStopAtFinalGoalTile() {
    List<LudoGoalTile> redGoals = path.goals().get(LudoColor.RED);
    LudoGoalTile lastGoal = redGoals.getLast();

    Tile result = path.nextTile(lastGoal, 1, LudoColor.RED);
    assertSame(lastGoal, result, "Should not move past last goal tile");
  }

  @Test
  void isEndShouldReturnTrueForLastGoalTile() {
    List<LudoGoalTile> greenGoals = path.goals().get(LudoColor.GREEN);
    LudoGoalTile last = greenGoals.get(greenGoals.size() - 1);

    assertTrue(path.isEnd(last));
  }

  @Test
  void isEndShouldReturnFalseForNonGoalTile() {
    Tile ringTile = path.ring().get(5);
    assertFalse(path.isEnd(ringTile));
  }

  private int expectedStartId(LudoColor color) {
    return switch (color) {
      case BLUE -> 1;
      case RED -> 14;
      case GREEN -> 27;
      case YELLOW -> 40;
    };
  }
}
