package edu.ntnu.idatt2003.presentation.ludo.controller;

import edu.ntnu.idatt2003.gateway.CompleteBoardGame;
import edu.ntnu.idatt2003.presentation.common.controller.AbstractPageController;
import edu.ntnu.idatt2003.presentation.ludo.view.LudoBoardView;
import edu.ntnu.idatt2003.presentation.ludo.view.LudoPage;
import edu.ntnu.idatt2003.presentation.navigation.NavigationService;
import edu.ntnu.idatt2003.presentation.shared.controller.ChoosePlayerController;
import edu.ntnu.idatt2003.presentation.shared.view.ChoosePlayerPage;
import edu.ntnu.idatt2003.utils.Dialogs;
import edu.ntnu.idatt2003.utils.Errors;
import edu.ntnu.idatt2003.utils.UiDialogs;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Controller for the Ludo main page. Manages the flow from player selection to
 * game start
 * and supports resetting the game.
 */
public final class LudoPageController extends AbstractPageController<LudoPage> {

  private static final Logger LOG = Logger.getLogger(LudoPageController.class.getName());

  /**
   * Constructs the controller and initializes the Ludo game via the gateway.
   *
   * @param view    the Ludo page view
   * @param gateway the gateway providing game setup and control
   */
  public LudoPageController(LudoPage view, CompleteBoardGame gateway) {
    super(view, gateway);
    LOG.info("LudoPageController initialized.");
    try {
      gateway.newGame(0); // Ludo board size is fixed
      initializeEventHandlers();
      LOG.info("Ludo game initialized in gateway.");
    } catch (Exception e) {
      LOG.log(Level.SEVERE, "Failed to initialize Ludo page or game", e);
      Errors.handle("Could not set up the Ludo game page.", e);
    }
  }

  /**
   * Opens a modal dialog for selecting players and registers them with the
   * gateway.
   */
  private void showPlayerDialog() {
    LOG.info("Showing player dialog for Ludo.");
    try {
      String[] ludoTokens = { "BLUE", "GREEN", "RED", "YELLOW" };
      ChoosePlayerPage choosePlayerPage = new ChoosePlayerPage(ludoTokens);
      choosePlayerPage.connectToModel(gateway);
      new ChoosePlayerController(choosePlayerPage, gateway);
      UiDialogs.createModalPopup("Players", choosePlayerPage.getView(), 1000, 800).showAndWait();
      LOG.info("Player dialog closed.");
    } catch (Exception e) {
      LOG.log(Level.SEVERE, "Error showing Ludo player dialog", e);
      Errors.handle("Could not open the player selection dialog.", e);
    }
  }

  /**
   * Starts the Ludo game by navigating to the board view if enough players are
   * present.
   */
  private void startGame() {
    LOG.info("Starting Ludo game.");
    try {
      if (gateway.players().size() < 2) {
        LOG.warning("Attempted to start Ludo game with insufficient players: " + gateway.players().size());
        Dialogs.warn("Cannot Start Game", "Ludo requires at least 2 players.");
        return;
      }
      LudoBoardView boardView = new LudoBoardView();
      boardView.connectToModel(gateway);
      new LudoBoardController(boardView, gateway);

      NavigationService.getInstance().navigateToGameScene(boardView.getScene(), "Ludo Board");
      boardView.showDice(1);
      LOG.info("Ludo game started and navigated to board scene.");
    } catch (Exception e) {
      LOG.log(Level.SEVERE, "Error starting Ludo game", e);
      Errors.handle("An error occurred while trying to start the Ludo game.", e);
    }
  }

  /**
   * Resets the game state to allow a new session.
   */
  private void resetGame() {
    try {
      gateway.newGame(0);
      LOG.info("Ludo game reset in gateway.");
    } catch (Exception ex) {
      LOG.log(Level.SEVERE, "Error resetting Ludo game.", ex);
      Errors.handle("An error occurred while resetting the game.", ex);
    }
  }

  /**
   * Registers UI event handlers for player selection, start, and reset actions.
   */
  @Override
  protected void initializeEventHandlers() {
    LOG.fine("Initializing LudoPage event handlers.");
    view.choosePlayerButton().setOnAction(e -> {
      LOG.fine("Choose player button clicked.");
      showPlayerDialog();
    });
    view.startButton().setOnAction(e -> {
      LOG.fine("Start game button clicked.");
      startGame();
    });
    view.resetButton().setOnAction(e -> {
      LOG.info("Reset game button clicked.");
      resetGame();
    });
  }
}