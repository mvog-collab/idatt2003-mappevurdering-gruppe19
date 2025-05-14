package edu.ntnu.idatt2003.ui.common.view;

import edu.games.engine.observer.BoardGameEvent;
import edu.ntnu.idatt2003.gateway.CompleteBoardGame;
import edu.ntnu.idatt2003.utils.ResourcePaths;
import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public abstract class AbstractMenuView implements MenuView {
  protected CompleteBoardGame gateway;
  protected Button startButton;
  protected Button choosePlayerButton;
  protected Button chooseBoardButton;
  protected Button resetButton;
  protected Label statusLabel;

  @Override
  public void connectToModel(CompleteBoardGame gateway) {
    this.gateway = gateway;
    gateway.addObserver(this);
  }

  @Override
  public void update(BoardGameEvent event) {
    Platform.runLater(
        () -> {
          switch (event.getType()) {
            case GAME_STARTED:
              handleGameStarted();
              break;

            case GAME_RESET:
              handleGameReset();
              break;

            case PLAYER_ADDED:
              handlePlayerAdded();
              break;
          }
        });
  }

  protected void handleGameStarted() {
    disableStartButton();
  }

  protected void handleGameReset() {
    disableStartButton();
  }

  protected void handlePlayerAdded() {
    // Check if we have enough players to start
    if (gateway != null) {
      boolean ready = gateway.players().size() >= 2;
      setStartButtonEnabled(ready);

      // Update status label
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
    startButton.setDisable(false);
  }

  @Override
  public void disableStartButton() {
    startButton.setDisable(true);
  }

  @Override
  public void updateStatusMessage(String message) {
    statusLabel.setText(message);
  }

  protected void setStartButtonEnabled(boolean enabled) {
    startButton.setDisable(!enabled);
  }

  public void enableChoosePlayerButton() {
    choosePlayerButton.setDisable(false);
  }

  public void disableChoosePlayerButton() {
    choosePlayerButton.setDisable(true);
  }

  protected String getStylesheet() {
    return getClass().getResource(ResourcePaths.STYLE_SHEET).toExternalForm();
  }
}
