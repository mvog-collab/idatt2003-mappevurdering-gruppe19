package edu.ntnu.idatt2003.ui.ludo.controller;

import edu.ntnu.idatt2003.gateway.CompleteBoardGame;
import edu.ntnu.idatt2003.ui.common.controller.AbstractPageController;
import edu.ntnu.idatt2003.ui.ludo.view.LudoBoardView;
import edu.ntnu.idatt2003.ui.ludo.view.LudoPage;
import edu.ntnu.idatt2003.ui.navigation.NavigationService;
import edu.ntnu.idatt2003.ui.shared.controller.ChoosePlayerController;
import edu.ntnu.idatt2003.ui.shared.view.ChoosePlayerPage;
import edu.ntnu.idatt2003.utils.UiDialogs;

public final class LudoPageController extends AbstractPageController<LudoPage> {

  public LudoPageController(LudoPage view, CompleteBoardGame gateway) {
    super(view, gateway);
    gateway.newGame(0);
    initializeEventHandlers();
  }

  private void showPlayerDialog() {
    String[] ludoTokens = {"BLUE", "GREEN", "RED", "YELLOW"};
    ChoosePlayerPage choosePlayerPage = new ChoosePlayerPage(ludoTokens);
    choosePlayerPage.connectToModel(gateway);
    new ChoosePlayerController(choosePlayerPage, gateway);
    UiDialogs.createModalPopup("Players", choosePlayerPage.getView(), 1000, 800).showAndWait();
  }

  private void startGame() {
    LudoBoardView boardView = new LudoBoardView();
    boardView.connectToModel(gateway);
    new LudoBoardController(boardView, gateway);

    // NavigationService handles stage and scene switching
    NavigationService.getInstance().navigateToGameScene(boardView.getScene(), "Ludo Board");

    boardView.showDice(1);
  }

  @Override
  protected void initializeEventHandlers() {
    view.choosePlayerButton().setOnAction(e -> showPlayerDialog());
    view.startButton().setOnAction(e -> startGame());
    view.resetButton().setOnAction(e -> gateway.resetGame());
  }
}
