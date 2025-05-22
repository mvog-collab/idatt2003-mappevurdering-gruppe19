package edu.ntnu.idatt2003.ui.service.board;

import edu.ntnu.idatt2003.ui.fx.OverlayParams;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.geometry.Point2D;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

public class LudoBoardUIService implements BoardUIService {
  public static final int TILE_PX = 35;
  private final Map<Integer, Point2D> coordinates;
  private final Map<String, List<Point2D>> homePositions;

  public LudoBoardUIService() {
    this.coordinates = buildCoordinates();
    this.homePositions = buildHomePositions();
  }

  public void initializeGameBoardArea(StackPane gameBoardAreaContainer, Pane overlayPane, Pane tokenPane) {
    double boardActualSize = TILE_PX * 15;

    ImageView boardImg = new ImageView(new Image(getClass().getResourceAsStream("/images/ludoBoard.jpg")));
    boardImg.setPreserveRatio(true);
    boardImg.setFitWidth(boardActualSize);
    boardImg.setFitHeight(boardActualSize);

    // Configure the passed-in panes
    overlayPane.setPrefSize(boardActualSize, boardActualSize);
    overlayPane.setMinSize(boardActualSize, boardActualSize);
    overlayPane.setMaxSize(boardActualSize, boardActualSize);

    tokenPane.setPrefSize(boardActualSize, boardActualSize);
    tokenPane.setMinSize(boardActualSize, boardActualSize);
    tokenPane.setMaxSize(boardActualSize, boardActualSize);

    // Add children to the container passed from the View
    gameBoardAreaContainer.getChildren().setAll(boardImg, overlayPane, tokenPane);
    gameBoardAreaContainer.setPrefSize(boardActualSize, boardActualSize);
    gameBoardAreaContainer.setMinSize(boardActualSize, boardActualSize);
    gameBoardAreaContainer.setMaxSize(boardActualSize, boardActualSize);
  }

  @Override
  public StackPane createBoardPane(int sizeIgnoredForLudo) {
    StackPane boardArea = new StackPane();
    Pane internalOverlayPane = new Pane();
    Pane internalTokenPane = new Pane();
    initializeGameBoardArea(boardArea, internalOverlayPane, internalTokenPane);
    return boardArea;
  }

  @Override
  public StackPane createTile(int id, boolean isWhite) {
    // Not used in Ludo implementation
    return new StackPane();
  }

  @Override
  public void addOverlays(Pane overlayPane, List<OverlayParams> overlays) {
    overlayPane.getChildren().clear();

    for (OverlayParams params : overlays) {
      Point2D center = coordinates.get(params.getStartTileId());
      if (center == null)
        continue;

      ImageView iv = new ImageView(new Image(getClass().getResourceAsStream(params.getImagePath())));
      iv.setFitWidth(params.getFitWidth());
      iv.setPreserveRatio(true);

      iv.setLayoutX(center.getX() + params.getOffsetX() - iv.getFitWidth() / 2);
      iv.setLayoutY(center.getY() + params.getOffsetY() - iv.getBoundsInParent().getHeight() / 2);

      overlayPane.getChildren().add(iv);
    }
  }

  @Override
  public void placeTokenOnTile(Pane tokenPane, ImageView token, int tileId) {
    Point2D target = coordinates.get(tileId);
    if (target == null)
      return;

    if (!tokenPane.getChildren().contains(token)) {
      tokenPane.getChildren().add(token);
    }

    token.setLayoutX(target.getX() - token.getFitWidth() / 2);
    token.setLayoutY(target.getY() - token.getFitHeight() / 2);
  }

  @Override
  public void placeTokenAtStart(Pane tokenPane, ImageView token) {
    // Default implementation - would need to know which player's token it is
    // Instead use placePieceAtHome
  }

  public void placePieceAtHome(Pane tokenPane, ImageView token, String tokenName, int pieceIndex) {
    List<Point2D> homePositions = this.homePositions.get(tokenName);
    if (homePositions == null || pieceIndex >= homePositions.size())
      return;

    Point2D position = homePositions.get(pieceIndex);

    if (!tokenPane.getChildren().contains(token)) {
      tokenPane.getChildren().add(token);
    }

    token.setLayoutX(position.getX() - token.getFitWidth() / 2);
    token.setLayoutY(position.getY() - token.getFitHeight() / 2);
  }

  public void placePieceOnBoard(Pane tokenPane, ImageView token, int tileId, int pieceIndex) {
    Point2D target = coordinates.get(tileId);
    if (target == null)
      return;

    // Add offset based on piece index to avoid exact overlap
    double offsetX = (pieceIndex % 2) * 8 - 4;
    double offsetY = (pieceIndex / 2) * 8 - 4;

    if (!tokenPane.getChildren().contains(token)) {
      tokenPane.getChildren().add(token);
    }

    token.setLayoutX(target.getX() + offsetX - token.getFitWidth() / 2);
    token.setLayoutY(target.getY() + offsetY - token.getFitHeight() / 2);
  }

  public void highlightActivePiece(ImageView piece) {
    DropShadow highlight = new DropShadow(10, Color.BLACK);
    highlight.setSpread(0.8);
    piece.setEffect(highlight);
  }

  public void removeHighlight(ImageView piece) {
    piece.setEffect(null);
  }

  @Override
  public Map<Integer, Point2D> getTileCoordinates() {
    return coordinates;
  }

  private Map<Integer, Point2D> buildCoordinates() {
    Map<Integer, Point2D> map = new HashMap<>();

    int[][] tileMap = {
        { 0, 0, 0, 0, 0, 0, 24, 25, 26, 0, 0, 0, 0, 0, 0 },
        { 0, 0, 0, 0, 0, 0, 23, 65, 27, 0, 0, 0, 0, 0, 0 },
        { 0, 0, 0, 0, 0, 0, 22, 66, 28, 0, 0, 0, 0, 0, 0 },
        { 0, 0, 0, 0, 0, 0, 21, 67, 29, 0, 0, 0, 0, 0, 0 },
        { 0, 0, 0, 0, 0, 0, 20, 68, 30, 0, 0, 0, 0, 0, 0 },
        { 0, 0, 0, 0, 0, 0, 19, 69, 31, 0, 0, 0, 0, 0, 0 },
        { 13, 14, 15, 16, 17, 18, 0, 70, 0, 32, 33, 34, 35, 36, 37 },
        { 12, 59, 60, 61, 62, 63, 64, 0, 76, 75, 74, 73, 72, 71, 38 },
        { 11, 10, 9, 8, 7, 6, 0, 58, 0, 44, 43, 42, 41, 40, 39 },
        { 0, 0, 0, 0, 0, 0, 5, 57, 45, 0, 0, 0, 0, 0, 0 },
        { 0, 0, 0, 0, 0, 0, 4, 56, 46, 0, 0, 0, 0, 0, 0 },
        { 0, 0, 0, 0, 0, 0, 3, 55, 47, 0, 0, 0, 0, 0, 0 },
        { 0, 0, 0, 0, 0, 0, 2, 54, 48, 0, 0, 0, 0, 0, 0 },
        { 0, 0, 0, 0, 0, 0, 1, 53, 49, 0, 0, 0, 0, 0, 0 },
        { 0, 0, 0, 0, 0, 0, 52, 51, 50, 0, 0, 0, 0, 0, 0 }
    };

    for (int row = 0; row < tileMap.length; row++) {
      for (int col = 0; col < tileMap[row].length; col++) {
        int id = tileMap[row][col];
        if (id > 0)
          map.put(id, createPoint(row, col));
      }
    }

    return map;
  }

  private Map<String, List<Point2D>> buildHomePositions() {
    Map<String, List<Point2D>> positions = new HashMap<>();

    // Four spawn coordinates for tokens still in house (tileId == 0)
    positions.put(
        "RED",
        List.of(
            createPoint(1, 1), createPoint(1, 4),
            createPoint(4, 1), createPoint(4, 4)));

    positions.put(
        "GREEN",
        List.of(
            createPoint(1, 10), createPoint(1, 13),
            createPoint(4, 10), createPoint(4, 13)));

    positions.put(
        "BLUE",
        List.of(
            createPoint(10, 1), createPoint(10, 4),
            createPoint(13, 1), createPoint(13, 4)));

    positions.put(
        "YELLOW",
        List.of(
            createPoint(10, 10), createPoint(10, 13),
            createPoint(13, 10), createPoint(13, 13)));

    return positions;
  }

  private Point2D createPoint(int row, int col) {
    double x = col * TILE_PX + TILE_PX / 2.0;
    double y = row * TILE_PX + TILE_PX / 2.0;
    return new Point2D(x, y);
  }
}
