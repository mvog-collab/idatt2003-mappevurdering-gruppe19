package application;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import game_logic.BoardMaker;
import models.Board;
import models.Dice;
import models.Player;
import models.Tile;

public class BoardGame {

  private final Board board;
  private Player currentPlayer;
  private final List<Player> players;
  private final Dice dice;
  Scanner sc = new Scanner(System.in);

  public BoardGame() {
    this.board = BoardMaker.createBoard(100);
    this.players = new ArrayList<Player>();
    this.dice = new Dice();
  }

  public void addPlayer(Player player) {
    players.add(player);
  }

  public void currentPlayerPlaysTurn() {
    int roll = dice.rollDice();
    currentPlayer.move(roll);
  }

  public void setStartPosition(Player player) {
    Tile startTile = board.getTile(0);
    if (startTile != null) {
      player.placeOnTile(startTile);
    } else {
      throw new IllegalStateException("Board has no tiles!");
    }
  }

  public void setCurrentPlayer(Player player) {
    if (player == null || currentPlayer == player) {
      throw new IllegalArgumentException("New player cannot be null.");
    }
    this.currentPlayer = player;
  }

  public Player getWinner() {
    if (currentPlayer.getCurrentTile().getNextTile() == null ) {
      System.out.println("The winner is " + currentPlayer.getName());
      return currentPlayer;
    }
    return null;
  }

  public void playATurn() {
      do {
          for (Player player : players) {
              String wantToThrow = sc.nextLine();
              if (!wantToThrow.isBlank()) {
                  setCurrentPlayer(player);
                  currentPlayerPlaysTurn();
              }
          }
      } while (getWinner() == null);
  }

  public Board getBoard() {
    return board;
  }
}