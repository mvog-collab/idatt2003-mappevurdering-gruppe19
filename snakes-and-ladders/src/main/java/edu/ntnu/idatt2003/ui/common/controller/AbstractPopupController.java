package edu.ntnu.idatt2003.ui.common.controller;

import edu.ntnu.idatt2003.gateway.CompleteBoardGame;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public abstract class AbstractPopupController<V> extends AbstractController {
  protected final V view;

  public AbstractPopupController(V view, CompleteBoardGame gateway) {
    super(gateway);
    this.view = view;

    // Connect view to model if it's an observer
    if (view instanceof edu.games.engine.observer.BoardGameObserver observer) {
      gateway.addObserver(observer);
    }

    // Initialize the view
    initializeEventHandlers();
  }

  protected abstract void initializeEventHandlers();

  public abstract void confirm();

  public abstract void cancel();

  protected void close(Button button) {
    ((Stage) button.getScene().getWindow()).close();
  }
}
