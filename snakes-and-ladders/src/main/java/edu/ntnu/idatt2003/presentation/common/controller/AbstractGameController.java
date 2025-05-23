package edu.ntnu.idatt2003.presentation.common.controller;

import edu.ntnu.idatt2003.gateway.CompleteBoardGame;
import edu.ntnu.idatt2003.presentation.common.view.GameView;

public abstract class AbstractGameController<V extends GameView> extends AbstractController {
  protected final V view;

  public AbstractGameController(V view, CompleteBoardGame gateway) {
    super(gateway);
    this.view = view;

    // Common initialization
    view.connectToModel(gateway);
    initializeEvents();
  }

  protected void initializeEvents() {
    view.getRollButton().setOnAction(e -> handleRollDice());
    view.getPlayAgainButton().setOnAction(e -> handleResetGame());
  }

  protected void handleRollDice() {
    view.disableRollButton();
    onRollDice();
  }

  protected void handleResetGame() {
    gateway.resetGame();
  }

  // Game-specific logic to be implemented by subclasses
  protected abstract void onRollDice();
}
