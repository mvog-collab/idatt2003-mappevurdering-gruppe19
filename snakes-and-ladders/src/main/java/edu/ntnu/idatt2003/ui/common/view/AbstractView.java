package edu.ntnu.idatt2003.ui.common.view;

import edu.games.engine.observer.BoardGameEvent;
import edu.games.engine.observer.BoardGameObserver;
import edu.ntnu.idatt2003.gateway.CompleteBoardGame;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;

public abstract class AbstractView implements BoardGameObserver {
  protected CompleteBoardGame gateway;

  public void connectToModel(CompleteBoardGame gateway) {
    this.gateway = gateway;
    gateway.addObserver(this);
  }

  @Override
  public void update(BoardGameEvent event) {
    Platform.runLater(
        () -> {
          handleEvent(event);
        });
  }

  protected abstract void handleEvent(BoardGameEvent event);

  protected Button createHowToPlayButton(String title, String instructions) {
    Button btn = new Button("How to play");
    btn.getStyleClass().add("icon-button"); // evt. egen css-klasse
    btn.setOnAction(
        e -> {
          Alert alert = new Alert(Alert.AlertType.INFORMATION);
          alert.setTitle(title);
          alert.setHeaderText(null);
          alert.setContentText(instructions);
          alert.showAndWait();
        });
    return btn;
  }
}
