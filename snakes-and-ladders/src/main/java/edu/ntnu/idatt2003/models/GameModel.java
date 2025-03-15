package edu.ntnu.idatt2003.models;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class GameModel {

  private final Board board;
  private Player currentPlayer;
  private final List<Player> players;
  private final Dice dice;

  public GameModel(Board board, Dice dice) {
    this.board = board;
    this.players = new ArrayList<>();
    this.dice = dice;
  }

  public void addPlayer(String name, LocalDate birthday) {
    Player player = new Player(name, birthday);
    players.add(player);
    if (players.size() == 1) {
      setCurrentPlayer(player);
    }
    setStartPosition(player);
  }

  public void moveCurrentPlayer() {
    int roll = dice.rollDice();
    currentPlayer.move(roll);
  }

  public void nextPlayersTurn() {
    int currentIndex = players.indexOf(currentPlayer);
    int nextIndex = (currentIndex + 1) % players.size();
    setCurrentPlayer(players.get(nextIndex));
  }

  public void setStartPosition(Player player) {
    Tile startTile = board.getTile(0);
    if (startTile != null) {
      player.placeOnTile(startTile);
    } else {
      throw new IllegalStateException("Board has no tiles!");
    }
  }

  public boolean hasPlayerWon(Player player) {
    if (player.getCurrentTile().getNextTile() == null ) {
      System.out.println("The winner is " + player.getName());
      return true;
    }
    return false;
  }

  public Board getBoard() {
    return board;
  }

  public List<Player> getPlayers() {
    return players;
  }

  public Player getCurrentPlayer() {
    return currentPlayer;
  }

  public void setCurrentPlayer(Player player) {
    if (player == null || currentPlayer == player) {
      throw new IllegalArgumentException("New player is invalid");
    }
    this.currentPlayer = player;
  }
}