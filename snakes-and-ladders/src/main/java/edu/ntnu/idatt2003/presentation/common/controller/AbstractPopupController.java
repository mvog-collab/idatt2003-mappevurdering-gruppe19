package edu.ntnu.idatt2003.presentation.common.controller;

import edu.ntnu.idatt2003.gateway.CompleteBoardGame;
import javafx.scene.control.Button;
import javafx.stage.Stage;

/**
 * Base class for controllers of modal popups.
 * <p>
 * Automatically registers the view as an observer if it implements
 * {@code BoardGameObserver}, and calls {@link #initializeEventHandlers()}
 * immediately after construction.
 * </p>
 *
 * @param <V> the type of the view managed by this controller
 */
public abstract class AbstractPopupController<V> extends AbstractController {

  /** The view instance controlled by this controller. */
  protected final V view;

  /**
   * Constructs a popup controller, registers the view as observer if applicable,
   * and initializes its event handlers.
   *
   * @param view    the UI view for this popup
   * @param gateway the shared {@link CompleteBoardGame} instance
   */
  protected AbstractPopupController(V view, CompleteBoardGame gateway) {
    super(gateway);
    this.view = view;

    // Register as observer if view implements BoardGameObserver
    if (view instanceof edu.games.engine.observer.BoardGameObserver observer) {
      gateway.addObserver(observer);
    }

    // Hook up view event handlers
    initializeEventHandlers();
  }

  /**
   * Initialize UI event handlers for this popup.
   * <p>
   * Subclasses must implement to bind buttons and other controls.
   * </p>
   */
  protected abstract void initializeEventHandlers();

  /** Called when the user confirms the popup. */
  public abstract void confirm();

  /** Called when the user cancels the popup. */
  public abstract void cancel();

  /**
   * Closes the window that contains the given button.
   *
   * @param button any button within the popup window
   */
  protected void close(Button button) {
    ((Stage) button.getScene().getWindow()).close();
  }
}