package edu.games.engine.model;

import edu.games.engine.board.Tile;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class Player {
  private final String name;
  private final Token token;
  private final LocalDate birthday;
  private final List<PlayerPiece> pieces;

  public Player(String name, Token token, LocalDate birthday) {
    if (name == null || name.isBlank()) {
      throw new IllegalArgumentException("Invalid name: cannot be empty");
    }
    this.name = name;
    this.token = Objects.requireNonNull(token);
    this.birthday = Objects.requireNonNull(birthday);

    // Initialize 4 pieces for the player
    this.pieces = new ArrayList<>(4);
    for (int i = 0; i < 4; i++) {
      this.pieces.add(new PlayerPiece(i));
    }
  }

  public String getName() {
    return name;
  }

  public Token getToken() {
    return token;
  }

  public LocalDate getBirtday() {
    return birthday;
  }

  public List<PlayerPiece> getPieces() {
    return pieces;
  }

  // Get a specific piece by ID (0-3)
  public PlayerPiece getPiece(int pieceId) {
    return pieces.get(pieceId);
  }

  // For backward compatibility with existing code
  public Tile getCurrentTile() {
    // Return the tile of the first piece that's on the board, or null if all are at home
    return pieces.stream()
        .filter(PlayerPiece::isOnBoard)
        .map(PlayerPiece::getCurrentTile)
        .findFirst()
        .orElse(null);
  }

  // For backward compatibility with existing code
  public void moveTo(Tile tile) {
    // Move the first piece that's on the board, or the first piece if all are at home
    PlayerPiece pieceToMove =
        pieces.stream().filter(PlayerPiece::isOnBoard).findFirst().orElse(pieces.get(0));

    pieceToMove.moveTo(tile);
  }

  // New method to move a specific piece
  public void movePiece(int pieceId, Tile tile) {
    if (pieceId < 0 || pieceId >= pieces.size()) {
      throw new IllegalArgumentException("Invalid piece ID: " + pieceId);
    }
    pieces.get(pieceId).moveTo(tile);
  }

  // Check if player has at least one piece at home
  public boolean hasHomepieces() {
    return pieces.stream().anyMatch(PlayerPiece::isAtHome);
  }

  // Check if player has at least one piece on the board
  public boolean hasPiecesOnBoard() {
    return pieces.stream().anyMatch(PlayerPiece::isOnBoard);
  }

  // Get all pieces that are at home
  public List<PlayerPiece> getHomePieces() {
    return pieces.stream().filter(PlayerPiece::isAtHome).toList();
  }

  // Get all pieces that are on the board
  public List<PlayerPiece> getBoardPieces() {
    return pieces.stream().filter(PlayerPiece::isOnBoard).toList();
  }

  @Override
  public boolean equals(Object object) {
    return (object instanceof Player player)
        && player.name.equals(name)
        && player.birthday.equals(birthday);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, birthday);
  }

  @Override
  public String toString() {
    return "%s (%s)".formatted(name, token);
  }
}
