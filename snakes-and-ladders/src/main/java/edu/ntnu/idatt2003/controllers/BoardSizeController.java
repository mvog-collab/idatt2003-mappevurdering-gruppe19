package edu.ntnu.idatt2003.controllers;

import edu.ntnu.idatt2003.gateway.GameGateway;
import edu.ntnu.idatt2003.ui.BoardSizePage;
import javafx.stage.Stage;

public class BoardSizeController implements BasePopupController {

    private final BoardSizePage view;
    private final GameGateway gameGateway;

    public BoardSizeController(BoardSizePage view, GameGateway gameGateway) {
        this.view = view;
        this.gameGateway = gameGateway;

        init();
    }
    
    private void init() {
        view.getSixtyFourTiles().setOnAction(e -> {
            gameGateway.newGame(64);
            System.out.println("Chose 64");
        });
        view.getNinetyTiles().setOnAction(e -> {
            gameGateway.newGame(90);
            System.out.println("Chose 90");
        });
        view.getOneTwentyTiles().setOnAction(e -> {
            gameGateway.newGame(120);
            System.out.println("Chose 120");
        });
        view.getContinueButton().setOnAction(e -> confirm());
        view.getCancelButton().setOnAction(e -> cancel());
    }

    @Override
    public void confirm() { 
        close();
    }

    @Override
    public void cancel()  { 
        close();
    }

  private void close() {
    System.out.println("Closing board size page");
    Stage s = (Stage) view.getContinueButton().getScene().getWindow();
    s.close();
  }
}
