package edu.ntnu.idatt2003.models;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class GameModel {

  private Board board;
  private Player currentPlayer;
  private List<Player> playersOfGame;
  private final Dice dice;

  public GameModel(Board board, Dice dice) {
    this.board = board;
    this.playersOfGame = new ArrayList<>();
    this.dice = dice;
  }

  public void addPlayer(String name, String token, LocalDate birthday) {
    Player player = new Player(name, token, birthday);
    playersOfGame.add(player);
    setStartPosition(player);
  }

  public Optional<Tile> moveCurrentPlayer() {
    int roll = dice.rollDice();
    currentPlayer.move(roll);
    return Optional.ofNullable(currentPlayer.getCurrentTile());
  }

  public Player nextPlayersTurn() {
    int currentIndex = playersOfGame.indexOf(currentPlayer);
    int nextIndex = (currentIndex + 1) % playersOfGame.size();
    setCurrentPlayer(playersOfGame.get(nextIndex));
    return currentPlayer;
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

  public Dice getDice() {
    return dice;
  }

  public List<Player> getPlayers() {
    return playersOfGame;
  }

  public Player getCurrentPlayer() {
    return currentPlayer;
  }

  public void setBoard(Board newBoard) {
    this.board = newBoard;
  }

  public void setCurrentPlayer(Player player) {
    if (player == null || currentPlayer == player) {
      throw new IllegalArgumentException("New player is invalid");
    }
    this.currentPlayer = player;
  }

  public void setPlayersOfGame(List<Player> playersOfGame) {
    this.playersOfGame = playersOfGame;
  }
}