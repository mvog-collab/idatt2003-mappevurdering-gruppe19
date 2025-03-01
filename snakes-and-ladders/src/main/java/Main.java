import java.time.LocalDate;

public class Main {

    public static void initialize() {
        BoardGame game = new BoardGame();
        Player Player = new Player("Martha", LocalDate.of(2004, 01, 19));
        Player player = new Player("Edvard", LocalDate.of(2003, 03, 27));

        game.addPlayer(Player);
        game.addPlayer(player);
        game.setStartPosition(Player);
        game.setStartPosition(player);

        for (int i = 0; i < 100; i++) {
            game.playATurn();
            if (game.getWinner() != null) {
                return;
            }
        }
    }

    public static void main(String[] args) {
        initialize();
    }
}
