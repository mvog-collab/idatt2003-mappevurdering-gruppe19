package edu.games.engine.impl.overlay;

import java.util.List;

import edu.ntnu.idatt2003.ui.fx.OverlayParams;

public interface OverlayProvider {
    List<OverlayParams> overlaysForBoard(int size);
}