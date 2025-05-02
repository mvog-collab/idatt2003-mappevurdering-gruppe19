package edu.games.engine.board;

public interface MovementPath {
    Tile start();
    Tile next(Tile current, int steps);
    boolean isEnd(Tile tile);
}