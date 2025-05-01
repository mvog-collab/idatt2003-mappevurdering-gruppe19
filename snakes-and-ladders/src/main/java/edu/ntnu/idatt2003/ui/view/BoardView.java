package edu.ntnu.idatt2003.ui.view;

import edu.ntnu.idatt2003.gateway.view.PlayerView;
import edu.ntnu.idatt2003.ui.fx.OverlayParams;
import edu.ntnu.idatt2003.utils.Dialogs;
import edu.ntnu.idatt2003.utils.ResourcePaths;
import javafx.application.Platform;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Scene;
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

import java.util.*;

public class BoardView {

    private static final int TILE_SIZE = 50;
    private static final double START_OFFSET_X = -40;
    private static final double START_OFFSET_Y =  0;

    private int width;
    private int height;
    private final int boardSize; 

    /* javafx nodes we need later */
    private final Map<Integer, StackPane> tileUI = new HashMap<>();
    private final Map<String, ImageView> tokenUI = new HashMap<>();
    private final Map<String, VBox> playerBoxes = new HashMap<>();

    private final Pane   overlayPane = new Pane();
    private final Pane   tokenPane   = new Pane();

    private final Button rollButton  = new Button("Roll dice");
    private final Button againButton = new Button("Play again");

    private ImageView die1Img;
    private ImageView die2Img;
    private static final int DIE_SIDE = 50; 

    private VBox playersPanel;
    private final HBox root;

    public BoardView(int boardSize) {
        if (boardSize != 64 && boardSize != 90 && boardSize != 120) {
            throw new IllegalArgumentException("Unsupported board size " + boardSize);
        }
        this.boardSize = boardSize;
        this.root      = new HBox();
    }

    /** Build the scene and return it.  Call **once** directly before showing the stage. */
    public Scene start() {
        Scene scene = new Scene(root, 1000, 700);

        scene.getStylesheets()
            .add(getClass().getResource(ResourcePaths.STYLE_SHEET)
                            .toExternalForm());
        return scene;
    }

    /* ------------------------------------------------------------------
     *  Wiring helpers for controller
     * ------------------------------------------------------------------ */
    public Button getRollButton() {
        return rollButton; 
    }

    public Button getAgainButton() {
        return againButton; 
    }

    public void disableRollButton() {
        rollButton.setDisable(true);
    }

    public void enableRollButton()  {
        rollButton.setDisable(false);
    }

    public void announceWinner(String name) {
        disableRollButton();
        Dialogs.info("Winner!", "Congratulations, " + name + "! You won the game");
    }

    /* ------------------------------------------------------------------
     *  PUBLIC  — snapshot‑based setters called from controller
     * ------------------------------------------------------------------ */

     public void setPlayers(List<PlayerView> players, List<OverlayParams> overlays) {
        Objects.requireNonNull(players);
    
        /* 1. Første kall?  Bygg den statiske delen av brettet ---------------- */
        if (root.getChildren().isEmpty()) {
            buildBoardStatic(boardSize /* ← kommer nå fra konstruktøren */,
                             overlays);
        }
    
        /* 2. Re-bygg sidepanelet --------------------------------------------- */
        playersPanel.getChildren().clear();
        playerBoxes.clear();

        for (PlayerView pv : players) {
            VBox box = createPlayerBox(pv);
            playersPanel.getChildren().add(box);
            playerBoxes.put(pv.token(), box);
        }
    
        /* 3. Plasser alle brikker på riktig rute ------------------------------ */
        tokenPane.getChildren().clear();
        tokenUI.clear();
        for (PlayerView pv : players) {
            ImageView iv = createTokenImage(pv.token());
            tokenUI.put(pv.token(), iv);
            placeTokenOnTile(pv.tileId(), iv);
        }
    
        /* 4. Marker hvem som har tur ----------------------------------------- */
        players.forEach(this::updateTurnIndicator);
    }

    /**
     * Animate one token from start‑id to end‑id and call the callback afterwards.
     */
    public void animateMove(String tokenName, int startId, int endId, Runnable onFinished) {
        ImageView token = tokenUI.get(tokenName);
        if (token == null) { if (onFinished!=null) onFinished.run(); return; }

        new Thread(() -> {
            for (int i = startId + 1; i <= endId; i++) {
                int id = i;
                try { Thread.sleep(200); } catch (InterruptedException ignored) {}
                Platform.runLater(() -> placeTokenOnTile(id, token));
            }
            if (onFinished != null) Platform.runLater(onFinished);
        }).start();
    }

    /* ------------------------------------------------------------------
     *  private helpers – UI construction / effects
     * ------------------------------------------------------------------ */

    private void buildBoardStatic(int boardSize, List<OverlayParams> overlays) {
        setWidthHeight(boardSize);

        GridPane boardGrid = new GridPane();
        boardGrid.setPrefSize(TILE_SIZE * width, TILE_SIZE * height);

        overlayPane.setPickOnBounds(false);
        overlayPane.setPrefSize(boardGrid.getPrefWidth(), boardGrid.getPrefHeight());
        tokenPane.setPickOnBounds(false);
        tokenPane.setPrefSize(boardGrid.getPrefWidth(), boardGrid.getPrefHeight());

        StackPane boardContainer = new StackPane(boardGrid, overlayPane, tokenPane);
        boardContainer.setMaxSize(boardGrid.getPrefWidth(), boardGrid.getPrefHeight());
        HBox.setMargin(boardContainer, new Insets(0, 0, 0, 30));

        VBox playersBox = new VBox();
        playersBox.getStyleClass().add("players-box");
        this.playersPanel = playersBox; 

        HBox diceBox = new HBox(10);                      // liten luft mellom terningene
        diceBox.setPrefSize(220, 120);
        diceBox.setAlignment(Pos.CENTER);
        diceBox.getStyleClass().add("dice-box");

        String imgDir = ResourcePaths.IMAGE_DIR;
        die1Img = new ImageView(new Image(getClass().getResourceAsStream(imgDir + "1.png")));
        die2Img = new ImageView(new Image(getClass().getResourceAsStream(imgDir + "1.png")));
        for (ImageView iv : List.of(die1Img, die2Img)) {
            iv.setFitWidth(DIE_SIDE);
            iv.setFitHeight(DIE_SIDE);
        }
        diceBox.getChildren().addAll(die1Img, die2Img);
        diceBox.setPrefSize(280, 285);
        diceBox.getStyleClass().add("dice-box");

        HBox diceContainer = new HBox(diceBox);
        diceContainer.getStyleClass().add("dice-box-container");

        HBox buttons = new HBox(rollButton, againButton);
        buttons.getStyleClass().add("button-box");

        VBox gameControl = new VBox(playersBox, diceContainer, buttons);
        gameControl.getStyleClass().add("game-control");

        HBox main = new HBox(boardContainer, gameControl);
        main.setAlignment(Pos.CENTER);
        main.getStyleClass().add("main-box");

        root.getChildren().setAll(main);
        root.getStyleClass().add("page-background");

        buildTiles(boardGrid);
        Platform.runLater(() -> addOverlays(overlays));
    }

    public void showDice(int d1, int d2) {
        String dir = ResourcePaths.IMAGE_DIR;
        die1Img.setImage(new Image(getClass().getResourceAsStream(dir + d1 + ".png")));
        die2Img.setImage(new Image(getClass().getResourceAsStream(dir + d2 + ".png")));
    }

    private void buildTiles(GridPane grid) {
        boolean leftToRight = true;
        int id = 1;
        for (int row = height - 1; row >= 0; row--) {
            if (leftToRight) {
                for (int col = 0; col < width; col++) addTile(grid, row, col, id++);
            } else {
                for (int col = width - 1; col >= 0; col--) addTile(grid, row, col, id++);
            }
            leftToRight = !leftToRight;
        }
    }

    private void addTile(GridPane grid, int row, int col, int id) {
        StackPane tile = new StackPane();
        tile.setPrefSize(TILE_SIZE, TILE_SIZE);
        Label lbl = new Label(String.valueOf(id));
        boolean white = (row + col) % 2 == 0;
        tile.getStyleClass().add(white ? "tile-white" : "tile-black");
        lbl .getStyleClass().add(white ? "tile-label-black" : "tile-label-white");
        tile.getChildren().add(lbl);
        grid.add(tile, col, row);
        tileUI.put(id, tile);
    }

    private void addOverlays(List<OverlayParams> overlays) {
        overlayPane.getChildren().clear();
        for (OverlayParams overlayParams : overlays) {
            StackPane start = tileUI.get(overlayParams.getStartTileId() + 1);
            if (start == null) continue;
            Point2D tileCenter = tileCenter(start);
            ImageView imageView = new ImageView(new Image(getClass().getResourceAsStream(overlayParams.getImagePath())));
            imageView.setFitWidth(overlayParams.getFitWidth()); imageView.setPreserveRatio(true);
            imageView.setLayoutX(tileCenter.getX()+overlayParams.getOffsetX()-imageView.getFitWidth()/2);
            imageView.setLayoutY(tileCenter.getY()+overlayParams.getOffsetY()-imageView.getBoundsInParent().getHeight()/2);
            overlayPane.getChildren().add(imageView);
        }
    }

    /* ---------- token & player helpers ---------- */
    private VBox createPlayerBox(PlayerView playerView) {
        VBox box = new VBox();
        box.setAlignment(Pos.CENTER); box.setSpacing(5);
        box.getStyleClass().add("display-player-box");

        ImageView tokenImg = createTokenImage(playerView.token());
        Label turnLbl = new Label("\uD83C\uDFB2 Your turn!");
        tokenImg.setFitWidth(70);     // var 100
        tokenImg.setFitHeight(70);
        turnLbl.getStyleClass().add("turn-indicator"); turnLbl.setVisible(playerView.hasTurn());
        Label nameLbl = new Label(playerView.name()); nameLbl.getStyleClass().add("player-name");

        box.getChildren().addAll(turnLbl, tokenImg, nameLbl);
        if (playerView.hasTurn()) box.getStyleClass().add("current-player");
        return box;
    }

    private ImageView createTokenImage(String tokenName) {
        String path = ResourcePaths.IMAGE_DIR + tokenName.toLowerCase() + "Piece.png";
        ImageView iv = new ImageView(new Image(getClass().getResourceAsStream(path)));
        iv.setFitWidth(40); iv.setFitHeight(40); iv.getStyleClass().add("player-figure");
        return iv;
    }

    private void updateTurnIndicator(PlayerView playerView) {
        VBox box = playerBoxes.get(playerView.token()); 
        if (box == null) return;
        if (playerView.hasTurn()) {
            box.getStyleClass().add("current-player");
        }
        else {
            box.getStyleClass().remove("current-player");
        }
        for (var n : box.getChildren())
            if (n instanceof Label l && l.getStyleClass().contains("turn-indicator"))
                l.setVisible(playerView.hasTurn());
        if (playerView.hasTurn()) {
            ImageView t = tokenUI.get(playerView.token());
            DropShadow glow = new DropShadow(20, javafx.scene.paint.Color.GOLD); glow.setSpread(0.5);
            t.setEffect(glow);
        } else {
            tokenUI.get(playerView.token()).setEffect(null);
        }
    }

    /* ---------- tile helpers ---------- */
    private void placeTokenOnTile(int id, ImageView token) {
        Platform.runLater(() -> placeTokenOnStart(id, token));
        int viewId = Math.min(id, boardSize);                // id==0 håndtert over
        StackPane tile = tileUI.get(viewId);
        if (tile == null) return;

        if (!tokenPane.getChildren().contains(token))
            tokenPane.getChildren().add(token);

        Point2D c = tileCenter(tile);
        token.setLayoutX(c.getX() - token.getFitWidth()  / 2);
        token.setLayoutY(c.getY() - token.getFitHeight() / 2);
    }

    private void placeTokenOnStart(int id, ImageView token) {
        if (id == 0) {
            StackPane tile1 = tileUI.get(1);
            Point2D c = tileCenter(tile1);
    
            token.setLayoutX(c.getX() + START_OFFSET_X - token.getFitWidth()  / 2);
            token.setLayoutY(c.getY() + START_OFFSET_Y - token.getFitHeight() / 2);
            if (!tokenPane.getChildren().contains(token))
                tokenPane.getChildren().add(token);
        }
    }

    private Point2D tileCenter(StackPane tile) {
        Bounds b = tile.getBoundsInParent();
        return new Point2D(b.getMinX()+b.getWidth()/2, b.getMinY()+b.getHeight()/2);
    }

    private void setWidthHeight(int size) {
        switch (size) {
            case 64 -> { width = 8;  height = 8; }
            case 90 -> { width = 9;  height = 10;}
            case 120 -> { width = 10; height = 12;}
            default -> throw new IllegalArgumentException("Unsupported board size " + size);
        }
    }
}
