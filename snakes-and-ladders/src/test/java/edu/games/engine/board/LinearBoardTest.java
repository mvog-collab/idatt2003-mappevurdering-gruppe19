package edu.games.engine.board;

import edu.games.engine.exception.ValidationException;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class LinearBoardTest {

  @Nested
  class Constructor {

    @Test
    void shouldCreateBoardWithValidSize() {
      LinearBoard board = new LinearBoard(5);
      assertNotNull(board.tile(0));
      assertNotNull(board.tile(5));
    }

    @Test
    void shouldThrowExceptionWhenSizeLessThanTwo() {
      assertThrows(ValidationException.class, () -> new LinearBoard(1));
    }

    @Test
    void shouldCreateBoardFromValidMap() {
      Map<Integer, LinearTile> map = new HashMap<>();
      map.put(0, new LinearTile(0));
      map.put(1, new LinearTile(1));
      LinearBoard board = new LinearBoard(map);
      assertNotNull(board.tile(0));
      assertNotNull(board.tile(1));
    }

    @Test
    void shouldThrowIfMapIsNull() {
      assertThrows(ValidationException.class, () -> new LinearBoard(null));
    }

    @Test
    void shouldThrowIfMapIsEmpty() {
      assertThrows(ValidationException.class, () -> new LinearBoard(new HashMap<>()));
    }
  }

  @Nested
  class Movement {

    LinearBoard board = new LinearBoard(5);

    @Test
    void shouldReturnStartTile() {
      Tile start = board.start();
      assertEquals(0, start.tileId());
    }

    @Test
    void shouldMoveCorrectNumberOfSteps() {
      Tile start = board.start();
      Tile moved = board.move(start, 3);
      assertEquals(3, moved.tileId());
    }

    @Test
    void shouldStopAtLastTileIfStepsTooFar() {
      Tile start = board.start();
      Tile moved = board.move(start, 10);
      assertEquals(5, moved.tileId());
    }

    @Test
    void shouldThrowIfStepsNegative() {
      Tile start = board.start();
      assertThrows(ValidationException.class, () -> board.move(start, -1));
    }

    @Test
    void shouldThrowIfTileIsNotLinearTile() {
      Tile fakeTile = () -> 0;
      assertThrows(ValidationException.class, () -> board.move(fakeTile, 1));
    }

    @Test
    void shouldReturnNullIfTileIdDoesNotExist() {
      assertNull(board.tile(999));
    }
  }

  @Nested
  class EndCheck {

    LinearBoard board = new LinearBoard(3);

    @Test
    void shouldReturnTrueIfTileIsLast() {
      Tile end = board.tile(3);
      assertTrue(board.isEnd(end));
    }

    @Test
    void shouldReturnFalseIfTileIsNotLast() {
      Tile notEnd = board.tile(2);
      assertFalse(board.isEnd(notEnd));
    }

    @Test
    void shouldThrowIfTileNotLinear() {
      Tile fakeTile = () -> 1;
      assertThrows(ValidationException.class, () -> board.isEnd(fakeTile));
    }
  }
}
