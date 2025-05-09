package edu.games.engine.observer;

public interface Observable {
    void addObserver(BoardGameObserver observer);
    void removeObserver(BoardGameObserver observer);
    void notifyObservers(BoardGameEvent event);
}