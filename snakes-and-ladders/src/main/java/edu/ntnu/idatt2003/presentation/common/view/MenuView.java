package edu.ntnu.idatt2003.presentation.common.view;

import edu.games.engine.observer.BoardGameObserver;
import edu.ntnu.idatt2003.gateway.CompleteBoardGame;
import javafx.scene.Scene;
import javafx.scene.control.Button;

/**
 * Interface defining the contract for menu views.
 * <p>
 * Extends {@link BoardGameObserver} to react to game events and provides
 * methods to manage navigation and status controls.
 * </p>
 */
public interface MenuView extends BoardGameObserver {

  /** @return the JavaFX {@link Scene} for this menu */
  Scene getScene();

  /**
   * Connects this menu to the game model.
   *
   * @param gateway the {@link CompleteBoardGame} instance
   */
  void connectToModel(CompleteBoardGame gateway);

  /** @return the button used to start the game */
  Button getStartButton();

  /** @return the button used to add or choose players */
  Button getChoosePlayerButton();

  /** @return the button used to reset the game */
  Button getResetButton();

  /** Enables the start button when ready */
  void enableStartButton();

  /** Disables the start button when not ready */
  void disableStartButton();

  /**
   * Updates the status label with a new message.
   *
   * @param message the status text to display
   */
  void updateStatusMessage(String message);
}