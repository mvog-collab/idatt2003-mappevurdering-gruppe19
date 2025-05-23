package edu.ntnu.idatt2003.presentation.service.board;

import edu.ntnu.idatt2003.presentation.fx.OverlayParams;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.geometry.Point2D;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

/**
 * Board UI service implementation for Snakes and Ladders games.
 * <p>
 * Manages grid-based board layout with snake and ladder styling,
 * dynamic tile creation, and special tile effects.
 * </p>
 */
public class SnlBoardUIService implements BoardUIService {

  private static final int TILE_SIZE = 50;
  private static final double START_OFFSET_X = -40;
  private static final double START_OFFSET_Y = 0;
  private final Map<Integer, Point2D> tileCoordinates = new HashMap<>();
  private final Map<Integer, StackPane> tiles = new HashMap<>();

  /**
   * Constructs a new SnlBoardUIService.
   */
  public SnlBoardUIService() {
  }

  @Override
  public StackPane createBoardPane(int size) {
    int width;
    int height;
    switch (size) {
      case 64 -> {
        width = 8;
        height = 8;
      }
      case 90 -> {
        width = 9;
        height = 10;
      }
      case 120 -> {
        width = 10;
        height = 12;
      }
      default -> throw new IllegalArgumentException("Unsupported board size: " + size);
    }

    // Create the grid for the board
    GridPane boardGrid = new GridPane();
    boardGrid.getStyleClass().add("board-container");

    double boardDisplayWidth = width * TILE_SIZE;
    double boardDisplayHeight = height * TILE_SIZE;

    boardGrid.setPrefSize(boardDisplayWidth, boardDisplayHeight);
    boardGrid.setMinSize(boardDisplayWidth, boardDisplayHeight);
    boardGrid.setMaxSize(boardDisplayWidth, boardDisplayHeight);

    buildTiles(boardGrid, width, height);

    StackPane boardStack = new StackPane(boardGrid);

    boardStack.setPrefSize(boardDisplayWidth, boardDisplayHeight);
    boardStack.setMinSize(boardDisplayWidth, boardDisplayHeight);
    boardStack.setMaxSize(boardDisplayWidth, boardDisplayHeight);

    return boardStack;
  }

  /**
   * Builds tiles in serpentine pattern for the board grid.
   *
   * @param grid   the grid to add tiles to
   * @param width  the grid width
   * @param height the grid height
   */
  private void buildTiles(GridPane grid, int width, int height) {
    boolean leftToRight = true;
    int id = 1;

    for (int row = height - 1; row >= 0; row--) {
      if (leftToRight) {
        for (int col = 0; col < width; col++) {
          addTile(grid, row, col, id++);
        }
      } else {
        for (int col = width - 1; col >= 0; col--) {
          addTile(grid, row, col, id++);
        }
      }
      leftToRight = !leftToRight;
    }
  }

  /**
   * Applies special styling for snakes and ladders tiles.
   *
   * @param snakes      map of snake head to tail positions
   * @param ladders     map of ladder bottom to top positions
   * @param overlayPane pane for overlay elements
   */
  public void applySpecialTileStyling(
      Map<Integer, Integer> snakes, Map<Integer, Integer> ladders, Pane overlayPane) {
    overlayPane.getChildren().clear();

    for (Map.Entry<Integer, Integer> entry : ladders.entrySet()) {
      int fromId = entry.getKey();
      StackPane fromTile = tiles.get(fromId);
      if (fromTile != null) {
        fromTile.getStyleClass().add("tile-ladder");
      }
    }

    for (Map.Entry<Integer, Integer> entry : snakes.entrySet()) {
      int fromId = entry.getKey();
      StackPane fromTile = tiles.get(fromId);
      if (fromTile != null) {
        fromTile.getStyleClass().add("tile-snake");
      }
    }
  }

  /**
   * Adds a single tile to the grid.
   *
   * @param grid the grid to add to
   * @param row  the row position
   * @param col  the column position
   * @param id   the tile identifier
   */
  private void addTile(GridPane grid, int row, int col, int id) {
    StackPane tile = createTile(id, (row + col) % 2 == 0);
    grid.add(tile, col, row);
    tiles.put(id, tile);

    // Store the center coordinates
    double centerX = col * TILE_SIZE + TILE_SIZE / 2.0;
    double centerY = row * TILE_SIZE + TILE_SIZE / 2.0;
    tileCoordinates.put(id, new Point2D(centerX, centerY));
  }

  @Override
  public StackPane createTile(int id, boolean isWhite) {
    StackPane tile = new StackPane();
    tile.setPrefSize(TILE_SIZE, TILE_SIZE);

    Label label = new Label(String.valueOf(id));
    tile.getStyleClass().add(isWhite ? "tile-white" : "tile-black");
    label.getStyleClass().add(isWhite ? "tile-label-black" : "tile-label-white");

    tile.getChildren().add(label);
    return tile;
  }

  @Override
  public void addOverlays(Pane overlayPane, List<OverlayParams> overlays) {
    overlayPane.getChildren().clear();

    for (OverlayParams params : overlays) {
      Point2D tileCenter = tileCoordinates.get(params.getStartTileId() + 1);
      if (tileCenter == null)
        continue;

      ImageView imageView = new ImageView(new Image(getClass().getResourceAsStream(params.getImagePath())));

      imageView.setFitWidth(params.getFitWidth());
      imageView.setPreserveRatio(true);

      double x = tileCenter.getX() + params.getOffsetX() - imageView.getFitWidth() / 2;
      double y = tileCenter.getY() + params.getOffsetY() - imageView.getFitHeight() / 2;

      imageView.setLayoutX(x);
      imageView.setLayoutY(y);

      overlayPane.getChildren().add(imageView);
    }
  }

  @Override
  public void placeTokenOnTile(Pane tokenPane, ImageView token, int tileId) {
    Point2D position = tileCoordinates.get(tileId);
    if (position == null)
      return;

    if (!tokenPane.getChildren().contains(token)) {
      tokenPane.getChildren().add(token);
    }

    token.setLayoutX(position.getX() - token.getFitWidth() / 2);
    token.setLayoutY(position.getY() - token.getFitHeight() / 2);
  }

  @Override
  public void placeTokenAtStart(Pane tokenPane, ImageView token) {
    Point2D startPosition = tileCoordinates.get(1);
    if (startPosition == null)
      return;

    if (!tokenPane.getChildren().contains(token)) {
      tokenPane.getChildren().add(token);
    }

    token.setLayoutX(startPosition.getX() + START_OFFSET_X - token.getFitWidth() / 2);
    token.setLayoutY(startPosition.getY() + START_OFFSET_Y - token.getFitHeight() / 2);
  }

  @Override
  public Map<Integer, Point2D> getTileCoordinates() {
    return tileCoordinates;
  }
}