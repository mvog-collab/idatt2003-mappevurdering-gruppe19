package edu.games.engine.board;

public class LinearTile implements Tile {
    private final int id;
    LinearTile next;

    public LinearTile (int id) {
        this.id = id;
    }

    @Override
    public int id() {
        return id;
    }

    public LinearTile next() {
        return next;
    }

}
