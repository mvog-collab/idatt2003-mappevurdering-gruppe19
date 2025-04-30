package edu.games.engine.board;

public interface Board {
    Tile start();
    Tile move(Tile from, int steps);
    boolean isEnd(Tile tile);
}
