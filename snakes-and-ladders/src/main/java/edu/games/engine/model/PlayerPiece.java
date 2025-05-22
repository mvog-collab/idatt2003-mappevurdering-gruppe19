package edu.games.engine.model;

import edu.games.engine.board.Tile;

/** Represents an individual game piece for a player */
public class PlayerPiece {
  private final int playerPieceId;
  private Tile currentTile;

  public PlayerPiece(int playerPieceId) {
    this.playerPieceId = playerPieceId;
    this.currentTile = null; // Start at home
  }

  public int getPlayerPieceId() {
    return playerPieceId;
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
