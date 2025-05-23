package edu.games.engine.observer;

/**
 * Represents an observable entity in the board game.
 * Implementing classes can have observers that are notified of game events.
 */
public interface Observable {

  /**
   * Registers an observer to receive updates.
   *
   * @param observer the observer to add
   */
  void addObserver(BoardGameObserver observer);

  /**
   * Unregisters an observer so it no longer receives updates.
   *
   * @param observer the observer to remove
   */
  void removeObserver(BoardGameObserver observer);

  /**
   * Notifies all registered observers of a game event.
   *
   * @param event the event to broadcast
   */
  void notifyObservers(BoardGameEvent event);
}
