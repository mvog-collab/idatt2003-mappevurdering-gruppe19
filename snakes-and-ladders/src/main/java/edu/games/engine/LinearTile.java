package edu.games.engine;

public class LinearTile implements Tile {
    private final int id;
    LinearTile next;

    LinearTile (int id) {
        this.id = id;
    }

    @Override
    public int id() {
        return id;
    }
}
