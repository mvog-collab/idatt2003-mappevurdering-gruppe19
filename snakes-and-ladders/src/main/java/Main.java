public class Main {

    public static void initialize() {
        BoardGame game = new BoardGame();
        Player Ingrid = new Player("Ingrid");

        game.addPlayer(Ingrid);
        game.createBoard(91);
        game.setCurrentPlayer(Ingrid);

        for (int i = 0; i < 100; i++) {
            game.play();
            if (game.getWinner() != null) {
                return;
            }
        }
    }

    public static void main(String[] args) {
        initialize();
    }
}
