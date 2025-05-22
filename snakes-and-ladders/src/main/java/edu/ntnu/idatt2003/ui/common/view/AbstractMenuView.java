package edu.ntnu.idatt2003.ui.common.view;

import edu.games.engine.observer.BoardGameEvent;
import edu.ntnu.idatt2003.gateway.CompleteBoardGame;
import edu.ntnu.idatt2003.utils.ResourcePaths;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public abstract class AbstractMenuView extends AbstractView implements MenuView {
  private static final Logger LOG = Logger.getLogger(AbstractMenuView.class.getName());
  protected Button startButton;
  protected Button choosePlayerButton;
  protected Button resetButton;
  protected Label statusLabel;

  @Override
  public void connectToModel(CompleteBoardGame gateway) {
    this.gateway = gateway;
    gateway.addObserver(this);
  }

  @Override
  public void handleEvent(BoardGameEvent event) {
    Platform.runLater(
        () -> {
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
              LOG.finest("Received unhandled event type in menu view: " + event.getTypeOfEvent());
              break;
          }
        });
  }

  protected void handleGameStarted() {
    disableStartButton();
  }

  protected void handleGameReset() {
    disableStartButton();
    if (gateway != null && statusLabel != null) {
      updateStatusMessage(
          "Game reset. Need at least 2 players to start (currently have " + gateway.players().size() + ")");
    }
  }

  protected void handlePlayerAdded() {
    if (gateway != null && statusLabel != null) {
      boolean ready = gateway.players().size() >= 2;
      setStartButtonEnabled(ready);
      if (ready) {
        updateStatusMessage("Ready to start game with " + gateway.players().size() + " players");
      } else {
        updateStatusMessage(
            "Need at least 2 players to start (currently have " + gateway.players().size() + ")");
      }
    }
  }

  @Override
  public Button getStartButton() {
    return startButton;
  }

  @Override
  public Button getChoosePlayerButton() {
    return choosePlayerButton;
  }

  @Override
  public Button getResetButton() {
    return resetButton;
  }

  @Override
  public void enableStartButton() {
    if (startButton != null)
      startButton.setDisable(false);
  }

  @Override
  public void disableStartButton() {
    if (startButton != null)
      startButton.setDisable(true);
  }

  @Override
  public void updateStatusMessage(String message) {
    if (statusLabel != null)
      statusLabel.setText(message);
  }

  protected void setStartButtonEnabled(boolean enabled) {
    if (startButton != null)
      startButton.setDisable(!enabled);
  }

  public void enableChoosePlayerButton() {
    if (choosePlayerButton != null)
      choosePlayerButton.setDisable(false);
  }

  public void disableChoosePlayerButton() {
    if (choosePlayerButton != null)
      choosePlayerButton.setDisable(true);
  }

  protected String getStylesheet() {
    java.net.URL cssUrl = getClass().getResource(ResourcePaths.STYLE_SHEET);
    if (cssUrl == null) {
      LOG.warning("Stylesheet not found: " + ResourcePaths.STYLE_SHEET);
      return null;
    }
    return cssUrl.toExternalForm();
  }
}