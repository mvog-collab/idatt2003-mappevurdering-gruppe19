package edu.ntnu.idatt2003.presentation.service.board;

import edu.ntnu.idatt2003.presentation.fx.OverlayParams;
import java.util.List;
import java.util.Map;
import javafx.geometry.Point2D;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

/**
 * Interface for board UI management services.
 * <p>
 * Defines operations for creating board layouts, managing tiles,
 * positioning tokens, and handling overlay elements.
 * </p>
 */
public interface BoardUIService {
  /**
   * Creates the main board pane with the specified size.
   *
   * @param size the board size
   * @return the constructed board pane
   */
  StackPane createBoardPane(int size);

  /**
   * Creates a single tile for the board.
   *
   * @param id      the tile identifier
   * @param isWhite whether the tile should use white styling
   * @return the created tile pane
   */
  StackPane createTile(int id, boolean isWhite);

  /**
   * Adds overlay elements to the specified pane.
   *
   * @param overlayPane the pane to add overlays to
   * @param overlays    list of overlay parameters
   */
  void addOverlays(Pane overlayPane, List<OverlayParams> overlays);

  /**
   * Places a token on the specified tile.
   *
   * @param tokenPane the pane containing tokens
   * @param token     the token to position
   * @param tileId    the target tile identifier
   */
  void placeTokenOnTile(Pane tokenPane, ImageView token, int tileId);

  /**
   * Places a token at the starting position.
   *
   * @param tokenPane the pane containing tokens
   * @param token     the token to position
   */
  void placeTokenAtStart(Pane tokenPane, ImageView token);

  /**
   * Gets the coordinate mapping for all tiles.
   *
   * @return map of tile IDs to their coordinates
   */
  Map<Integer, Point2D> getTileCoordinates();
}