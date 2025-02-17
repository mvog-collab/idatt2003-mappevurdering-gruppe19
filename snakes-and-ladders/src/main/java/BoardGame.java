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
    for (int i = 0; i < size; i++) {
      board.addTile(new Tile(i));
    }
  }

  public void play() {
    int roll = dice.rollDice();

    currentPlayer.move(roll);


  }

  public Player getWinner() {
    if (currentPlayer.getCurrentTile().getNextTile() == null ) {
      return currentPlayer;
    }
    return null;
  }
}