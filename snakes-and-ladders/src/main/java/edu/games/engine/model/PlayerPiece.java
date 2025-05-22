package edu.games.engine.model;

import edu.games.engine.board.Tile;

/**
 * Represents an individual game piece belonging to a player.
 * A piece starts at home (not on any tile) and can be moved onto the board.
 */
public class PlayerPiece {
  private final int playerPieceId;
  private Tile currentTile;

  /**
   * Constructs a new player piece with the given ID.
   *
   * @param playerPieceId the ID of the piece (typically 0–3)
   */
  public PlayerPiece(int playerPieceId) {
    this.playerPieceId = playerPieceId;
    this.currentTile = null; // Start at home
  }

  /**
   * Returns the ID of this piece.
   *
   * @return the piece ID
   */
  public int getPlayerPieceId() {
    return playerPieceId;
  }

  /**
   * Returns the tile the piece is currently on, or null if at home.
   *
   * @return the current tile or null
   */
  public Tile getCurrentTile() {
    return currentTile;
  }

  /**
   * Moves the piece to the given tile.
   *
   * @param tile the new tile
   */
  public void moveTo(Tile tile) {
    this.currentTile = tile;
  }

  /**
   * Checks whether the piece is still at home (not on the board).
   *
   * @return true if at home, false if placed
   */
  public boolean isAtHome() {
    return currentTile == null;
  }

  /**
   * Checks whether the piece is on the board (i.e. not at home).
   *
   * @return true if on the board
   */
  public boolean isOnBoard() {
    return currentTile != null;
  }
}
