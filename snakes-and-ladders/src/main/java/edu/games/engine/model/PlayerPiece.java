package edu.games.engine.model;

import edu.games.engine.board.Tile;

/**
 * Represents an individual game piece for a player
 */
public class PlayerPiece {
    private final int id; // 0-3 to identify each piece
    private Tile currentTile;
    
    public PlayerPiece(int id) {
        this.id = id;
        this.currentTile = null; // Start at home
    }
    
    public int getId() {
        return id;
    }
    
    public Tile getCurrentTile() {
        return currentTile;
    }
    
    public void moveTo(Tile tile) {
        this.currentTile = tile;
    }
    
    public boolean isAtHome() {
        return currentTile == null;
    }
    
    public boolean isOnBoard() {
        return currentTile != null;
    }
}
