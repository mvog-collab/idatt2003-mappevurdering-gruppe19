package edu.ntnu.idatt2003.ui.snl.controller;

import edu.ntnu.idatt2003.gateway.CompleteBoardGame;
import edu.ntnu.idatt2003.ui.common.controller.AbstractPageController;
import edu.ntnu.idatt2003.ui.shared.controller.ChoosePlayerController;
import edu.ntnu.idatt2003.ui.shared.view.ChoosePlayerPage;
import edu.ntnu.idatt2003.ui.snl.view.BoardSizePage;
import edu.ntnu.idatt2003.ui.snl.view.BoardView;
import edu.ntnu.idatt2003.ui.snl.view.SnlPage;
import edu.ntnu.idatt2003.utils.Dialogs;
import edu.ntnu.idatt2003.utils.UiDialogs;
import javafx.stage.Stage;

public class SnlPageController extends AbstractPageController<SnlPage> {

  public SnlPageController(SnlPage view, CompleteBoardGame gateway) {
    super(view, gateway);

    // Connect view to observe the model
    view.connectToModel(gateway);

    gateway.newGame(90);
    initializeEventHandlers();
  }

  private void showPlayerDialog() {
    ChoosePlayerPage choosePlayerPage = new ChoosePlayerPage();
    // Connect player page to observe model
    choosePlayerPage.connectToModel(gateway);

    new ChoosePlayerController(choosePlayerPage, gateway);
    UiDialogs.createModalPopup("Players", choosePlayerPage.getView(), 800, 700).showAndWait();

    // No need to manually refresh - observer pattern will handle UI updates
  }

  private void setupChooseBoardButton() {
    view.getChooseBoardButton()
        .setOnAction(
            e -> {
              BoardSizePage page = new BoardSizePage();
              var root = page.getBoardSizeView();
              new BoardSizeController(page, (CompleteBoardGame) gateway);
              UiDialogs.createModalPopup("Choose board size", root, 600, 500).showAndWait();
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

              BoardView board = new BoardView(boardSize);

              // Connect view to observe the model
              board.connectToModel(gateway);

              BoardController boardController = new BoardController(board, gateway);

              // Initial UI setup
              board.setPlayers(gateway.players(), gateway.boardOverlays());

              Stage stage = (Stage) view.getStartButton().getScene().getWindow();
              stage.setScene(board.getScene());
            });
  }

  private void setupResetGameButton() {
    view.getResetButton()
        .setOnAction(
            e -> {
              gateway.newGame(gateway.boardSize());
              view.disableChoosePlayerButton();
              // No need to manually refresh UI - observer pattern will handle it
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
