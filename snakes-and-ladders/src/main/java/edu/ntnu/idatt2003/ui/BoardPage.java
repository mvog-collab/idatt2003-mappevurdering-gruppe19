package edu.ntnu.idatt2003.ui;
import edu.ntnu.idatt2003.game_logic.BoardMaker;
import edu.ntnu.idatt2003.models.Board;
import java.util.HashMap;
import java.util.Map;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

public class BoardPage extends Application {
    private final int tileSize = 50;
    private final int width = 10;
    private final int height = 9;

    private Map<Integer, StackPane> tileUIMap;
    private Board gameBoard;

    @Override
    public void start(Stage primaryStage) {
        gameBoard = new Board(width * height);
        tileUIMap = new HashMap<>();

        GridPane board = new GridPane();
        board.getStyleClass().add("grid-pane");

        Label playersLabel = new Label("Players");
        HBox playersBox = new HBox();
        playersBox.getStyleClass().add("players-box");

        HBox diceBox = new HBox();
        diceBox.getStyleClass().add("dice-box");

        Button rollDice = new Button("Roll Dice");
        rollDice.getStyleClass().add("rollButton");

        HBox buttonBox = new HBox(rollDice);
        buttonBox.getStyleClass().add("button-box");
        VBox gameControl = new VBox(playersLabel, playersBox, diceBox, buttonBox);
        gameControl.getStyleClass().add("game-control");

        HBox mainBox = new HBox(board, gameControl);

        BoardSetup(board);


        for (int i = 0; i < 5; i++){
            Rectangle playersRectangle = new Rectangle(50, 50);
            playersRectangle.getStyleClass().add("player-figure");
            playersBox.getChildren().add(playersRectangle);
        }


        Scene scene = new Scene(mainBox, 1000, 700);
        scene.getStylesheets().add(getClass().getResource("/styles/style.css").toExternalForm());

        primaryStage.setTitle("Snakes and ladders");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void BoardSetup(GridPane board) {

        boolean leftToRight = true;
        int tileId = 1;

        for (int row = height - 1; row >= 0; row--) {
            if (leftToRight) {
                for (int col = 0; col < width; col++) {
                    addTile(board, row, col, tileId++);
                }
            } else {
                for (int col = width -1; col >= 0; col--) {
                    addTile(board, row, col, tileId++);
                }
            }
            leftToRight = !leftToRight;
        }
    }

    private void addTile(GridPane board, int row, int col, int tileId) {
        StackPane tile = new StackPane();
        tile.setPrefSize(tileSize, tileSize);

        Label tileLabel = new Label(String.valueOf(tileId));

        if ((row + col) % 2 == 0) {
            tile.getStyleClass().add("tile-white");
            tileLabel.getStyleClass().add("tile-label-black");
        } else {
            tile.getStyleClass().add("tile-black");
            tileLabel.getStyleClass().add("tile-label-white");
        }

        tile.getChildren().add(tileLabel);
        board.add(tile, col, row);

        tileUIMap.put(tileId, tile);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
