package edu.games.engine.model;

import edu.games.engine.board.Tile;
import edu.games.engine.observer.BoardGameEvent;
import edu.games.engine.observer.BoardGameObserver;
import edu.games.engine.observer.Observable;
import java.util.ArrayList;
import java.util.List;

/**
 * Abstract base class for board games that supports observer functionality.
 * Implements the Observable interface and provides helper methods to notify
 * observers of in-game events.
 */
public abstract class BoardGame implements Observable {
  private final List<BoardGameObserver> observers = new ArrayList<>();

  /**
   * Adds an observer if it's not already registered.
   *
   * @param observer the observer to add
   */
  @Override
  public void addObserver(BoardGameObserver observer) {
    if (!observers.contains(observer)) {
      observers.add(observer);
    }
  }

  /**
   * Removes a registered observer.
   *
   * @param observer the observer to remove
   */
  @Override
  public void removeObserver(BoardGameObserver observer) {
    observers.remove(observer);
  }

  /**
   * Notifies all registered observers of a game event.
   *
   * @param event the event to broadcast
   */
  @Override
  public void notifyObservers(BoardGameEvent event) {
    for (BoardGameObserver observer : observers) {
      observer.update(event);
    }
  }

  /**
   * Notifies observers about a player move.
   * Can be called from game logic to report movement.
   *
   * @param player the player who moved
   * @param fromTile the tile the player moved from
   * @param toTile the tile the player moved to
   */
  protected void playerMoved(Player player, Tile fromTile, Tile toTile) {
    PlayerMoveData moveData = new PlayerMoveData(player, fromTile, toTile);
    notifyObservers(new BoardGameEvent(BoardGameEvent.EventType.PLAYER_MOVED, moveData));
  }

  /**
   * Encapsulates data related to a player's movement on the board.
   */
  public static class PlayerMoveData {
    private final Player player;
    private final Tile fromTile;
    private final Tile toTile;

    /**
     * Constructs a new PlayerMoveData instance.
     *
     * @param player the player who moved
     * @param fromTile the tile they moved from
     * @param toTile the tile they moved to
     */
    public PlayerMoveData(Player player, Tile fromTile, Tile toTile) {
      this.player = player;
      this.fromTile = fromTile;
      this.toTile = toTile;
    }

    public Player getPlayer() {
      return player;
    }

    public Tile getFromTile() {
      return fromTile;
    }

    public Tile getToTile() {
      return toTile;
    }
  }
}
