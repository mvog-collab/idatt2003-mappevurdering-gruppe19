package edu.ntnu.idatt2003.ui;
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
    private final int height = 10;

    @Override
    public void start(Stage primaryStage) {
        GridPane board = new GridPane();
        board.getStyleClass().add("grid-pane");

        Label playersLabel = new Label("Players");
        HBox playersBox = new HBox();

        HBox diceBox = new HBox();
        diceBox.getStyleClass().add("dice-box");

        Button rollDice = new Button("Roll Dice");
        rollDice.getStyleClass().add("rollButton");

        HBox buttonBox = new HBox(rollDice);
        buttonBox.getStyleClass().add("button-box");
        VBox gameControl = new VBox(playersLabel, playersBox, diceBox, buttonBox);
        gameControl.getStyleClass().add("game-control");

        HBox mainBox = new HBox(board, gameControl);


        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                StackPane tile = new StackPane();
                tile.setPrefSize(tileSize, tileSize);

                if ((row + col) % 2 == 0) {
                    tile.getStyleClass().add("tile-black");
                } else {
                    tile.getStyleClass().add("tile-white");
                }

                board.add(tile, col, height - row -1);
            }
        }


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


    public static void main(String[] args) {
        launch(args);
    }
}
