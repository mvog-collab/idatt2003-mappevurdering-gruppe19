package edu.games.engine.board;

import edu.games.engine.model.LudoColor;

public final class LudoBoard implements Board {

    private final LudoPath path;

    public LudoBoard(LudoPath path) {
        this.path = path;
    }

    @Override
    public Tile start() {
        return null; // Null when inside spawn/house
    }

    @Override
    public boolean isEnd(Tile tile) {
        return path.isEnd(tile);
    }

    @Override
    public Tile move(Tile from, int steps) {
        // This method shouldn't be used directly anymore
        // Use path.next() with the appropriate color instead
        // We'll return the same tile to prevent unintended movement
        return from;
    }
    
    /**
     * Properly move a piece based on color and dice roll
     * 
     * @param from Starting tile
     * @param steps Number of steps to move
     * @param color Player's color
     * @return The destination tile
     */
    public Tile move(Tile from, int steps, LudoColor color) {
        return path.next(from, steps, color);
    }

    /**
     * Get the starting tile for a particular color
     * 
     * @param color The player's color
     * @return The starting tile for that color
     */
    public Tile getStartTile(LudoColor color) {
        if (path instanceof LudoPath ludoPath) {
            return ludoPath.getStartTile(color);
        }
        return null;
    }

    /* helper so LudoGateway.boardSize() can ask */
    public int size() {
        return 76; // 52 ring tiles + 4 colors × 6 goal tiles
    }
}