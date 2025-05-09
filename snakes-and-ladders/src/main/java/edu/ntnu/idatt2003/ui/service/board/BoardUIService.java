package edu.ntnu.idatt2003.ui.service.board;

import java.util.List;
import java.util.Map;

import edu.ntnu.idatt2003.ui.fx.OverlayParams;
import javafx.geometry.Point2D;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

public interface BoardUIService {
    StackPane createBoardPane(int size);
    StackPane createTile(int id, boolean isWhite);
    void addOverlays(Pane overlayPane, List<OverlayParams> overlays);
    void placeTokenOnTile(Pane tokenPane, ImageView token, int tileId);
    void placeTokenAtStart(Pane tokenPane, ImageView token);
    Map<Integer, Point2D> getTileCoordinates();
}