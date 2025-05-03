package edu.ntnu.idatt2003.ui.view;

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
  private final HBox root = new HBox();

  private final Button rollButton = new Button("Roll dice");
  private final Button againButton = new Button("Play again");

  private ImageView dieImg;

  private final Pane  tokenPane   = new Pane();
  private final GridPane tileGrid = new GridPane();
  private final Map<Integer, StackPane> tileUi = new HashMap<>();
  private static final int TILE_PX = 35;

  private final Map<String, ImageView> tokenUi = new HashMap<>();
  private final Map<Integer, Point2D> coords   = buildCoordinates();   // init én gang

  public LudoBoardView() {
    buildBoardStatic();
  }

  public Scene getScene() {
    Scene scene = new Scene(root, 1000, 700);
    scene.getStylesheets().add(
        getClass().getResource(ResourcePaths.STYLE_SHEET).toExternalForm());
    return scene;
  }

  public Button getRollButton() {
    return rollButton;
  }

  public Button getAgainButton() {
    return againButton;
  }

  public void disableRollButton() {
    rollButton.setDisable(true);
  }

  public void enableRollButton() {
    rollButton.setDisable(false);
  }

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

    for (int r = 0; r < 15; r++)
        for (int c = 0; c < 15; c++) {
            StackPane tile = new StackPane();
            tile.setMinSize(TILE_PX, TILE_PX);
            tile.setPrefSize(TILE_PX, TILE_PX);
            tileGrid.add(tile, c, r);
        }
    tileGrid.setMouseTransparent(true);
    tileGrid.setOpacity(0);

    StackPane board = new StackPane(boardImg, tileGrid, tokenPane);

    // DiceBox
    HBox diceBox = new HBox(10);
    diceBox.setAlignment(Pos.CENTER);
    diceBox.setPrefSize(300, 300);
    diceBox.getStyleClass().add("dice-box");

    String imgDir = ResourcePaths.IMAGE_DIR;
    dieImg = new ImageView(new Image(getClass().getResourceAsStream(imgDir + "1.png")));
    dieImg.setFitWidth(DIE_SIDE);
    dieImg.setFitHeight(DIE_SIDE);
    diceBox.getChildren().add(dieImg);

    // Buttons
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
    board.setStyle("-fx-border-color: blue; -fx-border-width: 2;");      // around the entire board stack
    tokenPane.setStyle("-fx-border-color: red; -fx-border-width: 2;");  // around your token layer

    // wherever you finish building your scene, e.g. in your controller, after show():
    Platform.runLater(() -> {
        Bounds imgBounds   = boardImg.getBoundsInParent();
        Bounds paneBounds  = tokenPane.getBoundsInParent();
        System.out.println("boardImg:   " + imgBounds.getWidth() + "×" + imgBounds.getHeight()
                          + " @ " + imgBounds.getMinX() + "," + imgBounds.getMinY());
        System.out.println("tokenPane:  " + paneBounds.getWidth() + "×" + paneBounds.getHeight()
                          + " @ " + paneBounds.getMinX() + "," + paneBounds.getMinY());
    });

    root.getChildren().setAll(main);
    root.setAlignment(Pos.CENTER);
    root.getStyleClass().add("page-background");
  }

  private Map<Integer, Point2D> buildCoordinates() {
    Map<Integer, Point2D> map = new HashMap<>();

    /* --- 1) ringet (52 felter) ---------------------- */

    int id = 1;

    // rad 6, kol 0‑5  (BLUE start → høyre)
    for (int c = 0; c < 6; c++) map.put(id++, pt(6,c));

    // kol 6, rad 5‑0  (opp)
    for (int r = 5; r >= 0; r--) map.put(id++, pt(r,6));

    // rad 0, kol 7‑12 (høyre)
    for (int c = 7; c < 13; c++) map.put(id++, pt(0,c));

    // kol 13, rad 1‑6 (ned)
    for (int r = 1; r < 7; r++) map.put(id++, pt(r,13));

    // rad 7, kol 12‑7 (venstre)
    for (int c = 12; c >= 7; c--) map.put(id++, pt(7,c));

    // kol 6, rad 8‑13 (ned)
    for (int r = 8; r < 14; r++) map.put(id++, pt(r,6));

    // rad 13, kol 5‑0 (venstre)
    for (int c = 5; c >= 0; c--) map.put(id++, pt(13,c));

    // kol 0, rad 12‑7 (opp)
    for (int r = 12; r >= 7; r--) map.put(id++, pt(r,0));

    /* --- 2) mål‑feltene ----------------------------- */

    // BLUE goal (rad 7, col 1‑5)
    for (int c = 1; c <= 5; c++) map.put(id++, pt(7,c));

    // RED goal (col 7, rad 1‑5)
    for (int r = 1; r <= 5; r++) map.put(id++, pt(r,7));

    // GREEN goal (rad 7, col 9‑13)
    for (int c = 9; c <= 13; c++) map.put(id++, pt(7,c));

    // PURPLE goal (col 7, rad 9‑13)
    for (int r = 9; r <= 13; r++) map.put(id++, pt(r,7));

    return map;
  }

  private Point2D pt(int row,int col) {
    double x = col * TILE_PX + TILE_PX/2.0;
    double y = row * TILE_PX + TILE_PX/2.0;
    return new Point2D(x, y);
  }

  private void placeToken(int tileId, ImageView token) {
    Point2D p = coords.get(tileId);
    if (p == null) return;
    if (!tokenPane.getChildren().contains(token))
        tokenPane.getChildren().add(token);

    token.setLayoutX(p.getX() - token.getFitWidth() / 2);
    token.setLayoutY(p.getY() - token.getFitHeight() / 2);
  }

  public void animateMove(String tokenName, int startId, int endId, Runnable onFinished) {
        ImageView token = tokenUi.get(tokenName);
        if (token == null) { if (onFinished!=null) onFinished.run(); return; }

        new Thread(() -> {
            for (int i = startId + 1; i <= endId; i++) {
                int id = i;
                try { Thread.sleep(200); } catch (InterruptedException ignored) {}
                Platform.runLater(() -> placeToken(id, token));
            }
            if (onFinished != null) Platform.runLater(onFinished);
        }).start();
  }

  public void setPlayers(List<PlayerView> players) {
    // clear existing tokens
    tokenPane.getChildren().clear();
    tokenUi.clear();
    // for each player, create an ImageView and position it
    for (PlayerView pv : players) {
      String tokenName = pv.token().toLowerCase();
      ImageView iv = new ImageView(
        new Image(getClass().getResourceAsStream(
          ResourcePaths.IMAGE_DIR + tokenName + "Piece.png"))
      );
      iv.setFitWidth(TOKEN_SIZE);
      iv.setFitHeight(TOKEN_SIZE);
      tokenUi.put(pv.token(), iv);
      // initial placement
      placeToken(pv.tileId(), iv);
    }
  }

  public void showDice(int d1) {
    String dir = ResourcePaths.IMAGE_DIR;
    dieImg.setImage(new Image(getClass().getResourceAsStream(dir + d1 + ".png")));
    dieImg.setRotate(Math.random() * 360);
  }
}