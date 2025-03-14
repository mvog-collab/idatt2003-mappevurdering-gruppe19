package edu.ntnu.idatt2003.application;
import edu.ntnu.idatt2003.ui.BoardView;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import edu.ntnu.idatt2003.game_logic.BoardMaker;
import edu.ntnu.idatt2003.models.Board;
import edu.ntnu.idatt2003.models.Dice;
import edu.ntnu.idatt2003.models.Player;
import edu.ntnu.idatt2003.models.Tile;

public class BoardGame {

  private final Board board;
  private Player currentPlayer;
  private final List<Player> players;
  private final Dice dice;
  private BoardView boardView;
  Scanner sc = new Scanner(System.in);

  public BoardGame(BoardView boardView) {
    this.board = BoardMaker.createBoard(100);
    this.players = new ArrayList<Player>();
    this.dice = new Dice();
    this.boardView = boardView;
  }

  public void addPlayer(String name, LocalDate birthday) {
    Player player = new Player(name, birthday);
    players.add(player);
    setStartPosition(player);
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