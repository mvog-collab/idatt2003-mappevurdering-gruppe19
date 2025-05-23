package edu.games.engine.model;

import edu.games.engine.board.Tile;
import edu.games.engine.observer.BoardGameEvent;
import edu.games.engine.observer.BoardGameObserver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BoardGameTest {

  private TestableBoardGame boardGame;
  private Player player;
  private Tile fromTile;
  private Tile toTile;

  private static class DummyTile implements Tile {
    private final int id;
    DummyTile(int id) { this.id = id; }
    @Override public int tileId() { return id; }
  }

  private static class TestableBoardGame extends BoardGame {
    public void simulatePlayerMove(Player player, Tile from, Tile to) {
      playerMoved(player, from, to);
    }
  }

  private static class TestObserver implements BoardGameObserver {
    private final List<BoardGameEvent> receivedEvents = new ArrayList<>();
    @Override
    public void update(BoardGameEvent event) {
      receivedEvents.add(event);
    }
    public List<BoardGameEvent> getEvents() {
      return receivedEvents;
    }
  }

  @BeforeEach
  void setUp() {
    boardGame = new TestableBoardGame();
    player = new Player("Alice", Token.RED, LocalDate.of(2000, 1, 1));
    fromTile = new DummyTile(1);
    toTile = new DummyTile(2);
  }

  @Test
  void shouldNotifyObserverWhenPlayerMoves() {
    TestObserver observer = new TestObserver();
    boardGame.addObserver(observer);

    boardGame.simulatePlayerMove(player, fromTile, toTile);

    assertEquals(1, observer.getEvents().size());
    BoardGameEvent event = observer.getEvents().get(0);
    assertEquals(BoardGameEvent.EventType.PLAYER_MOVED, event.getTypeOfEvent());

    BoardGame.PlayerMoveData data = (BoardGame.PlayerMoveData) event.getData();
    assertEquals(player, data.getPlayer());
    assertEquals(fromTile, data.getFromTile());
    assertEquals(toTile, data.getToTile());
  }

  @Test
  void shouldNotifyMultipleObservers() {
    TestObserver observer1 = new TestObserver();
    TestObserver observer2 = new TestObserver();

    boardGame.addObserver(observer1);
    boardGame.addObserver(observer2);

    boardGame.simulatePlayerMove(player, fromTile, toTile);

    assertEquals(1, observer1.getEvents().size());
    assertEquals(1, observer2.getEvents().size());
  }

  @Test
  void shouldNotNotifyRemovedObserver() {
    TestObserver observer = new TestObserver();
    boardGame.addObserver(observer);
    boardGame.removeObserver(observer);

    boardGame.simulatePlayerMove(player, fromTile, toTile);

    assertTrue(observer.getEvents().isEmpty());
  }

  @Test
  void shouldNotAddSameObserverTwice() {
    TestObserver observer = new TestObserver();
    boardGame.addObserver(observer);
    boardGame.addObserver(observer); // Should not be added twice

    boardGame.simulatePlayerMove(player, fromTile, toTile);

    assertEquals(1, observer.getEvents().size());
  }

  // --- Negative test cases ---

  @Test
  void shouldHandleNullObserverGracefully() {
    assertDoesNotThrow(() -> boardGame.addObserver(null)); // Expect it to handle nulls
  }

  @Test
  void shouldNotThrowIfNoObserversRegistered() {
    assertDoesNotThrow(() -> boardGame.simulatePlayerMove(player, fromTile, toTile)); // No observers, but no crash
  }

  @Test
  void playerMoveDataShouldReturnCorrectValues() {
    BoardGame.PlayerMoveData moveData = new BoardGame.PlayerMoveData(player, fromTile, toTile);
    assertEquals(player, moveData.getPlayer());
    assertEquals(fromTile, moveData.getFromTile());
    assertEquals(toTile, moveData.getToTile());
  }
}
