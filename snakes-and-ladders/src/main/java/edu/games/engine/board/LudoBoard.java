package edu.games.engine.board;

public final class LudoBoard implements Board {

    private final LudoPath path;

    public LudoBoard(LudoPath path) {
        this.path = path;
    }

    @Override
    public Tile start() {
        return null; //Null when inside spawn/house
    }

    @Override
    public boolean isEnd(Tile tile) {
        return path.isEnd(tile);
    }

    @Override
    public Tile move(Tile from,int steps) {
        return from;
    }

    /* helper so LudoGateway.boardSize() can ask */
    public int size() {
        return 57;
    }
}