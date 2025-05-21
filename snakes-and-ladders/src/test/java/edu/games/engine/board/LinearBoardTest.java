package edu.games.engine.board;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LinearBoardTest {

  @Nested
  class Constructor {

    @Test
    void shouldCreateBoardWithValidSize() {
      LinearBoard board = new LinearBoard(5);
      assertNotNull(board.tile(0));
      assertNotNull(board.tile(4));
    }

    @Test
    void shouldThrowExceptionWhenSizeLessThanTwo() {
      assertThrows(IllegalArgumentException.class, () -> new LinearBoard(1));
    }
  }

  @Nested
  class Movement {

    LinearBoard board = new LinearBoard(5);

    @Test
    void shouldReturnStartTile() {
      Tile start = board.start();
      assertEquals(0, start.id());
    }

    @Test
    void shouldMoveCorrectNumberOfSteps() {
      Tile start = board.start();
      Tile moved = board.move(start, 3);
      assertEquals(3, moved.id());
    }

    @Test
    void shouldStopAtLastTileIfStepsTooFar() {
      Tile start = board.start();
      Tile moved = board.move(start, 10);
      assertEquals(5, moved.id());
    }

    @Test
    void shouldThrowIfStepsNegative() {
      Tile start = board.start();
      assertThrows(IllegalArgumentException.class, () -> board.move(start, -1));
    }

    @Test
    void shouldThrowIfTileIsNotLinearTile() {
      Tile fakeTile = new Tile() {
        @Override
        public int id() {
          return 0;
        }
      };
      assertThrows(IllegalArgumentException.class, () -> board.move(fakeTile, 1));
    }
  }

  @Nested
  class EndCheck {

    @Test
    void shouldReturnTrueIfTileIsLast() {
      LinearBoard board = new LinearBoard(3);
      Tile end = board.tile(3);
      boolean isEnd = board.isEnd(end);
      assertTrue(isEnd);
    }

    @Test
    void shouldReturnFalseIfTileIsNotLast() {
      LinearBoard board = new LinearBoard(3);
      Tile notEnd = board.tile(2);
      boolean isEnd = board.isEnd(notEnd);
      assertFalse(isEnd);
    }

    @Test
    void shouldThrowIfTileNotLinear() {
      LinearBoard board = new LinearBoard(3);
      Tile fakeTile = new Tile() {
        @Override
        public int id() {
          return 1;
        }
      };
      assertThrows(IllegalArgumentException.class, () -> board.isEnd(fakeTile));
    }
  }
}
