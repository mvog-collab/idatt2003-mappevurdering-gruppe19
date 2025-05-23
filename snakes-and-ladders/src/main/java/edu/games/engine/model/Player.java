package edu.games.engine.model;

import edu.games.engine.exception.ValidationException;
import edu.games.engine.board.Tile;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Represents a player in a board game. A player has a name, a unique token,
 * a birthdate, and a set of game pieces.
 */
public final class Player {
  private final String name;
  private final Token token;
  private final LocalDate birthday;
  private final List<PlayerPiece> pieces;

  /**
   * Constructs a new Player with four pieces.
   *
   * @param name the name of the player
   * @param token the token representing the player
   * @param birthday the player's birthdate
   * @throws ValidationException if any parameter is null or invalid
   */
  public Player(String name, Token token, LocalDate birthday) {
    if (name == null || name.isBlank()) {
      throw new ValidationException("Invalid playerName: cannot be empty");
    }
    if (token == null) {
      throw new ValidationException("Invalid playerToken: cannot be null");
    }
    if (birthday == null) {
      throw new ValidationException("Invalid birthday: cannot be null");
    }
    this.name = name;
    this.token = Objects.requireNonNull(token);
    this.birthday = Objects.requireNonNull(birthday);

    this.pieces = new ArrayList<>(4);
    for (int i = 0; i < 4; i++) {
      this.pieces.add(new PlayerPiece(i));
    }
  }

  /**
   * Returns the player's name.
   *
   * @return the name
   */
  public String getName() {
    return name;
  }

  /**
   * Returns the token associated with the player.
   *
   * @return the token
   */
  public Token getToken() {
    return token;
  }

  /**
   * Returns the player's birthdate.
   *
   * @return the birthdate
   */
  public LocalDate getBirthday() {
    return birthday;
  }

  /**
   * Returns all the pieces belonging to the player.
   *
   * @return a list of player pieces
   */
  public List<PlayerPiece> getPieces() {
    return pieces;
  }

  /**
   * Returns a specific piece by its ID (0-3).
   *
   * @param pieceId the ID of the piece
   * @return the PlayerPiece
   */
  public PlayerPiece getPiece(int pieceId) {
    return pieces.get(pieceId);
  }

  /**
   * Returns the tile of the first piece on the board, or null if all are at home.
   *
   * @return the current tile, or null
   */
  public Tile getCurrentTile() {
    return pieces.stream()
        .filter(PlayerPiece::isOnBoard)
        .map(PlayerPiece::getCurrentTile)
        .findFirst()
        .orElse(null);
  }

  /**
   * Moves the first active piece to the specified tile.
   * If all are at home, moves the first one.
   *
   * @param tile the tile to move to
   */
  public void moveTo(Tile tile) {
    PlayerPiece pieceToMove =
        pieces.stream().filter(PlayerPiece::isOnBoard).findFirst().orElse(pieces.get(0));
    pieceToMove.moveTo(tile);
  }

  /**
   * Moves a specific piece to the given tile.
   *
   * @param pieceId the ID of the piece
   * @param tile the tile to move to
   * @throws ValidationException if the pieceId is invalid
   */
  public void movePiece(int pieceId, Tile tile) {
    if (pieceId < 0 || pieceId >= pieces.size()) {
      throw new ValidationException("pieceId out of range: " + pieceId);
    }
    pieces.get(pieceId).moveTo(tile);
  }

  /**
   * Checks if the player has any pieces at home.
   *
   * @return true if at least one piece is at home
   */
  public boolean hasHomePieces() {
    return pieces.stream().anyMatch(PlayerPiece::isAtHome);
  }

  /**
   * Checks if the player has any pieces on the board.
   *
   * @return true if at least one piece is on the board
   */
  public boolean hasPiecesOnBoard() {
    return pieces.stream().anyMatch(PlayerPiece::isOnBoard);
  }

  /**
   * Returns all pieces that are currently at home.
   *
   * @return a list of home pieces
   */
  public List<PlayerPiece> getHomePieces() {
    return pieces.stream().filter(PlayerPiece::isAtHome).toList();
  }

  /**
   * Returns all pieces that are currently on the board.
   *
   * @return a list of board pieces
   */
  public List<PlayerPiece> getBoardPieces() {
    return pieces.stream().filter(PlayerPiece::isOnBoard).toList();
  }

  /**
   * Checks equality based on name and birthday.
   */
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
