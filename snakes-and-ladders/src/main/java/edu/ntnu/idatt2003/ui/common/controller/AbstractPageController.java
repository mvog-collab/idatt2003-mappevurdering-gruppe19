package edu.ntnu.idatt2003.ui.common.controller;

import edu.ntnu.idatt2003.gateway.CompleteBoardGame;

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

  protected abstract void initializeEventHandlers();
}
