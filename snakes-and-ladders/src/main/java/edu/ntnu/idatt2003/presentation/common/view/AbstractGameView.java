package edu.ntnu.idatt2003.presentation.common.view;

import edu.games.engine.observer.BoardGameEvent;
import javafx.application.Platform;
import javafx.scene.control.Button;

public abstract class AbstractGameView extends AbstractView implements GameView {
  protected Button rollButton;
  protected Button playAgainButton;

  @Override
  protected void handleEvent(BoardGameEvent event) {
    switch (event.getTypeOfEvent()) {
      case DICE_ROLLED:
        handleDiceRolled(event.getData());
        break;
      case PLAYER_MOVED:
        handlePlayerMoved(event.getData());
        break;
      case WINNER_DECLARED:
        handleWinnerDeclared(event.getData());
        break;
      case GAME_RESET:
        handleGameReset();
        break;
      case TURN_CHANGED:
        handleTurnChanged(event.getData());
        break;
    }
  }

  // Default implementations
  protected void handleDiceRolled(Object data) {}

  protected void handlePlayerMoved(Object data) {}

  protected void handleWinnerDeclared(Object data) {}

  protected void handleGameReset() {}

  protected void handleTurnChanged(Object data) {}

  @Override
  public Button getRollButton() {
    return rollButton;
  }

  @Override
  public Button getPlayAgainButton() {
    return playAgainButton;
  }

  @Override
  public void disableRollButton() {
    Platform.runLater(() -> rollButton.setDisable(true));
  }

  @Override
  public void enableRollButton() {
    Platform.runLater(() -> rollButton.setDisable(false));
  }
}
