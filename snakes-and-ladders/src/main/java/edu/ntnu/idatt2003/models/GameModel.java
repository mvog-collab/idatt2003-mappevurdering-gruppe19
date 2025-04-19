package edu.ntnu.idatt2003.models;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

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

  public void addPlayer(String name, PlayerTokens token, LocalDate birthday) {
    Player player = new Player(name, token, birthday);
    playersOfGame.add(player);
    setStartPosition(player);
  }

  public Optional<Tile> moveCurrentPlayer(int diceRoll) {
    if (diceRoll == 12) {
      System.out.println(currentPlayer.getName() + " is skipping their turn!");
      nextPlayersTurn();
      return Optional.empty();
    }
    currentPlayer.move(diceRoll);
    return Optional.ofNullable(currentPlayer.getCurrentTile());
  }

  public Player playerCollision() {
    Tile currentTile = currentPlayer.getCurrentTile();
    Set<Player> playersOnTile = currentTile.getPlayersOnTile();

    if (playersOnTile.size() > 1) {
      for (Player playerOnTile : playersOnTile) {
        if (!playerOnTile.equals(currentPlayer)) {
        sendPlayerBackToStart(playerOnTile);
        return playerOnTile;
        }
      }
    }
    return null;
  }

  private void sendPlayerBackToStart(Player playerToBeRemoved) {
    playerToBeRemoved.getCurrentTile().removePlayerFromTile(playerToBeRemoved);
    putPlayerOnFirstTile(playerToBeRemoved);
    System.out.println(playerToBeRemoved.getName() + " was sent back to start.");
  }

  public Player nextPlayersTurn() {
    if (dice.isPairAndNotTwelve()) {
      return currentPlayer;
    }
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

  public void putPlayerOnFirstTile (Player player) {
    Tile startTile = board.getTile(1);
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
    if (player == null) {
      throw new IllegalArgumentException("New player is invalid");
    }
    if (currentPlayer == player) {
      return;
    }
    this.currentPlayer = player;
  }

  public void setPlayersOfGame(List<Player> playersOfGame) {
    this.playersOfGame = playersOfGame;
  }
}