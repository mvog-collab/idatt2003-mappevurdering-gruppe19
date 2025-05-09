package edu.games.engine.observer;

public interface BoardGameObserver {
    /**
     * Called when a board game state changes
     * @param event The event that happened
     */
    void update(BoardGameEvent event);
}