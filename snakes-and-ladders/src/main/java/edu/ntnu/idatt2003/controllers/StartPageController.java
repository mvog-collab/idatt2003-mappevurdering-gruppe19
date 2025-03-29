package edu.ntnu.idatt2003.controllers;

import edu.ntnu.idatt2003.game_logic.BoardMaker;
import edu.ntnu.idatt2003.models.Board;
import edu.ntnu.idatt2003.models.Dice;
import edu.ntnu.idatt2003.models.GameModel;
import edu.ntnu.idatt2003.ui.BoardSizePage;
import edu.ntnu.idatt2003.ui.BoardView;
import edu.ntnu.idatt2003.ui.ChoosePlayerPage;
import edu.ntnu.idatt2003.ui.StartPage;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class StartPageController {

    private final StartPage startPage;
    private final GameModel gameModel;

    public StartPageController(StartPage startPage) {
        this.startPage = startPage;
        Board initialBoard = BoardMaker.createBoard(100);
        this.gameModel = new GameModel(initialBoard, new Dice());
        init();
    }

    private void init() {
        startPage.getChoosePlayerButton().setOnAction(e -> {
            ChoosePlayerPage choosePlayerPage = new ChoosePlayerPage();
            Stage choosePlayerPopup = new Stage();
            choosePlayerPopup.initModality(Modality.APPLICATION_MODAL);
            choosePlayerPopup.setTitle("Choose players");

            Scene scene = new Scene(choosePlayerPage.getView(), 500, 350);

            ChoosePlayerController choosePlayerController = new ChoosePlayerController(choosePlayerPage, gameModel);

            choosePlayerPopup.setScene(scene);
            choosePlayerPopup.showAndWait();

            startPage.enableChooseBoardButton();
        });
        
        startPage.getChooseBoardButton().setOnAction(e -> {
            BoardSizePage boardSizePage = new BoardSizePage();
            Stage boardPickPopup = new Stage();
            boardPickPopup.initModality(Modality.APPLICATION_MODAL);

            Scene scene = new Scene(boardSizePage.getBoardSizeView(), 500, 350);

            BoardSizeController boardSizeController = new BoardSizeController(boardSizePage, selectedBoard -> {
                gameModel.setBoard(selectedBoard);
            });

            boardPickPopup.setScene(scene);
            boardPickPopup.showAndWait();

            startPage.enableStartButton();
        });

        startPage.getStartButton().setOnAction(e -> {
            BoardView gameBoard = new BoardView(gameModel);
            Stage stage = (Stage) startPage.getStartButton().getScene().getWindow();
            stage.setScene(gameBoard.start());
        });
    }




    
}
