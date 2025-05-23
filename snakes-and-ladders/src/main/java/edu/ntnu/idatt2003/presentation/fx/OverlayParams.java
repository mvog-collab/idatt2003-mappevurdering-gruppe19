package edu.ntnu.idatt2003.presentation.fx;

public class OverlayParams {
  private final String imagePath;
  private final double offsetX;
  private final double offsetY;
  private final double fitWidth;
  private final int startTileId;

  public OverlayParams(
      String imagePath, double offsetX, double offsetY, double fitWidth, int startTileId) {
    this.imagePath = imagePath;
    this.offsetX = offsetX;
    this.offsetY = offsetY;
    this.fitWidth = fitWidth;
    this.startTileId = startTileId;
  }

  public String getImagePath() {
    return imagePath;
  }

  public double getOffsetX() {
    return offsetX;
  }

  public double getOffsetY() {
    return offsetY;
  }

  public double getFitWidth() {
    return fitWidth;
  }

  public int getStartTileId() {
    return startTileId;
  }
}
