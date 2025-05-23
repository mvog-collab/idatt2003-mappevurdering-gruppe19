package edu.ntnu.idatt2003.presentation.snl.controller;

import edu.ntnu.idatt2003.gateway.CompleteBoardGame;
import edu.ntnu.idatt2003.presentation.common.controller.AbstractPopupController;
import edu.ntnu.idatt2003.presentation.snl.view.SnlBoardSizePage;

/**
 * Controller for the board size selection popup in Snakes and Ladders.
 * Handles user interactions for choosing between different board sizes
 * (64, 90, or 120 tiles) and manages the popup dialog lifecycle.
 */
public class SnlBoardSizeController extends AbstractPopupController<SnlBoardSizePage> {

  /**
   * Creates a new board size selection controller.
   * Sets up event handlers for all the board size buttons and dialog controls.
   *
   * @param view    the board size selection view
   * @param gateway the game gateway to update with the selected board size
   */
  public SnlBoardSizeController(SnlBoardSizePage view, CompleteBoardGame gateway) {
    super(view, gateway);
  }

  /**
   * Sets up all the event handlers for the board size selection interface.
   * Each board size button creates a new game with the selected size,
   * and the confirm/cancel buttons handle closing the dialog.
   */
  @Override
  protected void initializeEventHandlers() {
    view.getSixtyFourTiles()
        .setOnAction(
            e -> {
              gateway.newGame(64);
            });
    view.getNinetyTiles()
        .setOnAction(
            e -> {
              gateway.newGame(90);
            });
    view.getOneTwentyTiles()
        .setOnAction(
            e -> {
              gateway.newGame(120);
            });
    view.getContinueButton().setOnAction(e -> confirm());
    view.getCancelButton().setOnAction(e -> cancel());
  }

  /**
   * Confirms the board size selection and closes the popup.
   * The selected board size is already applied when the user clicked
   * one of the size buttons.
   */
  @Override
  public void confirm() {
    close(view.getContinueButton());
  }

  /**
   * Cancels the board size selection and closes the popup.
   * The user can re-open the dialog to make a different selection.
   */
  @Override
  public void cancel() {
    close(view.getCancelButton());
  }
}