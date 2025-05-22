package edu.games.engine.board.factory;

import edu.games.engine.board.Board;
import edu.games.engine.board.LinearBoard;
import edu.games.engine.exception.ValidationException;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LinearBoardFactoryTest {

  private final LinearBoardFactory factory = new LinearBoardFactory();

  @Nested
  class Create {

    @Test
    void shouldCreateLinearBoardOfGivenSize() {
      Board board = factory.create(5);
      assertNotNull(board);
      assertTrue(board instanceof LinearBoard);

      LinearBoard linearBoard = (LinearBoard) board;
      assertNotNull(linearBoard.tile(0), "First tile should exist");
      assertEquals(0, linearBoard.tile(0).tileId());
    }

    @Test
    void shouldThrowExceptionIfSizeIsTooSmall() {
      assertThrows(ValidationException.class, () -> factory.create(0), "Size 0 should throw exception");
      assertThrows(ValidationException.class, () -> factory.create(1), "Size 1 should throw exception");
    }

    @Test
    void shouldThrowExceptionForNegativeSize() {
      assertThrows(ValidationException.class, () -> factory.create(-10), "Negative size should throw exception");
    }
  }
}
