import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import java.time.LocalDate;

public class Main extends Application {

    private BoardGame game;

    @Override
    public void start(Stage primaryStage) {
        game = new BoardGame();
        Player player1 = new Player("Martha", LocalDate.of(2004, 1, 19));
        Player player2 = new Player("Edvard", LocalDate.of(2003, 3, 27));

        game.addPlayer(player1);
        game.addPlayer(player2);
        game.setStartPosition(player1);
        game.setStartPosition(player2);

        Button startButton = new Button("Start Game");
        startButton.setOnAction(event -> playGame());

        StackPane root = new StackPane(startButton);
        Scene scene = new Scene(root, 400, 300);

        primaryStage.setTitle("Snakes and Ladders");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void playGame() {
        for (int i = 0; i < 100; i++) {
            game.playATurn();
            if (game.getWinner() != null) {
                System.out.println("Winner: " + game.getWinner().getName());
                return;
            }
        }
    }

    public static void main(String[] args) {
        launch(args); // Starter JavaFX-applikasjonen
    }
}