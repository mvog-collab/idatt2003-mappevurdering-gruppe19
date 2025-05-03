package edu.ntnu.idatt2003.ui.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.ntnu.idatt2003.gateway.view.PlayerView;
import edu.ntnu.idatt2003.utils.Dialogs;
import edu.ntnu.idatt2003.utils.ResourcePaths;
import javafx.application.Platform;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;

public class LudoBoardView {

  private static final int DIE_SIDE = 50;
  private static final int TOKEN_SIZE = 30;
  private static final int TILE_PX = 35;

  private final HBox root = new HBox();
  private final Button rollButton = new Button("Roll dice");
  private final Button againButton = new Button("Play again");
  private ImageView dieImg;

  private final Pane tokenPane = new Pane();
  private final GridPane tileGrid = new GridPane();

  // Holds each player's list of token ImageViews by uppercase token name
  private final Map<String, List<ImageView>> tokenUi = new HashMap<>();
  // Coordinates for board tiles (1..52 ring + goals)
  private final Map<Integer, Point2D> coords = buildCoordinates();
  // Four spawn coordinates for tokens still in house (tileId == 0)
  private final Map<String, List<Point2D>> spawnCoords = Map.of(
      "RED",    List.of(pt(1,1), pt(1,4), pt(4,1), pt(4,4)),
      "GREEN",  List.of(pt(1,10), pt(1,13), pt(4,10), pt(4,13)),
      "BLUE",   List.of(pt(10,1), pt(10,4), pt(13,1), pt(13,4)),
      "PURPLE", List.of(pt(10,10), pt(10,13), pt(13,10), pt(13,13))
  );

  public LudoBoardView() {
    buildBoardStatic();
  }

  public Scene getScene() {
    Scene scene = new Scene(root, 1000, 700);
    scene.getStylesheets().add(
        getClass().getResource(ResourcePaths.STYLE_SHEET).toExternalForm());
    return scene;
  }

  public Button getRollButton() { return rollButton; }
  public Button getAgainButton() { return againButton; }
  public void disableRollButton() { rollButton.setDisable(true); }
  public void enableRollButton() { rollButton.setDisable(false); }
  public void announceWinner(String name) {
    disableRollButton();
    Dialogs.info("Winner!", "Congratulations, " + name + "! You won the game");
  }

  private void buildBoardStatic() {
    ImageView boardImg = new ImageView(
        new Image(getClass().getResourceAsStream("/images/ludoBoard.jpg")));
    boardImg.setPreserveRatio(true);
    boardImg.setFitWidth(15 * TILE_PX);

    double boardSize = TILE_PX * 15;
    boardImg.setFitWidth(boardSize);
    boardImg.setFitHeight(boardSize);

    tileGrid.setMinSize(boardSize, boardSize);
    tileGrid.setMaxSize(boardSize, boardSize);

    tokenPane.setMinSize(boardSize, boardSize);
    tokenPane.setMaxSize(boardSize, boardSize);

    for (int r = 0; r < 15; r++) {
      for (int c = 0; c < 15; c++) {
        StackPane tile = new StackPane();
        tile.setMinSize(TILE_PX, TILE_PX);
        tile.setPrefSize(TILE_PX, TILE_PX);
        tileGrid.add(tile, c, r);
      }
    }
    tileGrid.setMouseTransparent(true);
    tileGrid.setOpacity(0);

    StackPane board = new StackPane(boardImg, tileGrid, tokenPane);

    HBox diceBox = new HBox(10);
    diceBox.setAlignment(Pos.CENTER);
    diceBox.setPrefSize(300, 300);
    diceBox.getStyleClass().add("dice-box");

    String imgDir = ResourcePaths.IMAGE_DIR;
    dieImg = new ImageView(new Image(
        getClass().getResourceAsStream(imgDir + "1.png")));
    dieImg.setFitWidth(DIE_SIDE);
    dieImg.setFitHeight(DIE_SIDE);
    diceBox.getChildren().add(dieImg);

    rollButton.getStyleClass().add("roll-dice-button");
    againButton.getStyleClass().add("play-again-button");
    HBox buttons = new HBox(rollButton, againButton);
    buttons.setSpacing(10);
    buttons.setAlignment(Pos.CENTER);

    VBox gameControl = new VBox(diceBox, buttons);
    gameControl.setSpacing(20);
    gameControl.setAlignment(Pos.TOP_CENTER);
    gameControl.setPrefWidth(400);
    gameControl.getStyleClass().add("game-control");

    BorderPane main = new BorderPane();
    main.setCenter(board);
    main.setRight(gameControl);
    main.setPadding(new Insets(20));
    BorderPane.setAlignment(board, Pos.CENTER);
    BorderPane.setMargin(board, new Insets(0, 20, 0, 0));
    main.getStyleClass().add("main-box");

    board.setStyle("-fx-border-color: blue; -fx-border-width: 2;");
    tokenPane.setStyle("-fx-border-color: red; -fx-border-width: 2;");

    Platform.runLater(() -> {
      Bounds paneBounds = tokenPane.getBoundsInParent();
      System.out.println("tokenPane size: " + paneBounds.getWidth() + "×" + paneBounds.getHeight());
    });

    root.getChildren().setAll(main);
    root.setAlignment(Pos.CENTER);
    root.getStyleClass().add("page-background");
  }

  private Map<Integer, Point2D> buildCoordinates() {
    Map<Integer, Point2D> map = new HashMap<>();
    int id = 1;
    // Ring (52)
    for (int c = 0; c < 6; c++) map.put(id++, pt(6,c));
    for (int r = 5; r >= 0; r--) map.put(id++, pt(r,6));
    for (int c = 7; c < 13; c++) map.put(id++, pt(0,c));
    for (int r = 1; r < 7; r++) map.put(id++, pt(r,13));
    for (int c = 12; c >= 7; c--) map.put(id++, pt(7,c));
    for (int r = 8; r < 14; r++) map.put(id++, pt(r,6));
    for (int c = 5; c >= 0; c--) map.put(id++, pt(13,c));
    for (int r = 12; r >= 7; r--) map.put(id++, pt(r,0));
    // Goals (4×6)
    for (int c = 1; c <= 5; c++) map.put(id++, pt(7,c));
    for (int r = 1; r <= 5; r++) map.put(id++, pt(r,7));
    for (int c = 9; c <= 13; c++) map.put(id++, pt(7,c));
    for (int r = 9; r <= 13; r++) map.put(id++, pt(r,7));
    return map;
  }

  private Point2D pt(int row, int col) {
    double x = col * TILE_PX + TILE_PX / 2.0;
    double y = row * TILE_PX + TILE_PX / 2.0;
    return new Point2D(x, y);
  }

  // Places all four tokens for a player at appropriate coords
  private void placeTokens(String tokenName, int tileId) {
    List<ImageView> views = tokenUi.get(tokenName);
    List<Point2D> home = spawnCoords.get(tokenName);
    if (views == null || home == null) return;

    if (tileId == 0) {
      // All in house
      for (int i = 0; i < views.size(); i++) {
        Point2D p = home.get(i);
        ImageView iv = views.get(i);
        iv.setLayoutX(p.getX() - iv.getFitWidth() / 2);
        iv.setLayoutY(p.getY() - iv.getFitHeight() / 2);
      }
    } else {
      // One moves, others stay in house
      for (int i = 0; i < views.size(); i++) {
        Point2D p = (i == 0) ? coords.get(tileId) : home.get(i);
        ImageView iv = views.get(i);
        iv.setLayoutX(p.getX() - iv.getFitWidth() / 2);
        iv.setLayoutY(p.getY() - iv.getFitHeight() / 2);
      }
    }
  }

  public void animateMove(String tokenName, int startId, int endId, Runnable onFinished) {
    List<ImageView> views = tokenUi.get(tokenName);
    if (views == null || views.isEmpty()) {
      if (onFinished != null) onFinished.run();
      return;
    }
    // animate the first token only
    ImageView first = views.get(0);
    new Thread(() -> {
      for (int i = startId + 1; i <= endId; i++) {
        int id = i;
        try { Thread.sleep(200); } catch (InterruptedException ignored) {}
        Platform.runLater(() -> placeTokens(tokenName, id));
      }
      if (onFinished != null) Platform.runLater(onFinished);
    }).start();
  }

  public void setPlayers(List<PlayerView> players) {
    tokenPane.getChildren().clear();
    tokenUi.clear();
    for (PlayerView pv : players) {
      String tokenKey = pv.token(); // uppercase
      List<ImageView> views = new ArrayList<>();
      for (int i = 0; i < 4; i++) {
        String imgFile = tokenKey.toLowerCase() + "Piece.png";
        ImageView iv = new ImageView(
            new Image(getClass().getResourceAsStream(
                ResourcePaths.IMAGE_DIR + imgFile)));
        iv.setFitWidth(TOKEN_SIZE);
        iv.setFitHeight(TOKEN_SIZE);
        views.add(iv);
        tokenPane.getChildren().add(iv);
      }
      tokenUi.put(tokenKey, views);
      placeTokens(tokenKey, pv.tileId());
    }
  }

  public void showDice(int d1) {
    String dir = ResourcePaths.IMAGE_DIR;
    dieImg.setImage(new Image(
        getClass().getResourceAsStream(dir + d1 + ".png")));
    dieImg.setRotate(Math.random() * 360);
  }
}
