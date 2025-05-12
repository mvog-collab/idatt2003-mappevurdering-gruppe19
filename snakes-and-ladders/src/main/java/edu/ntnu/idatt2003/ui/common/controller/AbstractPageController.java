package edu.ntnu.idatt2003.ui.common.controller;

import edu.ntnu.idatt2003.gateway.CompleteBoardGame;
import edu.ntnu.idatt2003.utils.ResourcePaths;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

public abstract class AbstractPageController<V> extends AbstractController {
  protected final V view;

  public AbstractPageController(V view, CompleteBoardGame gateway) {
    super(gateway);
    this.view = view;

    // Connect view to model if it's an observer
    if (view instanceof edu.games.engine.observer.BoardGameObserver observer) {
      gateway.addObserver(observer);
    }
  }

  protected Stage createModalPopup(String title, Parent root, int width, int height) {
    Stage popupStage = new Stage();
    popupStage.initModality(Modality.APPLICATION_MODAL);
    popupStage.setTitle(title);

    Scene scene = new Scene(root, width, height);
    scene.getStylesheets().add(getClass().getResource(ResourcePaths.STYLE_SHEET).toExternalForm());

    scene.getRoot().requestFocus();
    popupStage.setScene(scene);
    return popupStage;
  }

  protected abstract void initializeEventHandlers();
}
