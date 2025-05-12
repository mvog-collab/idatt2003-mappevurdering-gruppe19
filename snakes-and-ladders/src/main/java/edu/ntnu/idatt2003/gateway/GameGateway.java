package edu.ntnu.idatt2003.gateway;

import edu.games.engine.observer.BoardGameObserver;

public interface GameGateway {
  void addObserver(BoardGameObserver observer);

  void removeObserver(BoardGameObserver observer);
}
