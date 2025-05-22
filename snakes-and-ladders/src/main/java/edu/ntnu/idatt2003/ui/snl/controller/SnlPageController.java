package edu.ntnu.idatt2003.ui.snl.controller;

import edu.ntnu.idatt2003.gateway.CompleteBoardGame;
import edu.ntnu.idatt2003.gateway.SnlGateway;
import edu.ntnu.idatt2003.ui.common.controller.AbstractPageController;
import edu.ntnu.idatt2003.ui.navigation.NavigationService;
import edu.ntnu.idatt2003.ui.service.board.SnlBoardUIService;
import edu.ntnu.idatt2003.ui.shared.controller.ChoosePlayerController;
import edu.ntnu.idatt2003.ui.shared.view.ChoosePlayerPage;
import edu.ntnu.idatt2003.ui.snl.view.SnlBoardSizePage;
import edu.ntnu.idatt2003.ui.snl.view.SnlBoardView;
import edu.ntnu.idatt2003.ui.snl.view.SnlFrontPage;
import edu.ntnu.idatt2003.utils.Dialogs;
import edu.ntnu.idatt2003.utils.UiDialogs;
import java.util.Map;

// import javafx.stage.Stage; // No longer needed for scene switching here

public class SnlPageController extends AbstractPageController<SnlFrontPage> {

  public SnlPageController(SnlFrontPage view, CompleteBoardGame gateway) {
    super(view, gateway);
    view.connectToModel(gateway);
    gateway.newGame(90);
    initializeEventHandlers();
  }

  private void showPlayerDialog() {
    ChoosePlayerPage choosePlayerPage = new ChoosePlayerPage();
    choosePlayerPage.connectToModel(gateway);
    new ChoosePlayerController(choosePlayerPage, gateway);
    UiDialogs.createModalPopup("Players", choosePlayerPage.getView(), 800, 700).showAndWait();
  }

  private void setupChooseBoardButton() {
    view.getChooseBoardButton()
        .setOnAction(
            e -> {
              SnlBoardSizePage page = new SnlBoardSizePage();
              var root = page.getBoardSizeView();
              new SnlBoardSizeController(page, (CompleteBoardGame) gateway);
              UiDialogs.createModalPopup("Choose board boardSize", root, 600, 500).showAndWait();
              view.enableChoosePlayerButton();
            });
  }

  private void setupChoosePlayerButton() {
    view.getChoosePlayerButton().setOnAction(e -> showPlayerDialog());
  }

  private void setupStartButton() {
    view.getStartButton()
        .setOnAction(
            e -> {
              if (gateway.players().size() < 2) {
                view.alertUserAboutUnfinishedSetup();
                return;
              }

              int boardSize = gateway.boardSize();
              Map<Integer, Integer> snakes = Map.of();
              Map<Integer, Integer> ladders = Map.of();

              if (gateway instanceof SnlGateway snlGateway) {
                snakes = snlGateway.getSnakes();
                ladders = snlGateway.getLadders();
              }

              SnlBoardView boardView = new SnlBoardView(boardSize);
              boardView.connectToModel(gateway);

              if (boardView.getBoardUIService() instanceof SnlBoardUIService snlBoardUIService) {
                snlBoardUIService.applySpecialTileStyling(
                    snakes, ladders, boardView.getOverlayPane());
              }
              new SnlBoardController(boardView, gateway);
              boardView.setPlayers(gateway.players(), gateway.boardOverlays());

              // NavigationService handles stage and scene switching
              NavigationService.getInstance()
                  .navigateToGameScene(boardView.getScene(), "Snakes & Ladders Board");
            });
  }

  private void setupResetGameButton() {
    view.getResetButton()
        .setOnAction(
            e -> {
              gateway.newGame(gateway.boardSize());
              view.disableChoosePlayerButton();
              showResetConfirmation();
            });
  }

  private void showResetConfirmation() {
    Dialogs.info(
        "Game reset", "The game has been reset successfully. Please choose a board to continue");
  }

  @Override
  protected void initializeEventHandlers() {
    setupChooseBoardButton();
    setupChoosePlayerButton();
    setupStartButton();
    setupResetGameButton();
  }
}
