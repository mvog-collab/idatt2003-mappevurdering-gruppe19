package edu.ntnu.idatt2003.presentation.fx;

/**
 * Parameters for rendering an overlay image on the game board.
 * <p>
 * Encapsulates the image path, position offsets, display width, and
 * the starting tile ID to which the overlay applies.
 * </p>
 */
public class OverlayParams {
  private final String imagePath;
  private final double offsetX;
  private final double offsetY;
  private final double fitWidth;
  private final int startTileId;

  /**
   * Constructs a new OverlayParams instance.
   *
   * @param imagePath   the classpath resource path of the overlay image
   * @param offsetX     horizontal offset in pixels from the tile center
   * @param offsetY     vertical offset in pixels from the tile center
   * @param fitWidth    the width to which the image should be scaled
   * @param startTileId the ID of the tile at which this overlay begins
   */
  public OverlayParams(
      String imagePath,
      double offsetX,
      double offsetY,
      double fitWidth,
      int startTileId) {
    this.imagePath = imagePath;
    this.offsetX = offsetX;
    this.offsetY = offsetY;
    this.fitWidth = fitWidth;
    this.startTileId = startTileId;
  }

  /**
   * @return the classpath resource path of the overlay image
   */
  public String getImagePath() {
    return imagePath;
  }

  /**
   * @return the horizontal offset in pixels from the tile center
   */
  public double getOffsetX() {
    return offsetX;
  }

  /**
   * @return the vertical offset in pixels from the tile center
   */
  public double getOffsetY() {
    return offsetY;
  }

  /**
   * @return the width to which the overlay image should be scaled
   */
  public double getFitWidth() {
    return fitWidth;
  }

  /**
   * @return the ID of the tile at which this overlay begins
   */
  public int getStartTileId() {
    return startTileId;
  }
}