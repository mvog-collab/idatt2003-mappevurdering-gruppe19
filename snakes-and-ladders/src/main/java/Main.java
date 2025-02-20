import java.time.LocalDate;

public class Main {

    public static void initialize() {
        BoardGame game = new BoardGame();
        Player Player = new Player("Martha", LocalDate.of(2004, 01, 19));

        game.addPlayer(Player);
        game.createBoard(91);
        game.setCurrentPlayer(Player);

        for (int i = 0; i < 100; i++) {
            game.currentPlayerPlaysTurn();
            if (game.getWinner() != null) {
                return;
            }
        }
    }

    public static void main(String[] args) {
        initialize();
    }
}
