package edu.ntnu.idatt2003.ui.snl.controller;

import edu.ntnu.idatt2003.gateway.CompleteBoardGame;
import edu.ntnu.idatt2003.ui.common.controller.AbstractPopupController;
import edu.ntnu.idatt2003.ui.snl.view.BoardSizePage;

public class BoardSizeController extends AbstractPopupController<BoardSizePage> {

  public BoardSizeController(BoardSizePage view, CompleteBoardGame gateway) {
    super(view, gateway);
  }

  @Override
  protected void initializeEventHandlers() {
    view.getSixtyFourTiles()
        .setOnAction(
            e -> {
              gateway.newGame(64);
              System.out.println("Chose 64");
            });
    view.getNinetyTiles()
        .setOnAction(
            e -> {
              gateway.newGame(90);
              System.out.println("Chose 90");
            });
    view.getOneTwentyTiles()
        .setOnAction(
            e -> {
              gateway.newGame(120);
              System.out.println("Chose 120");
            });
    view.getContinueButton().setOnAction(e -> confirm());
    view.getCancelButton().setOnAction(e -> cancel());
  }

  @Override
  public void confirm() {
    close(view.getContinueButton());
  }

  @Override
  public void cancel() {
    close(view.getCancelButton());
  }
}
