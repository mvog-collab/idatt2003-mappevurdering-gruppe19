package edu.ntnu.idatt2003.presentation.common.view;

import edu.games.engine.observer.BoardGameObserver;
import edu.ntnu.idatt2003.gateway.CompleteBoardGame;
import javafx.scene.Scene;
import javafx.scene.control.Button;

public interface MenuView extends BoardGameObserver {
  Scene getScene();

  void connectToModel(CompleteBoardGame gateway);

  Button getStartButton();

  Button getChoosePlayerButton();

  Button getResetButton();

  void enableStartButton();

  void disableStartButton();

  void updateStatusMessage(String message);
}
