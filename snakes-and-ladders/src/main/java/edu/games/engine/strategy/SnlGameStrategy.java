package edu.games.engine.strategy;

import edu.games.engine.board.Board;
import edu.games.engine.board.LinearBoard;
import edu.games.engine.board.Tile;
import edu.games.engine.impl.DefaultGame;
import edu.games.engine.model.Player;
import edu.games.engine.model.PlayerPiece;
import java.util.List;
import java.util.Map;

public class SnlGameStrategy implements GameStrategy {
  private final Map<Integer, Integer> snakes;
  private final Map<Integer, Integer> ladders;

  public SnlGameStrategy(Map<Integer, Integer> snakes, Map<Integer, Integer> ladders) {
    this.snakes = snakes;
    this.ladders = ladders;
  }

  @Override
  public void initializeGame(DefaultGame game) {
    // Place all players at the start position
    if (game != null && game.board() != null) {
      for (Player player : game.players()) {
        player.moveTo(game.board().start());
      }
    }
  }

  @Override
  public boolean processDiceRoll(Player player, int diceValue, DefaultGame game) {
    // Only get extra turn on doubles (same value on both dice)
    List<Integer> diceValues = game.dice().lastValues();
    if (diceValues.size() < 2) return false;

    // Check if it's a double roll (same value on both dice)
    boolean isDouble = diceValues.get(0).equals(diceValues.get(1));
    System.out.println(
        "It's a double!"
            + isDouble
            + " "
            + diceValues.get(0)
            + " "
            + diceValues.getLast()); // Debug
    // No extra turn for 12 (double 6)
    return isDouble && diceValue != 12;
  }

  @Override
  public Tile movePiece(Player player, int pieceIndex, int diceValue, DefaultGame game) {
    // Snakes and Ladders only has one piece per player, so ignore pieceIndex
    if (player == null || game == null) return null;

    Tile currentTile = player.getCurrentTile();
    if (currentTile == null) return null;

    Board board = game.board();
    if (board == null) return null;

    // Move the piece
    return board.move(currentTile, diceValue);
  }

  @Override
  public boolean checkWinCondition(Player player, DefaultGame game) {
    if (player == null || player.getCurrentTile() == null || game == null) return false;

    Board board = game.board();
    if (board == null) return false;

    // Check if player is at the final tile
    return board.isEnd(player.getCurrentTile());
  }

  @Override
  public void applySpecialRules(
      Player player, PlayerPiece piece, Tile destinationTile, DefaultGame game) {
    if (player == null || destinationTile == null || game == null) return;

    int position = destinationTile.id();

    // Check for snake or ladder
    Integer newPosition = snakes.get(position);
    if (newPosition == null) {
      newPosition = ladders.get(position);
    }

    Tile finalTile = destinationTile;

    if (newPosition != null && game.board() instanceof LinearBoard) {
      LinearBoard board = (LinearBoard) game.board();
      Tile newTile = board.tile(newPosition);

      // Move the player to the new position
      if (piece != null) {
        piece.moveTo(newTile);
      } else {
        player.moveTo(newTile);
      }
    }

    if (finalTile != null) {
      game.players().stream()
          .filter(p -> p != player) // Don't bump the current player
          .filter(
              p -> {
                Tile otherTile = p.getCurrentTile();
                return otherTile != null && otherTile.id() == finalTile.id();
              })
          .forEach(
              p -> {
                // Move the other player back to start
                p.moveTo(game.board().start());
                System.out.println(player.getName() + " bumps " + p.getName() + " back to start");
              });
    }
  }

  public Map<Integer, Integer> getSnakes() {
    return Map.copyOf(snakes); // Return immutable copy
  }

  public Map<Integer, Integer> getLadders() {
    return Map.copyOf(ladders); // Return immutable copy
  }
}
