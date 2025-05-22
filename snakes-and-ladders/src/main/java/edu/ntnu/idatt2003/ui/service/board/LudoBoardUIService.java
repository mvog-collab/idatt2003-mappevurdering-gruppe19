package edu.ntnu.idatt2003.ui.service.board;

import edu.ntnu.idatt2003.persistence.LudoBoardAdapter;
import edu.ntnu.idatt2003.persistence.LudoBoardFactory;
import edu.ntnu.idatt2003.ui.fx.OverlayParams;
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
  private static final String DEFAULT_BOARD_CONFIG = "/boards/ludoBoard.json";

  private final int tileSize;
  private final Map<Integer, Point2D> coordinates;
  private final Map<String, List<Point2D>> homePositions;

  public LudoBoardUIService() {
    this(DEFAULT_BOARD_CONFIG);
  }

  public LudoBoardUIService(String boardConfigPath) {
    LudoBoardAdapter.LudoMapData boardData = LudoBoardFactory.loadFromClasspath(boardConfigPath);
    this.tileSize = boardData.tileSize();
    this.coordinates = boardData.coordinates();
    this.homePositions = boardData.homePositions();
  }

  public LudoBoardUIService(LudoBoardAdapter.LudoMapData boardData) {
    this.tileSize = boardData.tileSize();
    this.coordinates = boardData.coordinates();
    this.homePositions = boardData.homePositions();
  }

  public void initializeGameBoardArea(StackPane gameBoardAreaContainer, Pane overlayPane, Pane tokenPane) {
    double boardActualSize = this.tileSize * 15;

    ImageView boardImg = new ImageView(new Image(getClass().getResourceAsStream("/images/ludoBoard.jpg")));
    boardImg.setPreserveRatio(true);
    boardImg.setFitWidth(boardActualSize);
    boardImg.setFitHeight(boardActualSize);

    overlayPane.setPrefSize(boardActualSize, boardActualSize);
    overlayPane.setMinSize(boardActualSize, boardActualSize);
    overlayPane.setMaxSize(boardActualSize, boardActualSize);

    tokenPane.setPrefSize(boardActualSize, boardActualSize);
    tokenPane.setMinSize(boardActualSize, boardActualSize);
    tokenPane.setMaxSize(boardActualSize, boardActualSize);

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
    return new StackPane();
  }

  @Override
  public void addOverlays(Pane overlayPane, List<OverlayParams> overlays) {
    overlayPane.getChildren().clear();
    if (overlays == null)
      return;

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
    if (target == null || token == null)
      return;

    if (!tokenPane.getChildren().contains(token)) {
      tokenPane.getChildren().add(token);
    }

    token.setLayoutX(target.getX() - token.getFitWidth() / 2);
    token.setLayoutY(target.getY() - token.getFitHeight() / 2);
  }

  @Override
  public void placeTokenAtStart(Pane tokenPane, ImageView token) {
    // This method is less specific for Ludo; use placePieceAtHome.
  }

  public void placePieceAtHome(Pane tokenPane, ImageView token, String tokenName, int pieceIndex) {
    if (token == null || tokenName == null || homePositions == null)
      return;
    List<Point2D> playerHomePositions = this.homePositions.get(tokenName.toUpperCase());
    if (playerHomePositions == null || pieceIndex < 0 || pieceIndex >= playerHomePositions.size())
      return;

    Point2D position = playerHomePositions.get(pieceIndex);
    if (position == null)
      return;

    if (!tokenPane.getChildren().contains(token)) {
      tokenPane.getChildren().add(token);
    }

    token.setLayoutX(position.getX() - token.getFitWidth() / 2);
    token.setLayoutY(position.getY() - token.getFitHeight() / 2);
  }

  public void placePieceOnBoard(Pane tokenPane, ImageView token, int tileId, int pieceIndex) {
    Point2D target = coordinates.get(tileId);
    if (target == null || token == null)
      return;

    double offsetX = (pieceIndex % 2) * 8 - 4;
    double offsetY = (pieceIndex / 2) * 8 - 4;

    if (!tokenPane.getChildren().contains(token)) {
      tokenPane.getChildren().add(token);
    }

    token.setLayoutX(target.getX() + offsetX - token.getFitWidth() / 2);
    token.setLayoutY(target.getY() + offsetY - token.getFitHeight() / 2);
  }

  public void highlightActivePiece(ImageView piece) {
    if (piece == null)
      return;
    DropShadow highlight = new DropShadow(10, Color.BLACK);
    highlight.setSpread(0.8);
    piece.setEffect(highlight);
  }

  public void removeHighlight(ImageView piece) {
    if (piece == null)
      return;
    piece.setEffect(null);
  }

  @Override
  public Map<Integer, Point2D> getTileCoordinates() {
    return this.coordinates;
  }
}