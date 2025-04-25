package edu.games.engine;

public interface Board {
    Tile start();
    Tile move(Tile from, int steps);
}
