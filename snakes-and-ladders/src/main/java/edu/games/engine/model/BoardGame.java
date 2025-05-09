package edu.games.engine.model;

import edu.games.engine.board.Tile;
import edu.games.engine.observer.BoardGameEvent;
import edu.games.engine.observer.BoardGameObserver;
import edu.games.engine.observer.Observable;

import java.util.ArrayList;
import java.util.List;

public abstract class BoardGame implements Observable {
    private final List<BoardGameObserver> observers = new ArrayList<>();
    
    @Override
    public void addObserver(BoardGameObserver observer) {
        if (!observers.contains(observer)) {
            observers.add(observer);
        }
    }
    
    @Override
    public void removeObserver(BoardGameObserver observer) {
        observers.remove(observer);
    }
    
    @Override
    public void notifyObservers(BoardGameEvent event) {
        for (BoardGameObserver observer : observers) {
            observer.update(event);
        }
    }
    
    // Example of how to notify observers in a game method
    protected void playerMoved(Player player, Tile fromTile, Tile toTile) {
        // Game logic here
        
        // Notify observers about the move
        PlayerMoveData moveData = new PlayerMoveData(player, fromTile, toTile);
        notifyObservers(new BoardGameEvent(BoardGameEvent.EventType.PLAYER_MOVED, moveData));
    }
    
    // Other game methods would follow similar pattern
    
    // Inner class to structure move data
    public static class PlayerMoveData {
        private final Player player;
        private final Tile fromTile;
        private final Tile toTile;
        
        public PlayerMoveData(Player player, Tile fromTile, Tile toTile) {
            this.player = player;
            this.fromTile = fromTile;
            this.toTile = toTile;
        }
        
        // Getters
        public Player getPlayer() { return player; }
        public Tile getFromTile() { return fromTile; }
        public Tile getToTile() { return toTile; }
    }
}