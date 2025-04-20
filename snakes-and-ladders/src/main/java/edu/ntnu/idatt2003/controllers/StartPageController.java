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
import javafx.scene.control.Alert;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class StartPageController {

    private final StartPage startPage;
    private GameModel gameModel;
    private final int DEFAULT_BOARD_SIZE = 100;

    public StartPageController(StartPage startPage) {
        this.startPage = startPage;
        this.gameModel = createNewGameModel();
        initializeButtonHandlers();
    }

    private GameModel createNewGameModel() {
        return new GameModel(BoardMaker.createBoard(DEFAULT_BOARD_SIZE), new Dice());
    }

    private void initializeButtonHandlers() {
        setupChooseBoardButton();
        setupChoosePlayerButton();
        setupStartButton();
        setupResetGameButton();
    }

    private void setupChooseBoardButton() {
        startPage.getChooseBoardButton().setOnAction(e -> {
            BoardSizePage boardSizePage = new BoardSizePage();

            Stage boardPickPopup = createModalPopup("Choose Board Size", boardSizePage.getBoardSizeView(), 600, 500);

            new BoardSizeController(boardSizePage, selectedBoard -> {
                gameModel.setBoard(selectedBoard);
            });

            boardPickPopup.showAndWait();
            startPage.enableChoosePlayerButton();
        });
    }

    private void setupChoosePlayerButton() {
        startPage.getChoosePlayerButton().setOnAction(e -> {
            ChoosePlayerPage choosePlayerPage = new ChoosePlayerPage();

            Stage choosePlayerPopup = createModalPopup("Choose Players", choosePlayerPage.getView(), 800, 700);

            new ChoosePlayerController(choosePlayerPage, gameModel);

            
            choosePlayerPopup.showAndWait();
            startPage.enableStartButton();
        });
    }

    private void setupStartButton() {
        startPage.getStartButton().setOnAction(e -> {
            if (gameModel.getBoard() == null || gameModel.getBoard().getSize() == 100 || gameModel.getPlayers().size() < 2) {
                startPage.alertUserAboutUnfinishedSetup();
                return;
            }
            BoardView gameBoard = new BoardView(gameModel);
            Stage stage = (Stage) startPage.getStartButton().getScene().getWindow();
            stage.setScene(gameBoard.start());
        });
    }

    private void setupResetGameButton() {
        startPage.getResetGameButton().setOnAction(e -> {
            resetGame();
            updateUIAfterReset();
            showResetConfirmation();
        });
    }

    private void resetGame() {
        this.gameModel = createNewGameModel();
    }

    private void updateUIAfterReset() {
        startPage.disableChoosePlayerButton();
        startPage.disableStartButton();
    }

    private void showResetConfirmation() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Game Reset");
        alert.setHeaderText(null);
        alert.setContentText("The game has been reset successfully. Please choose a board to continue.");
        alert.showAndWait();
    }

    private Stage createModalPopup(String title, javafx.scene.Parent root, int width, int height) {
        Stage popupStage = new Stage();
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.setTitle(title);

        Scene scene = new Scene(root, width, height);
        
        scene.getRoot().requestFocus();

        popupStage.setScene(scene);
        return popupStage;
    }
}