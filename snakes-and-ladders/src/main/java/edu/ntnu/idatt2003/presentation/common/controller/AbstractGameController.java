package edu.ntnu.idatt2003.presentation.common.controller;

import edu.ntnu.idatt2003.gateway.CompleteBoardGame;
import edu.ntnu.idatt2003.presentation.common.view.GameView;

/**
 * Base controller for game screens.
 * <p>
 * Connects the view to the model, initializes common button handlers
 * for rolling dice and resetting the game, and delegates game-specific
 * roll logic to subclasses.
 * </p>
 *
 * @param <V> view type extending {@link GameView}
 */
public abstract class AbstractGameController<V extends GameView> extends AbstractController {

  /** The game view managed by this controller. */
  protected final V view;

  /**
   * Constructs the game controller, connects view to the model, and
   * sets up common event handlers.
   *
   * @param view    the {@link GameView} instance
   * @param gateway the shared {@link CompleteBoardGame} instance
   */
  protected AbstractGameController(V view, CompleteBoardGame gateway) {
    super(gateway);
    this.view = view;

    // Connect view as observer and model client
    view.connectToModel(gateway);
    initializeEvents();
  }

  /** Sets up handlers for roll and play-again buttons. */
  protected void initializeEvents() {
    view.getRollButton().setOnAction(e -> handleRollDice());
    view.getPlayAgainButton().setOnAction(e -> handleResetGame());
  }

  /** Disables roll button and calls subclass logic. */
  protected void handleRollDice() {
    view.disableRollButton();
    onRollDice();
  }

  /** Invokes a game reset on the gateway. */
  protected void handleResetGame() {
    gateway.resetGame();
  }

  /**
   * Subclass hook to perform game-specific dice roll logic.
   * <p>
   * Called after the roll button is clicked and disabled.
   * </p>
   */
  protected abstract void onRollDice();
}