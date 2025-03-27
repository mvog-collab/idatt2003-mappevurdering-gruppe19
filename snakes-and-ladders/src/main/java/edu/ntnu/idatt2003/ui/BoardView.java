package edu.ntnu.idatt2003.ui;
import edu.ntnu.idatt2003.controllers.BoardController;
import edu.ntnu.idatt2003.game_logic.BoardMaker;
import edu.ntnu.idatt2003.models.Board;
import edu.ntnu.idatt2003.models.Dice;
import edu.ntnu.idatt2003.models.GameModel;
import edu.ntnu.idatt2003.models.Player;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.*;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

public class BoardView extends Application {
    private final int tileSize = 50;
    private final int width = 9;
    private final int height = 10;

    private Map<Integer, StackPane> tileUIMap;
    private Map<Player, Node> playerTokens;
    private Board gameBoard;
    private BoardController boardController;
    private Button rollDiceButton;

    @Override
    public void start(Stage primaryStage) {
        gameBoard = BoardMaker.createBoard(width * height + 1);
        tileUIMap = new HashMap<>();
        playerTokens = new HashMap<>();
        GameModel gameModel = new GameModel(gameBoard, new Dice());
        boardController = new BoardController(this, gameModel);

        GridPane board = new GridPane();
        board.setPrefSize(tileSize * width, tileSize * height);
        board.setMaxSize(tileSize * width, tileSize * height);


        StackPane boardContainer = new StackPane(board);
        boardContainer.setMaxSize(tileSize * width, tileSize * height);
        boardContainer.getStyleClass().add("board-container");
        boardContainer.setAlignment(Pos.CENTER);


        Label playersLabel = new Label("Players");
        HBox playersBox = new HBox();
        playersBox.getStyleClass().add("players-box");

        StackPane diceBox = new StackPane();
        diceBox.setPrefSize(280,285);
        diceBox.getStyleClass().add("dice-box");

        HBox diceBoxContainer = new HBox(diceBox);
        diceBoxContainer.getStyleClass().add("dice-box-container");




        rollDiceButton = new Button("Roll Dice");
        rollDiceButton.getStyleClass().add("roll-dice-button");

        rollDiceButton.setOnAction(e -> boardController.playATurn());

        HBox buttonBox = new HBox(rollDiceButton);
        buttonBox.getStyleClass().add("button-box");
        VBox gameControl = new VBox(playersLabel, playersBox, diceBoxContainer, buttonBox);
        gameControl.getStyleClass().add("game-control");


        HBox mainBox = new HBox(boardContainer, gameControl);
        mainBox.setAlignment(Pos.CENTER);
        mainBox.getStyleClass().add("main-box");

        BoardSetup(board);

        boardController.addPlayer("Edvard", LocalDate.of(2003, 03, 27));
        boardController.addPlayer("Martha", LocalDate.of(2004, 01, 19));

        for (int i = 0; i < 5; i++){
            Rectangle playersRectangle = new Rectangle(50, 50);
            playersRectangle.getStyleClass().add("player-figure");
            playersBox.getChildren().add(playersRectangle);
        }

        for (Player player : gameModel.getPlayers()){
            Rectangle playersRectangle = new Rectangle(15, 15);
            playersRectangle.getStyleClass().add("player-figure");
            playerTokens.put(player, playersRectangle);
        }

        /* Background */
        mainBox.getStyleClass().add("page-background");


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

    public void updatePlayerPosition(int tileId, Player player) {
        Node playerToken = playerTokens.get(player);
        StackPane tile = tileUIMap.get(tileId);
        tile.getChildren().add(playerToken);
    }

    private void disableRollButton() {
        rollDiceButton.setDisable(true);
    }

    private void enableRollButton() {
        rollDiceButton.setDisable(false);
    }

    public void announceWinner(Player winner) {
        disableRollButton();
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("The game is over!");
        alert.setHeaderText("Winner");
        alert.setContentText("Congratulations, " + winner.getName() + "! You've won the game!");
        alert.showAndWait();
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
