package edu.ntnu.idatt2003.presentation.common.controller;

import edu.ntnu.idatt2003.gateway.CompleteBoardGame;

/**
 * Base class for controllers of full-page views.
 * <p>
 * Automatically registers the view as an observer if it implements
 * {@code BoardGameObserver}. Subclasses should set up event handlers
 * in {@link #initializeEventHandlers()}.
 * </p>
 *
 * @param <V> the type of the view managed by this controller
 */
public abstract class AbstractPageController<V> extends AbstractController {

  /** The view instance controlled by this controller. */
  protected final V view;

  /**
   * Constructs a page controller and registers the view as observer if
   * applicable.
   *
   * @param view    the UI view for this page
   * @param gateway the shared {@link CompleteBoardGame} instance
   */
  protected AbstractPageController(V view, CompleteBoardGame gateway) {
    super(gateway);
    this.view = view;

    // Register as observer if view implements BoardGameObserver
    if (view instanceof edu.games.engine.observer.BoardGameObserver observer) {
      gateway.addObserver(observer);
    }
  }

  /**
   * Initialize UI event handlers for this page.
   * <p>
   * Subclasses must implement to bind buttons and other controls.
   * </p>
   */
  protected abstract void initializeEventHandlers();
}