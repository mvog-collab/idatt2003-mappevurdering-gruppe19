import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

public class JavaFXApp extends Application {
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

     // @Override
    // public void start(Stage primaryStage) {
    //     Pane root = new Pane();
    //     root.setPrefSize(600, 400);
    //     root.setStyle("-fx-background-color: #7C4C28;");

    //     HBox mainLayout = new HBox();
    //     mainLayout.setPrefSize(600, 400);

    //     GridPane gridPane = new GridPane();
    //     gridPane.setPrefSize(350, 350);
    //     gridPane.setStyle("-fx-background-color: white;");

    //     for (int i = 0; i < 8; i++) {
    //         ColumnConstraints column = new ColumnConstraints(100);
    //         gridPane.getColumnConstraints().add(column);
    //         RowConstraints row = new RowConstraints(30);
    //         gridPane.getRowConstraints().add(row);
    //     }

    //     VBox sidePanel = new VBox();
    //     sidePanel.setPrefSize(210, 400);

    //     HBox colorBox = new HBox(5);
    //     colorBox.setPrefSize(429, 160);
    //     colorBox.getChildren().addAll(
    //             createEllipse(null),
    //             createEllipse(Color.web("#c31fff")),
    //             createEllipse(Color.DODGERBLUE),
    //             createEllipse(Color.web("#ff1f1f")),
    //             createEllipse(Color.web("#1fff53"))
    //     );

    //     HBox displayBox = new HBox();
    //     displayBox.setPrefSize(210, 192);
    //     displayBox.setStyle("-fx-background-color: brown; -fx-border-color: black; -fx-border-radius: 30; -fx-border-width: 10; -fx-background-radius: 40;");

    //     HBox buttonBox = new HBox();
    //     buttonBox.setPrefSize(200, 100);
    //     Button button = new Button("Button");
    //     buttonBox.getChildren().add(button);

    //     sidePanel.getChildren().addAll(colorBox, displayBox, buttonBox);
    //     mainLayout.getChildren().addAll(gridPane, sidePanel);
    //     root.getChildren().add(mainLayout);

    //     Scene scene = new Scene(root);
    //     primaryStage.setTitle("JavaFX UI");
    //     primaryStage.setScene(scene);
    //     primaryStage.show();
    // }

    // private Ellipse createEllipse(Color fill) {
    //     Ellipse ellipse = new Ellipse(10, 15);
    //     ellipse.setStroke(Color.BLACK);
    //     ellipse.setStrokeType(javafx.scene.shape.StrokeType.INSIDE);
    //     if (fill != null) {
    //         ellipse.setFill(fill);
    //     }
    //     return ellipse;
    // }
}
