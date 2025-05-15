package edu.ntnu.idatt2003.ui.common.view;

import edu.games.engine.observer.BoardGameEvent;
import edu.games.engine.observer.BoardGameObserver;
import edu.ntnu.idatt2003.gateway.CompleteBoardGame;
import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

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

  // Common UI utilities

  protected Button createHowToPlayButton() {
    Button howToPlayButton = new Button();
    howToPlayButton.setGraphic(new ImageView(new Image("images/settings.png")));
    howToPlayButton.getStyleClass().add("icon-button");
    return howToPlayButton;
  }
}
