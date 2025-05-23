package edu.ntnu.idatt2003.presentation.common.view;

import edu.games.engine.observer.BoardGameEvent;
import edu.ntnu.idatt2003.gateway.CompleteBoardGame;
import edu.ntnu.idatt2003.utils.ResourcePaths;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

/**
 * Base implementation for menu screens in the application.
 * <p>
 * Registers for game events via {@link CompleteBoardGame}, updates UI controls
 * in response to game start, reset, and player-add events, and provides
 * methods to enable/disable buttons and update the status label.
 * </p>
 */
public abstract class AbstractMenuView extends AbstractView implements MenuView {
  private static final Logger LOG = Logger.getLogger(AbstractMenuView.class.getName());
  protected Button startButton;
  protected Button choosePlayerButton;
  protected Button resetButton;
  protected Label statusLabel;

  /**
   * Connects this menu view to the game gateway and registers for events.
   *
   * @param gateway the shared {@link CompleteBoardGame} instance
   */
  @Override
  public void connectToModel(CompleteBoardGame gateway) {
    this.gateway = gateway;
    gateway.addObserver(this);
  }

  /**
   * Handles incoming {@link BoardGameEvent}s on the JavaFX thread.
   *
   * @param event the game event to handle
   */
  @Override
  public void handleEvent(BoardGameEvent event) {
    Platform.runLater(() -> {
      switch (event.getTypeOfEvent()) {
        case GAME_STARTED:
          LOG.fine("Game started event received in menu view.");
          handleGameStarted();
          break;
        case GAME_RESET:
          LOG.fine("Game reset event received in menu view.");
          handleGameReset();
          break;
        case PLAYER_ADDED:
          LOG.fine("Player added event received in menu view.");
          handlePlayerAdded();
          break;
        default:
          LOG.finest("Unhandled event type in menu view: " + event.getTypeOfEvent());
      }
    });
  }

  /** Disables the start button when the game has started. */
  protected void handleGameStarted() {
    disableStartButton();
  }

  /**
   * Disables the start button and updates the status message
   * after a game reset.
   */
  protected void handleGameReset() {
    disableStartButton();
    if (gateway != null && statusLabel != null) {
      updateStatusMessage(
          "Game reset. Need at least 2 players to start (currently have " +
              gateway.players().size() + ")");
    }
  }

  /**
   * Enables or disables the start button based on player count,
   * and updates the status message accordingly.
   */
  protected void handlePlayerAdded() {
    if (gateway != null && statusLabel != null) {
      boolean ready = gateway.players().size() >= 2;
      setStartButtonEnabled(ready);
      if (ready) {
        updateStatusMessage(
            "Ready to start game with " + gateway.players().size() + " players");
      } else {
        updateStatusMessage(
            "Need at least 2 players to start (currently have " +
                gateway.players().size() + ")");
      }
    }
  }

  /** {@inheritDoc} */
  @Override
  public Button getStartButton() {
    return startButton;
  }

  /** {@inheritDoc} */
  @Override
  public Button getChoosePlayerButton() {
    return choosePlayerButton;
  }

  /** {@inheritDoc} */
  @Override
  public Button getResetButton() {
    return resetButton;
  }

  /** {@inheritDoc} */
  @Override
  public void enableStartButton() {
    if (startButton != null) {
      startButton.setDisable(false);
    }
  }

  /** {@inheritDoc} */
  @Override
  public void disableStartButton() {
    if (startButton != null) {
      startButton.setDisable(true);
    }
  }

  /** {@inheritDoc} */
  @Override
  public void updateStatusMessage(String message) {
    if (statusLabel != null) {
      statusLabel.setText(message);
    }
  }

  /**
   * Enables or disables the start button based on the given flag.
   *
   * @param enabled true to enable, false to disable
   */
  protected void setStartButtonEnabled(boolean enabled) {
    if (startButton != null) {
      startButton.setDisable(!enabled);
    }
  }

  /** Enables the "Choose Player" button if present. */
  public void enableChoosePlayerButton() {
    if (choosePlayerButton != null) {
      choosePlayerButton.setDisable(false);
    }
  }

  /** Disables the "Choose Player" button if present. */
  public void disableChoosePlayerButton() {
    if (choosePlayerButton != null) {
      choosePlayerButton.setDisable(true);
    }
  }

  /**
   * Retrieves the external form of the application stylesheet.
   *
   * @return the stylesheet URL string, or null if not found
   */
  protected String getStylesheet() {
    java.net.URL cssUrl = getClass().getResource(ResourcePaths.STYLE_SHEET);
    if (cssUrl == null) {
      LOG.warning("Stylesheet not found: " + ResourcePaths.STYLE_SHEET);
      return null;
    }
    return cssUrl.toExternalForm();
  }
}