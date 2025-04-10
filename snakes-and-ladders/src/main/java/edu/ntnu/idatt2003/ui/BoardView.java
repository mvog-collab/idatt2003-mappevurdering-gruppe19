package edu.ntnu.idatt2003.ui;
import edu.ntnu.idatt2003.controllers.BoardController;
import edu.ntnu.idatt2003.models.GameModel;
import edu.ntnu.idatt2003.models.Player;

import java.util.HashMap;
import java.util.Map;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;

public class BoardView {
    private final int tileSize = 50;
    private int width;
    private int height;

    private Map<Integer, StackPane> tileUIMap;
    private Map<Player, Node> playerTokens;
    private BoardController boardController;
    private Button rollDiceButton;

    private GameModel gameModel;

    public BoardView(GameModel gameModel) {
        this.gameModel = gameModel;
    }

    public Scene start() {
        tileUIMap = new HashMap<>();
        playerTokens = new HashMap<>();
        boardController = new BoardController(this, gameModel);
        setHeightAndWidth(gameModel.getBoard().getSize());

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

        HBox diceBox = new HBox();
        diceBox.setPrefSize(280, 285);
        diceBox.getStyleClass().add("dice-box");

        HBox diceBoxContainer = new HBox(diceBox);
        diceBoxContainer.getStyleClass().add("dice-box-container");

        /* ImageView diceImages*/
        ImageView diceImageView1 = new ImageView("Images/1.png");
        ImageView diceImageView2 = new ImageView("Images/5.png");
        diceImageView1.setFitWidth(50);
        diceImageView1.setFitHeight(50);
        diceImageView2.setFitWidth(50);
        diceImageView2.setFitHeight(50);
        diceImageView1.getStyleClass().add("dice-image1");
        diceImageView2.getStyleClass().add("dice-image2");

        diceBox.getChildren().addAll(diceImageView1, diceImageView2);

        rollDiceButton = new Button("Roll Dice");
        rollDiceButton.getStyleClass().add("roll-dice-button");

        rollDiceButton.setOnAction(e -> {
            boardController.playATurn();

            int firstDieValue = gameModel.getDice().getDiceList().getFirst().getLastRolledValue();
            int secondDieValue = gameModel.getDice().getDiceList().get(1).getLastRolledValue();

            String diceImageFile1 = "/Images/" + firstDieValue + ".png";
            String diceImageFile2 = "/Images/" + secondDieValue + ".png";

            diceImageView1.setImage(new Image(getClass().getResourceAsStream(diceImageFile1)));
            diceImageView2.setImage(new Image(getClass().getResourceAsStream(diceImageFile2)));


        });

        HBox buttonBox = new HBox(rollDiceButton);
        buttonBox.getStyleClass().add("button-box");
        VBox gameControl = new VBox(playersLabel, playersBox, diceBoxContainer, buttonBox);
        gameControl.getStyleClass().add("game-control");

        HBox mainBox = new HBox(boardContainer, gameControl);
        mainBox.setAlignment(Pos.CENTER);
        mainBox.getStyleClass().add("main-box");

        BoardSetup(board);

        String[] imageFiles = {
            "QueenChessBlack.png",
            "QueenChessGold.png",
            "QueenChessSilver.png",
            "QueenChessWhite.png",
            "QueenChessWood.png"
        };

        for (int i = 0; i < 5; i++) {
            String selectedImageFile = imageFiles[i % imageFiles.length];
            Image playerImage = new Image(
                getClass().getResourceAsStream("/Images/" + selectedImageFile));
            ImageView playerImageView = new ImageView(playerImage);

            playerImageView.setFitWidth(100);
            playerImageView.setFitHeight(100);
            playerImageView.getStyleClass().add("player-figure");

            playersBox.getChildren().add(playerImageView);
        }

        int i = 0;
        for (Player player : gameModel.getPlayers()) {
            String selectedImageFile = imageFiles[i % imageFiles.length];
            Image playerImage = new Image(
                getClass().getResourceAsStream("/Images/" + selectedImageFile));
            ImageView playerImageView = new ImageView(playerImage);

            playerImageView.setFitWidth(40);
            playerImageView.setFitHeight(40);
            playerImageView.getStyleClass().add("player-figure");

            playerTokens.put(player, playerImageView);

            i++;
        }

            /* Background */
            mainBox.getStyleClass().add("page-background");

            Scene scene = new Scene(mainBox, 1000, 700);
            scene.getStylesheets()
                .add(getClass().getResource("/styles/style.css").toExternalForm());

            return scene;
        }

        private void setHeightAndWidth(int boardSize) {
            switch (boardSize) {
                case 64:
                    this.width = 8;
                    this.height = 8;
                    break;
                case 90:
                    this.width = 9;
                    this.height = 10;
                    break;
                case 120:
                    this.width = 10;
                    this.height = 12;
                    break;
                default:
                    break;
            }
        }

        private void BoardSetup (GridPane board){

            boolean leftToRight = true;
            int tileId = 1;

            for (int row = height - 1; row >= 0; row--) {
                if (leftToRight) {
                    for (int col = 0; col < width; col++) {
                        addTile(board, row, col, tileId++);
                    }
                } else {
                    for (int col = width - 1; col >= 0; col--) {
                        addTile(board, row, col, tileId++);
                    }
                }
                leftToRight = !leftToRight;
            }
        }

        private void addTile (GridPane board,int row, int col, int tileId){
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

    public void updatePlayerPosition(int startTileId, int endTileId, Player player) {
        Node playerToken = playerTokens.get(player);

        // Sørg for å kjøre GUI-oppdateringer på JavaFX Application Thread
        new Thread(() -> {
            for (int i = startTileId + 1; i <= endTileId; i++) {
                try {
                    Thread.sleep(300); // pause i 300ms for hvert steg
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                int tileId = i;

                Platform.runLater(() -> {
                    // Fjern token fra forrige rute
                    if (tileId == 1) {
                        System.out.printf("Player is going from tile 0");
                    }
                    else if (tileId > startTileId + 1) {
                        tileUIMap.get(tileId - 1).getChildren().remove(playerToken);
                    } else {
                        tileUIMap.get(startTileId).getChildren().remove(playerToken);
                    }
                    // Legg token i ny rute
                    tileUIMap.get(tileId).getChildren().add(playerToken);
                });
            }
        }).start();
    }


    private void disableRollButton () {
            rollDiceButton.setDisable(true);
        }

        private void enableRollButton () {
            rollDiceButton.setDisable(false);
        }

        public void announceWinner (Player winner){
            disableRollButton();
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("The game is over!");
            alert.setHeaderText("Winner");
            alert.setContentText("Congratulations, " + winner.getName() + "! You've won the game!");
            alert.showAndWait();
        }

        public int getWidth () {
            return width;
        }

        public int getHeight () {
            return height;
        }
    }

