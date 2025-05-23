package edu.ntnu.idatt2003.gateway;

import edu.games.engine.observer.BoardGameObserver;

/**
 * Defines the observer registration API for a game gateway.
 * <p>
 * A {@code GameGateway} allows external components (typically views or controllers)
 * to subscribe to game events emitted by the underlying game engine and to unsubscribe when no longer needed.
 */
public interface GameGateway {

  /**
   * Registers an observer to receive game events.
   *
   * @param observer the {@link BoardGameObserver} to add; must not be {@code null}
   */
  void addObserver(BoardGameObserver observer);

  /**
   * Unregisters a previously registered observer so it no longer receives game events.
   *
   * @param observer the {@link BoardGameObserver} to remove; must not be {@code null}
   */
  void removeObserver(BoardGameObserver observer);
}
