package edu.ntnu.idatt2003.gateway.view;

import java.time.LocalDate;
import java.util.List;

/**
 * Represents the view model for a player in the UI layer.
 * Supports both Ludo (multiple piece positions) and Snakes & Ladders (single tile ID).
 *
 * @param playerName        the display name of the player
 * @param playerToken       the token identifier (e.g., color) used by the player
 * @param piecePositions    for Ludo: a list of tile IDs for each piece; for SNL this holds a single ID
 * @param birthday          the player's birthday for display purposes
 * @param hasTurn           {@code true} if it is currently this player's turn
 * @param activePieceIndex  for Ludo: index of the selected piece, or -1 if none; ignored for SNL
 * @param tileId            for SNL: the single tile ID; for Ludo: the first non-zero position for backward compatibility
 */
public record PlayerView(
    String playerName,
    String playerToken,
    List<Integer> piecePositions,
    LocalDate birthday,
    boolean hasTurn,
    int activePieceIndex,
    Integer tileId) {

  /**
   * Constructs a PlayerView for Ludo.
   *
   * @param playerName       the display name of the player
   * @param playerToken      the token identifier (e.g., color) used by the player
   * @param piecePositions   a list of tile IDs for each Ludo piece
   * @param birthday         the player's birthday for display purposes
   * @param hasTurn          {@code true} if it is currently this player's turn
   * @param activePieceIndex the index of the selected piece, or -1 if none is selected
   */
  public PlayerView(
      String playerName,
      String playerToken,
      List<Integer> piecePositions,
      LocalDate birthday,
      boolean hasTurn,
      int activePieceIndex) {
    this(
        playerName,
        playerToken,
        piecePositions,
        birthday,
        hasTurn,
        activePieceIndex,
        // Use the first non-zero position for backward compatibility
        piecePositions != null && !piecePositions.isEmpty()
            ? piecePositions.stream().filter(pos -> pos > 0).findFirst().orElse(0)
            : 0);
  }

  /**
   * Constructs a PlayerView for Snakes & Ladders.
   *
   * @param playerName  the display name of the player
   * @param playerToken the token identifier used by the player
   * @param tileId      the current tile ID of the player's piece
   * @param birthday    the player's birthday for display purposes
   * @param hasTurn     {@code true} if it is currently this player's turn
   */
  public PlayerView(
      String playerName,
      String playerToken,
      int tileId,
      LocalDate birthday,
      boolean hasTurn) {
    this(playerName, playerToken, List.of(tileId), birthday, hasTurn, -1, tileId);
  }
}
