package edu.games.engine.impl.overlay;

import edu.ntnu.idatt2003.presentation.fx.OverlayParams;
import java.util.List;

/**
 * Provides visual overlays for a game board based on its size.
 * Overlays define images to be placed over specific tiles.
 */
public interface OverlayProvider {

  /**
   * Returns a list of overlay parameters for a given board size.
   *
   * @param size the size of the board
   * @return a list of overlay parameters (can be empty but never null)
   */
  List<OverlayParams> overlaysForBoard(int size);
}
