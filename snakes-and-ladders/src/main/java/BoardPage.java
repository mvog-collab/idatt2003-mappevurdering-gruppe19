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
        Label playersLabel = new Label("Players");
        HBox playersBox = new HBox();
        HBox diceBox = new HBox();
        Button rollDice = new Button("Roll Dice");
        HBox buttonBox = new HBox(rollDice);
        VBox gameControl = new VBox(playersLabel, playersBox, diceBox, buttonBox);
        HBox mainBox = new HBox(board, gameControl);


        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                StackPane tile = new StackPane();
                tile.setPrefSize(tileSize, tileSize);
                tile.setStyle(
                    ((row + col) % 2 == 0 ? "-fx-background-color: black; " : "-fx-background-color: white; ") +
                    "-fx-border-color: black; -fx-border-width: 1px;");

                board.add(tile, col, height - row -1);
            }
        }


        for (int i = 0; i < 5; i++){
            Rectangle playersRectangle = new Rectangle(50, 50, Color.BLUEVIOLET);
            playersBox.getChildren().add(playersRectangle);
        }


        playersBox.setSpacing(10);
        mainBox.setStyle("-fx-border-color: black; -fx-border-width: 3px;");
        gameControl.setStyle("-fx-border-color: black; -fx-border-width: 3px;");
        gameControl.setPrefSize(400, 700);
        playersBox.setStyle("-fx-border-color: black; -fx-border-width: 3px;");
        playersLabel.setStyle("-fx-border-color: black; -fx-border-width: 3px;");
        buttonBox.setStyle("-fx-border-color: black; -fx-border-width: 3px;");
        diceBox.setStyle("-fx-border-color: black; -fx-border-width: 3px;");
        gameControl.setAlignment(Pos.TOP_CENTER);
        playersBox.setAlignment(Pos.CENTER);
        diceBox.setAlignment(Pos.CENTER);
        buttonBox.setAlignment(Pos.CENTER);

        gameControl.setSpacing(150);


        Scene scene = new Scene(mainBox, 1000, 700);
        primaryStage.setTitle("Snakes and ladders");
        primaryStage.setScene(scene);
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
