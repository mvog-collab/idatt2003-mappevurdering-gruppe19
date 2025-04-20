package edu.ntnu.idatt2003.ui;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.ntnu.idatt2003.controllers.BoardController;
import edu.ntnu.idatt2003.models.GameModel;
import edu.ntnu.idatt2003.models.Player;
import edu.ntnu.idatt2003.models.PlayerTokens;
import edu.ntnu.idatt2003.utils.ResourcePaths;
import javafx.application.Platform;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BoardView {
    private final int tileSize = 50;
    private int width;
    private int height;

    private Map<Integer, StackPane> tileUIMap;
    private Map<Player, ImageView> playerTokens;
    private Map<Player, VBox> playerDisplayBoxes = new HashMap<>();
    private VBox playerDisplayBox;
    private BoardController boardController;
    private Button rollDiceButton;

    private GameModel gameModel;

    private Pane overlayPane;
    private Pane tokenPane;

    public BoardView(GameModel gameModel) {
        this.gameModel = gameModel;
    }

    public Scene start() {
        tileUIMap = new HashMap<>();
        playerTokens = new HashMap<>();
        boardController = new BoardController(this, gameModel);
        setHeightAndWidth(gameModel.getBoard().getSize());

        GridPane boardGrid = new GridPane();
        boardGrid.setPrefSize(tileSize * width, tileSize * height);
        boardGrid.setMaxSize(tileSize * width, tileSize * height);

        overlayPane = new Pane();
        overlayPane.setPickOnBounds(false);
        overlayPane.setPrefSize(tileSize * width, tileSize * height);

        tokenPane = new Pane();
        tokenPane.setPickOnBounds(false);
        tokenPane.setPrefSize(tileSize * width, tileSize * height);

        StackPane boardContainer = new StackPane();
        boardContainer.getChildren().addAll(boardGrid, overlayPane, tokenPane);
        boardContainer.setMaxSize(tileSize * width, tileSize * height);
        boardContainer.getStyleClass().add("board-container");
        boardContainer.setAlignment(Pos.CENTER);
        HBox.setMargin(boardContainer, new Insets(0, 0, 0, 30));

        Label playersLabel = new Label("Players");
        playersLabel.getStyleClass().add("players-label");
        HBox playersBox = new HBox();
        playersBox.getStyleClass().add("players-box");

        HBox diceBox = new HBox();
        diceBox.setPrefSize(280, 285);
        diceBox.getStyleClass().add("dice-box");

        HBox diceBoxContainer = new HBox(diceBox);
        diceBoxContainer.getStyleClass().add("dice-box-container");

        String image_dir = ResourcePaths.IMAGE_DIR;
        ImageView diceImageView1 = new ImageView(image_dir + "1.png");
        ImageView diceImageView2 = new ImageView(image_dir + "5.png");
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

            String diceImageFile1 = image_dir + firstDieValue + ".png";
            String diceImageFile2 = image_dir + secondDieValue + ".png";

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

        BoardSetup(boardGrid);

        // Sidepane: spiller-bilder
        for (int i = 0; i < gameModel.getPlayers().size(); i++) {
            playerDisplayBox = new VBox();
            playerDisplayBox.setAlignment(Pos.CENTER);
            playerDisplayBox.setSpacing(5);
            playerDisplayBox.getStyleClass().add("display-player-box");
            playersBox.getChildren().add(playerDisplayBox);
        
            Player player = gameModel.getPlayers().get(i);
            playerDisplayBoxes.put(player, playerDisplayBox);
        
            ImageView playerImageView = new ImageView(
                new Image(getClass().getResourceAsStream(player.getToken().getImagePath()))
            );
            playerImageView.setFitWidth(100);
            playerImageView.setFitHeight(100);
            playerImageView.getStyleClass().add("player-figure");
        
            Label turnLabel = new Label("🎲 Your Turn!");
            turnLabel.getStyleClass().add("turn-indicator");
            turnLabel.setVisible(false);
        
            playerDisplayBox.getChildren().addAll(turnLabel, playerImageView);
        }

        // Initialisere tokens og plassere på start
        for (Player player : gameModel.getPlayers()) {
            ImageView token = new ImageView(
                new Image(getClass().getResourceAsStream(player.getToken().getImagePath()))
            );
            token.setFitWidth(40);
            token.setFitHeight(40);
            token.getStyleClass().add("player-figure");
            playerTokens.put(player, token);
            placeTokenOnTile(1, token);
        }

        mainBox.getStyleClass().add("page-background");

        Platform.runLater(this::addOverlaysFromJson);

        updateCurrentPlayerView(gameModel.getCurrentPlayer());

        Scene scene = new Scene(mainBox, 1000, 700);
        scene.getStylesheets().add(getClass().getResource("/styles/style.css").toExternalForm());

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

    private void BoardSetup(GridPane board) {
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

    private Point2D getTileCenter(StackPane tile) {
        Bounds bounds = tile.getBoundsInParent();
        double centerX = bounds.getMinX() + bounds.getWidth() / 2;
        double centerY = bounds.getMinY() + bounds.getHeight() / 2;
        return new Point2D(centerX, centerY);
    }

    private void addOverlaysFromJson() {
        List<OverlayParams> overlayList = loadOverlaysFromJson();
        for (OverlayParams params : overlayList) {
            StackPane startTile = tileUIMap.get(params.getStartTileId() + 1);
            if (startTile == null) {
                System.err.println("Fant ikke tile med id: " + params.getStartTileId());
                continue;
            }
            Point2D center = getTileCenter(startTile);
            InputStream is = getClass().getResourceAsStream(params.getImagePath());
            if (is == null) {
                System.err.println("Fant ikke ressurs: " + params.getImagePath());
                continue;
            }
            Image image = new Image(is);
            ImageView imageView = new ImageView(image);
            imageView.setFitWidth(params.getFitWidth());
            imageView.setPreserveRatio(true);
            imageView.setLayoutX(center.getX() + params.getOffsetX() - imageView.getFitWidth() / 2);
            imageView.setLayoutY(center.getY() + params.getOffsetY() - imageView.getBoundsInParent().getHeight() / 2);
            overlayPane.getChildren().add(imageView);
        }
    }

    private String getOverlaysPath(int boardSize) {
        switch (boardSize) {
            case 64:
                return "/overlays64.json";
            case 90: 
                return "/overlays90.json";
            case 120:
                return "/overlays120.json";
            default:
                return "/overlays90.json";
        }
    }

    public List<OverlayParams> loadOverlaysFromJson() {
        List<OverlayParams> overlayList = new ArrayList<>();
        try (InputStream is = getClass().getResourceAsStream(getOverlaysPath(gameModel.getBoard().getSize()))) {
            if (is == null) {
                System.err.println("Fant ikke konfigurasjonsfilen for overlays!");
                return overlayList;
            }
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(is);
            JsonNode overlaysNode = root.path("overlays");
            if (overlaysNode.isArray()) {
                for (JsonNode node : overlaysNode) {
                    int startTileId = node.path("startTileId").asInt();
                    String imagePath = node.path("imagePath").asText();
                    int offsetX = node.path("offsetX").asInt();
                    int offsetY = node.path("offsetY").asInt();
                    int fitWidth = node.path("fitWidth").asInt();
                    overlayList.add(new OverlayParams(imagePath, offsetX, offsetY, fitWidth, startTileId));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return overlayList;
    }

    /**
     * Plasserer en ImageView-token på midten av en gitt rute-ID.
     */
    private void placeTokenOnTile(int tileId, ImageView token) {
        tokenPane.getChildren().remove(token);
        StackPane tile = tileUIMap.get(tileId);
        Point2D center = getTileCenter(tile);
        token.setLayoutX(center.getX() - token.getFitWidth() / 2);
        token.setLayoutY(center.getY() - token.getFitHeight() / 2);
        tokenPane.getChildren().add(token);
    }

    public void movePlayerByDiceRoll(int startTileId, int endTileId, Player player, Runnable onComplete) {
        ImageView token = playerTokens.get(player);
        new Thread(() -> {
            for (int i = startTileId + 1; i <= endTileId; i++) {
                int tileId = i;
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Platform.runLater(() -> placeTokenOnTile(tileId, token));
            }
            if (onComplete != null) {
                Platform.runLater(onComplete);
            }
        }).start();
    }

    public void movePlayerOnSnakeOrLadder(int endTileId, Player player) {
        ImageView token = playerTokens.get(player);
        Platform.runLater(() -> placeTokenOnTile(endTileId, token));
    }

    public void disableRollButton() {
        rollDiceButton.setDisable(true);
    }

    public void enableRollButton() {
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

    public void updateCurrentPlayerView(Player currentPlayer) {
        for (Map.Entry<Player, VBox> entry : playerDisplayBoxes.entrySet()) {
            Player player = entry.getKey();
            VBox box = entry.getValue();
    
            // Fjern stilklassen for alle
            box.getStyleClass().remove("current-player");
    
            // Fjern glow fra token
            ImageView token = playerTokens.get(player);
            token.setEffect(null);
    
            // Finn den innebygde turn-indicator labelen hvis den finnes
            for (javafx.scene.Node node : box.getChildren()) {
                if (node instanceof Label && node.getStyleClass().contains("turn-indicator")) {
                    node.setVisible(player.equals(currentPlayer));
                }
            }
    
            // Dersom dette er spilleren som har tur
            if (player.equals(currentPlayer)) {
                box.getStyleClass().add("current-player");
    
                // Legg til glow på token
                DropShadow glow = new DropShadow(20, javafx.scene.paint.Color.GOLD);
                glow.setSpread(0.5);
                token.setEffect(glow);
            }
        }
    }
}


