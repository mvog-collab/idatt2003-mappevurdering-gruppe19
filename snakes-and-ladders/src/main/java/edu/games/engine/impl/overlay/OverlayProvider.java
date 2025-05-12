package edu.games.engine.impl.overlay;

import edu.ntnu.idatt2003.ui.fx.OverlayParams;
import java.util.List;

public interface OverlayProvider {
  List<OverlayParams> overlaysForBoard(int size);
}
