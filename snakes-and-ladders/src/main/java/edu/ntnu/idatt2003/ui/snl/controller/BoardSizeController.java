package edu.ntnu.idatt2003.ui.snl.controller;

import edu.ntnu.idatt2003.gateway.CompleteBoardGame;
import edu.ntnu.idatt2003.ui.common.controller.AbstractPopupController;
import edu.ntnu.idatt2003.ui.snl.view.BoardSizePage;
import edu.ntnu.idatt2003.utils.Errors;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BoardSizeController extends AbstractPopupController<BoardSizePage> {
  private static final Logger LOG = Logger.getLogger(BoardSizeController.class.getName());

  public BoardSizeController(BoardSizePage view, CompleteBoardGame gateway) {
    super(view, gateway);
    LOG.info("BoardSizeController initialized.");
  }

  @Override
  protected void initializeEventHandlers() {
    LOG.fine("Initializing BoardSizePage event handlers.");
    view.getSixtyFourTiles()
        .setOnAction(
            e -> {
              LOG.info("Board size 64 selected.");
              setNewGameSize(64);
            });
    view.getNinetyTiles()
        .setOnAction(
            e -> {
              LOG.info("Board size 90 selected.");
              setNewGameSize(90);
            });
    view.getOneTwentyTiles()
        .setOnAction(
            e -> {
              LOG.info("Board size 120 selected.");
              setNewGameSize(120);
            });
    view.getContinueButton().setOnAction(e -> {
      LOG.info("Board size confirmation clicked.");
      confirm();
    });
    view.getCancelButton().setOnAction(e -> {
      LOG.info("Board size selection cancelled.");
      cancel();
    });
  }

  private void setNewGameSize(int size) {
    try {
      gateway.newGame(size);
      LOG.info("Gateway newGame called with size: " + size);
    } catch (Exception ex) {
      LOG.log(Level.SEVERE, "Error setting new game size to " + size, ex);
      Errors.handle("Could not set the board size. Please try again.", ex);
    }
  }

  @Override
  public void confirm() {
    LOG.fine("BoardSizePage confirmed.");
    close(view.getContinueButton());
  }

  @Override
  public void cancel() {
    LOG.fine("BoardSizePage cancelled.");
    close(view.getCancelButton());
  }
}