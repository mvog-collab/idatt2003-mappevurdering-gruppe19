package edu.ntnu.idatt2003.ui.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import edu.ntnu.idatt2003.gateway.view.PlayerView;
import edu.ntnu.idatt2003.ui.fx.OverlayParams;
import edu.ntnu.idatt2003.utils.Dialogs;
import edu.ntnu.idatt2003.utils.ResourcePaths;
import javafx.application.Platform;
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
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

public class LudoBoardView {

  private static final int DIE_SIDE = 50;
  private static final int TOKEN_SIZE = 30;
  private static final int TILE_PX = 35;

  private final HBox root = new HBox();
  private final Button rollButton = new Button("Roll dice");
  private final Button againButton = new Button("Play again");
  private ImageView dieImg;
  private Label statusLabel;
  
  private List<PlayerView> players = new ArrayList<>();
  private int lastRoll = 0;

  private final Pane overlayPane = new Pane();
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
      "YELLOW", List.of(pt(10,10), pt(10,13), pt(13,10), pt(13,13))
  );
  
  // Callback for piece selection
  private Consumer<Integer> pieceSelectedCallback;

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
  
  public void setPieceSelectedCallback(Consumer<Integer> callback) {
    this.pieceSelectedCallback = callback;
  }
  
  public int getLastRoll() {
    return lastRoll;
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

    overlayPane.setMinSize(boardSize, boardSize);
    overlayPane.setMaxSize(boardSize, boardSize);

    StackPane board = new StackPane(
        boardImg,
        tileGrid,
        overlayPane,
        tokenPane
    );

    // DiceBox
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
    
    // Status label
    statusLabel = new Label("Roll the dice to start");
    statusLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
    statusLabel.setTextFill(Color.WHITE);
    statusLabel.setMaxWidth(Double.MAX_VALUE);
    statusLabel.setAlignment(Pos.CENTER);
    statusLabel.setPadding(new Insets(10));
    statusLabel.setBackground(new Background(new BackgroundFill(
        Color.rgb(70, 70, 170, 0.8), new CornerRadii(5), Insets.EMPTY)));

    rollButton.getStyleClass().add("roll-dice-button");
    againButton.getStyleClass().add("play-again-button");
    HBox buttons = new HBox(rollButton, againButton);
    buttons.setSpacing(10);
    buttons.setAlignment(Pos.CENTER);

    VBox gameControl = new VBox(statusLabel, diceBox, buttons);
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
        for (Map.Entry<Integer, Point2D> e : coords.entrySet()) {
          int id = e.getKey();
          Point2D pt = e.getValue();
    
          Label label = new Label(String.valueOf(id));
          label.setStyle("-fx-font-size: 10px; -fx-text-fill: black; -fx-background-color: rgba(255,255,255,0.7);");
    
          label.setLayoutX(pt.getX() - 6);
          label.setLayoutY(pt.getY() - 6);
    
          overlayPane.getChildren().add(label);
        }
    });


    root.getChildren().setAll(main);
    root.setAlignment(Pos.CENTER);
    root.getStyleClass().add("page-background");
  }

  public void setOverlays(List<OverlayParams> overlays) {
    addOverlays(overlays);
  }

  private void addOverlays(List<OverlayParams> overlays) {
    overlayPane.getChildren().clear();

    for (OverlayParams p : overlays) {

      Point2D center = coords.get(p.getStartTileId());
      if (center == null) continue;

      ImageView iv = new ImageView(
        new Image(getClass().getResourceAsStream(p.getImagePath()))
      );
      iv.setFitWidth(p.getFitWidth());
      iv.setPreserveRatio(true);

      iv.setLayoutX(center.getX() + p.getOffsetX() - iv.getFitWidth()/2);
      iv.setLayoutY(center.getY() + p.getOffsetY() - iv.getBoundsInParent().getHeight()/2);

      overlayPane.getChildren().add(iv);
    }
  }

  private Map<Integer, Point2D> buildCoordinates() {
    Map<Integer, Point2D> map = new HashMap<>();
  
    int[][] tileMap = {
      { 0,  0,  0,  0,  0,  0, 24, 25, 26, 0, 0,  0,  0,  0,  0 },
      { 0,  0,  0,  0,  0,  0, 23, 65, 27, 0, 0,  0,  0,  0,  0 },
      { 0,  0,  0,  0,  0,  0, 22, 66, 28, 0, 0,  0,  0,  0,  0 },
      { 0,  0,  0,  0,  0,  0, 21, 67,  29,  0, 0,  0,  0,  0,  0 },
      { 0,  0,  0,  0,  0,  0, 20, 68,  30,  0, 0,  0,  0,  0,  0 },
      { 0,  0,  0,  0,  0,  0, 19,  69, 31, 0, 0, 0,  0,  0,  0 },
      { 13,  14,  15,  16,  17,  18,  0,  70, 0, 32, 33, 34, 35, 36, 37 },
      { 12,  59,  60,  61,  62,  63,  64, 0,  76,  75, 74,  73,  72,  71, 38 },
      { 11,  10,  9,  8, 7, 6, 0, 58,  0, 44, 43, 42, 41,  40, 39 },
      { 0,  0,  0,  0,  0,  0, 5, 57, 45, 0, 0, 0, 0, 0, 0 },
      { 0,  0,  0,  0,  0,  0, 4, 56,  46,  0,  0,  0,  0,  0, 0 },
      { 0,  0,  0,  0,  0,  0, 3, 55,  47,  0,  0,  0,  0,  0, 0 },
      { 0,  0,  0,  0,  0,  0, 2, 54,  48,  0,  0,  0,  0,  0, 0 },
      { 0,  0,  0,  0,  0,  0, 1, 53, 49, 0, 0, 0,  0,  0, 0 },
      { 0,  0,  0,  0,  0,  0, 52,  51,  50,  0,  0,  0,  0,  0, 0 } 
    };
  
    for (int row = 0; row < tileMap.length; row++) {
      for (int col = 0; col < tileMap[row].length; col++) {
        int id = tileMap[row][col];
        if (id > 0)
          map.put(id, pt(row, col));
      }
    }
  
    return map;
  }

  private Point2D pt(int row, int col) {
    double x = col * TILE_PX + TILE_PX / 2.0;
    double y = row * TILE_PX + TILE_PX / 2.0;
    return new Point2D(x, y);
  }

  // Place a specific piece at the given tile ID
  private void placePiece(String tokenName, int pieceIndex, int tileId) {
    List<ImageView> tokens = tokenUi.get(tokenName);
    if (tokens == null || pieceIndex >= tokens.size()) return;
    
    ImageView token = tokens.get(pieceIndex);
    
    if (tileId <= 0) {
        // Place at home position
        List<Point2D> homePositions = spawnCoords.get(tokenName);
        if (homePositions != null && pieceIndex < homePositions.size()) {
            Point2D pos = homePositions.get(pieceIndex);
            
            token.setLayoutX(pos.getX() - token.getFitWidth() / 2);
            token.setLayoutY(pos.getY() - token.getFitHeight() / 2);
            
            if (!tokenPane.getChildren().contains(token)) {
                tokenPane.getChildren().add(token);
            }
        }
    } else {
        // Place on board
        Point2D target = coords.get(tileId);
        if (target == null) return;
        
        // Add an offset based on piece index to avoid exact overlap
        double offsetX = (pieceIndex % 2) * 8 - 4;
        double offsetY = (pieceIndex / 2) * 8 - 4;
        
        token.setLayoutX(target.getX() + offsetX - token.getFitWidth() / 2);
        token.setLayoutY(target.getY() + offsetY - token.getFitHeight() / 2);
        
        if (!tokenPane.getChildren().contains(token)) {
            tokenPane.getChildren().add(token);
        }
    }
  }

  // Method to update the display with new player data
  // Place a specific token at the given tile ID
  private void placeToken(int tileId, ImageView token) {
    Point2D target = coords.get(tileId);
    
    if (target == null) {
        if (tileId <= 0) {
            return;
        }
        return;
    }

    // Add token to the pane if it's not already there
    if (!tokenPane.getChildren().contains(token)) {
        tokenPane.getChildren().add(token);
    }
    
    // Position token centered on target
    token.setLayoutX(target.getX() - token.getFitWidth() / 2);
    token.setLayoutY(target.getY() - token.getFitHeight() / 2);
  }

  // Place all tokens for a player at the given tile ID or at home positions
  private void placeTokens(String tokenName, int tileId) {
    List<ImageView> tokens = tokenUi.get(tokenName);
    if (tokens == null || tokens.isEmpty()) return;
    
    if (tileId <= 0) {
        // Place tokens in home positions
        List<Point2D> homePositions = spawnCoords.get(tokenName);
        if (homePositions == null) return;
        
        for (int i = 0; i < Math.min(tokens.size(), homePositions.size()); i++) {
            Point2D pos = homePositions.get(i);
            ImageView token = tokens.get(i);
            
            token.setLayoutX(pos.getX() - token.getFitWidth() / 2);
            token.setLayoutY(pos.getY() - token.getFitHeight() / 2);
            
            if (!tokenPane.getChildren().contains(token)) {
                tokenPane.getChildren().add(token);
            }
        }
    } else {
        // Place first token on the board, others at home
        placeToken(tileId, tokens.get(0));
        
        // Place other tokens at home
        List<Point2D> homePositions = spawnCoords.get(tokenName);
        if (homePositions == null) return;
        
        for (int i = 1; i < Math.min(tokens.size(), homePositions.size()); i++) {
            Point2D pos = homePositions.get(i);
            ImageView token = tokens.get(i);
            
            token.setLayoutX(pos.getX() - token.getFitWidth() / 2);
            token.setLayoutY(pos.getY() - token.getFitHeight() / 2);
            
            if (!tokenPane.getChildren().contains(token)) {
                tokenPane.getChildren().add(token);
            }
        }
    }
  }

  public void animateMoveAlongPath(String tokenName, List<Integer> path, Runnable onFinished) {
      List<ImageView> tokens = tokenUi.get(tokenName);
      if (tokens == null || tokens.isEmpty()) {
          if (onFinished != null) Platform.runLater(onFinished);
          return;
      }

      // Use the first token for movement
      ImageView token = tokens.get(0);

      new Thread(() -> {
          try {
              for (Integer id : path) {
                  Thread.sleep(200); 
                  Platform.runLater(() -> placeToken(id, token));
              }
          } catch (InterruptedException ignored) {}
          if (onFinished != null) Platform.runLater(onFinished);
      }).start();
  }

  public void setPlayers(List<PlayerView> players) {
    this.players = new ArrayList<>(players);
    tokenPane.getChildren().clear();
    tokenUi.clear();
    
    for (PlayerView pv : players) {
        String tokenKey = pv.token(); // uppercase
        List<ImageView> views = new ArrayList<>();
        
        // Create 4 pieces for each player
        for (int i = 0; i < pv.piecePositions().size(); i++) {
            String imgFile = tokenKey.toLowerCase() + "Piece.png";
            ImageView iv = new ImageView(
                new Image(getClass().getResourceAsStream(
                    ResourcePaths.IMAGE_DIR + imgFile)));
            iv.setFitWidth(TOKEN_SIZE);
            iv.setFitHeight(TOKEN_SIZE);
            
            // Add click handler for piece selection
            final int pieceIndex = i;
            iv.setOnMouseClicked(e -> onPieceClicked(tokenKey, pieceIndex));
            
            // Highlight active piece
            if (pv.activePieceIndex() == i) {
                iv.setEffect(new DropShadow(10, Color.GOLD));
            }
            
            views.add(iv);
            tokenPane.getChildren().add(iv);
        }
        
        tokenUi.put(tokenKey, views);
        
        // Place all pieces according to their positions
        for (int i = 0; i < pv.piecePositions().size(); i++) {
            int tileId = pv.piecePositions().get(i);
            placePiece(tokenKey, i, tileId);
        }
    }
    
    // Update status message for the current player
    updateStatusForCurrentPlayer();
  }
  
  private void updateStatusForCurrentPlayer() {
    PlayerView currentPlayer = players.stream()
        .filter(PlayerView::hasTurn)
        .findFirst()
        .orElse(null);
        
    if (currentPlayer != null) {
        statusLabel.setText(currentPlayer.name() + "'s turn");
    } else {
        statusLabel.setText("Roll the dice to start");
    }
  }

  private void onPieceClicked(String tokenKey, int pieceIndex) {
    // Only respond to clicks from the current player's pieces
    PlayerView currentPlayer = players.stream()
        .filter(PlayerView::hasTurn)
        .findFirst()
        .orElse(null);
        
    if (currentPlayer == null || !currentPlayer.token().equals(tokenKey)) {
        return;
    }
    
    // If piece is at home and last roll wasn't 6, don't allow selection
    if (pieceIndex < currentPlayer.piecePositions().size()) {
        int position = currentPlayer.piecePositions().get(pieceIndex);
        if (position <= 0 && lastRoll != 6) {
            showAlert("Invalid move", "You need to roll a 6 to move a piece from home.");
            return;
        }
    }
    
    // Tell the controller that this piece was selected
    if (pieceSelectedCallback != null) {
        pieceSelectedCallback.accept(pieceIndex);
    }
  }

  // Display a message in the status area
  public void showStatusMessage(String message) {
    statusLabel.setText(message);
  }
  
  // Show an alert dialog
  public void showAlert(String title, String message) {
    Alert alert = new Alert(Alert.AlertType.INFORMATION);
    alert.setTitle(title);
    alert.setHeaderText(null);
    alert.setContentText(message);
    alert.showAndWait();
  }

  // Method to animate a specific piece along a path
  public void animateMoveAlongPath(String tokenName, int pieceIndex, List<Integer> path, Runnable onFinished) {
    List<ImageView> tokens = tokenUi.get(tokenName);
    if (tokens == null || pieceIndex >= tokens.size() || path.isEmpty()) {
        if (onFinished != null) Platform.runLater(onFinished);
        return;
    }
    
    ImageView token = tokens.get(pieceIndex);
    
    // Handle empty or single-item paths
    if (path.size() <= 1) {
        if (onFinished != null) Platform.runLater(onFinished);
        return;
    }
    
    new Thread(() -> {
        try {
            // Skip the first element as it's the current position
            for (int i = 1; i < path.size(); i++) {
                int id = path.get(i);
                Thread.sleep(200);
                
                Platform.runLater(() -> {
                    // Calculate offset based on piece index
                    double offsetX = (pieceIndex % 2) * 8 - 4;
                    double offsetY = (pieceIndex / 2) * 8 - 4;
                    
                    // Get target position
                    Point2D target = coords.get(id);
                    if (target != null) {
                        token.setLayoutX(target.getX() + offsetX - token.getFitWidth() / 2);
                        token.setLayoutY(target.getY() + offsetY - token.getFitHeight() / 2);
                    }
                });
            }
        } catch (InterruptedException ignored) {}
        
        if (onFinished != null) Platform.runLater(onFinished);
    }).start();
  }

  private void placePieceWithAnimation(String tokenName, int pieceIndex, int tileId) {
    List<ImageView> tokens = tokenUi.get(tokenName);
    if (tokens == null || pieceIndex >= tokens.size()) return;
    
    ImageView token = tokens.get(pieceIndex);
    
    if (tileId <= 0) {
        // Handle home positions
        List<Point2D> homePositions = spawnCoords.get(tokenName);
        if (homePositions != null && pieceIndex < homePositions.size()) {
            Point2D pos = homePositions.get(pieceIndex);
            
            token.setLayoutX(pos.getX() - token.getFitWidth() / 2);
            token.setLayoutY(pos.getY() - token.getFitHeight() / 2);
        }
    } else {
        // Handle board positions with animation
        Point2D target = coords.get(tileId);
        if (target == null) return;
        
        // Add an offset based on piece index to avoid exact overlap
        double offsetX = (pieceIndex % 2) * 8 - 4;
        double offsetY = (pieceIndex / 2) * 8 - 4;
        
        token.setLayoutX(target.getX() + offsetX - token.getFitWidth() / 2);
        token.setLayoutY(target.getY() + offsetY - token.getFitHeight() / 2);
    }
  }

  public void showDice(int d1) {
    String dir = ResourcePaths.IMAGE_DIR;
    dieImg.setImage(new Image(
        getClass().getResourceAsStream(dir + d1 + ".png")));
    dieImg.setRotate(Math.random() * 360);
    lastRoll = d1;
  }
}