package edu.ntnu.idatt2003.presentation.snl.controller;

import edu.ntnu.idatt2003.gateway.CompleteBoardGame;
import edu.ntnu.idatt2003.gateway.SnlGateway;
import edu.ntnu.idatt2003.presentation.common.controller.AbstractPageController;
import edu.ntnu.idatt2003.presentation.navigation.NavigationService;
import edu.ntnu.idatt2003.presentation.service.board.SnlBoardUIService;
import edu.ntnu.idatt2003.presentation.shared.controller.ChoosePlayerController;
import edu.ntnu.idatt2003.presentation.shared.view.ChoosePlayerPage;
import edu.ntnu.idatt2003.presentation.snl.view.SnlBoardSizePage;
import edu.ntnu.idatt2003.presentation.snl.view.SnlBoardView;
import edu.ntnu.idatt2003.presentation.snl.view.SnlFrontPage;
import edu.ntnu.idatt2003.utils.Dialogs;
import edu.ntnu.idatt2003.utils.UiDialogs;
import java.util.Map;

/**
 * Main controller for the Snakes and Ladders front page.
 * Handles the game setup flow including board selection, player configuration,
 * and transitioning to the actual game board. This is the central coordinator
 * for getting a SNL game ready to play.
 */
public class SnlPageController extends AbstractPageController<SnlFrontPage> {

  /**
   * Creates a new SNL page controller and sets up the initial game state.
   * Automatically creates a new game with a 90-tile board and connects
   * all the event handlers for the menu buttons.
   *
   * @param view    the front page view to control
   * @param gateway the game gateway for SNL operations
   */
  public SnlPageController(SnlFrontPage view, CompleteBoardGame gateway) {
    super(view, gateway);
    view.connectToModel(gateway);
    gateway.newGame(90);
    initializeEventHandlers();
  }

  /**
   * Shows the player selection dialog as a modal popup.
   * Players can add/remove players and configure their tokens and names.
   */
  private void showPlayerDialog() {
    ChoosePlayerPage choosePlayerPage = new ChoosePlayerPage();
    choosePlayerPage.connectToModel(gateway);
    new ChoosePlayerController(choosePlayerPage, gateway);
    UiDialogs.createModalPopup("Players", choosePlayerPage.getView(), 800, 700).showAndWait();
  }

  /**
   * Sets up the "Choose Board" button handler.
   * Opens the board size selection dialog and enables the player selection
   * button once a board is chosen.
   */
  private void setupChooseBoardButton() {
    view.getChooseBoardButton()
        .setOnAction(
            e -> {
              SnlBoardSizePage page = new SnlBoardSizePage();
              var root = page.getBoardSizeView();
              new SnlBoardSizeController(page, gateway);
              UiDialogs.createModalPopup("Choose board boardSize", root, 600, 600).showAndWait();
              view.enableChoosePlayerButton();
            });
  }

  /**
   * Sets up the "Choose Players" button to open the player selection dialog.
   */
  private void setupChoosePlayerButton() {
    view.getChoosePlayerButton().setOnAction(e -> showPlayerDialog());
  }

  /**
   * Sets up the "Start Game" button handler.
   * Validates that the game setup is complete (at least 2 players),
   * creates the game board with snakes and ladders, and navigates
   * to the actual game scene.
   */
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

              NavigationService.getInstance()
                  .navigateToGameScene(boardView.getScene(), "Snakes & Ladders Board");
            });
  }

  /**
   * Sets up the "Reset Game" button to start over.
   * Clears the current game state and requires the user to go through
   * the setup process again.
   */
  private void setupResetGameButton() {
    view.getResetButton()
        .setOnAction(
            e -> {
              gateway.newGame(gateway.boardSize());
              view.disableChoosePlayerButton();
              showResetConfirmation();
            });
  }

  /**
   * Shows a confirmation message when the game has been reset.
   */
  private void showResetConfirmation() {
    Dialogs.info(
        "Game reset", "The game has been reset successfully. Please choose a board to continue");
  }

  /**
   * Sets up all the button event handlers for the SNL front page.
   * Called during controller initialization to wire up all the UI interactions.
   */
  @Override
  protected void initializeEventHandlers() {
    setupChooseBoardButton();
    setupChoosePlayerButton();
    setupStartButton();
    setupResetGameButton();
  }
}