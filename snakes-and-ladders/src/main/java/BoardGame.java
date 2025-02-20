import java.util.ArrayList;
import java.util.List;

public class BoardGame {

  Board board;
  Player currentPlayer;
  List<Player> players;
  Dice dice;

  public BoardGame() {
    this.board = new Board();
    this.players = new ArrayList<Player>();
    this.dice = new Dice();
  }

  public void addPlayer(Player player) {
    players.add(player);
  }

  public void createBoard(int size) {
    if (100 < size || size < 0) {
      throw new IllegalArgumentException("Invalid board size.");
    }
    for (int i = 0; i < size; i++) {
      board.addTile(new Tile(i));
    }

    for (int i = 0; i < size - 1; i++) {
      Tile currentTile = board.getTile(i);
      Tile nextTile = board.getTile(i + 1);
      currentTile.setNextTile(nextTile);
    }
  }

  public void play() {
    int roll = dice.rollDice();

    currentPlayer.move(roll);
  }

  public void setCurrentPlayer(Player player) {
    this.currentPlayer = player;
    Tile startTile = board.getTile(0);
    if (startTile != null) {
      player.placeOnTile(startTile);
    } else {
      throw new IllegalStateException("Board has no tiles!");
    }
  }

  public Player getWinner() {
    if (currentPlayer.getCurrentTile().getNextTile() == null ) {
      System.out.println("The winner is " + currentPlayer.getName());
      return currentPlayer;
    }
    return null;
  }

  public Board getBoard() {
    return board;
  }
}