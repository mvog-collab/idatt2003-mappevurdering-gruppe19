package edu.ntnu.idatt2003.ui.ludo.controller;

import edu.ntnu.idatt2003.gateway.CompleteBoardGame;
import edu.ntnu.idatt2003.ui.common.controller.AbstractPageController;
import edu.ntnu.idatt2003.ui.ludo.view.LudoBoardView;
import edu.ntnu.idatt2003.ui.ludo.view.LudoPage;
import edu.ntnu.idatt2003.ui.navigation.NavigationService;
import edu.ntnu.idatt2003.ui.shared.controller.ChoosePlayerController;
import edu.ntnu.idatt2003.ui.shared.view.ChoosePlayerPage;
import edu.ntnu.idatt2003.utils.Dialogs;
import edu.ntnu.idatt2003.utils.Errors;
import edu.ntnu.idatt2003.utils.UiDialogs;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class LudoPageController extends AbstractPageController<LudoPage> {
  private static final Logger LOG = Logger.getLogger(LudoPageController.class.getName());

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

  private void startGame() {
    LOG.info("Starting Ludo game.");
    try {
      if (gateway.players().size() < 2) {
        LOG.warning("Attempted to start Ludo game with insufficient players: " + gateway.players().size());
        Dialogs.warn("Cannot Start Game", "Ludo requires at least " + 2 + " players.");
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
      try {
        gateway.resetGame();
        LOG.info("Ludo game reset in gateway.");
      } catch (Exception ex) {
        LOG.log(Level.SEVERE, "Error resetting Ludo game.", ex);
        Errors.handle("An error occurred while resetting the game.", ex);
      }
    });
  }
}