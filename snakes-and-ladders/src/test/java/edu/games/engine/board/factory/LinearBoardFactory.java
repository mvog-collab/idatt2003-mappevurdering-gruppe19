package edu.games.engine.board.factory;

import edu.games.engine.board.Board;
import edu.games.engine.board.LinearBoard;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LinearBoardFactoryTest {

  @Nested
  class Create {

    @Test
    void shouldCreateLinearBoardOfGivenSize() {
      LinearBoardFactory factory = new LinearBoardFactory();
      Board board = factory.create(5);
      assertTrue(board instanceof LinearBoard);
      assertNotNull(((LinearBoard) board).tile(0));
    }

    @Test
    void shouldThrowExceptionIfSizeIsTooSmall() {
      LinearBoardFactory factory = new LinearBoardFactory();
      assertThrows(IllegalArgumentException.class, () -> factory.create(1));
    }
  }
}
