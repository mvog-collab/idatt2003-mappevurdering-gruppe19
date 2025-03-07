import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

public class BoardPage extends Application {
    private final int tileSize = 50;
    private final int width = 10;
    private final int height = 10;

    @Override
    public void start(Stage primaryStage) {
        GridPane board = new GridPane();

        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                Rectangle tile = new Rectangle(tileSize, tileSize);
                tile.setFill((row + col) % 2 == 0 ? Color.BLACK : Color.WHITE);
                tile.setStroke(Color.BLACK);

                board.add(tile, col, height - row -1);
            }
        }

        Scene scene = new Scene(board, width * tileSize, height * tileSize);
        primaryStage.setTitle("Snakes and ladders");
        primaryStage.setScene(scene);
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
