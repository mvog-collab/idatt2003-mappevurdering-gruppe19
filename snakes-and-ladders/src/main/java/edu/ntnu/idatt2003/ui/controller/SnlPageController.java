package edu.ntnu.idatt2003.ui.controller;

import edu.ntnu.idatt2003.gateway.GameGateway;
import edu.ntnu.idatt2003.ui.view.BoardSizePage;
import edu.ntnu.idatt2003.ui.view.BoardView;
import edu.ntnu.idatt2003.ui.view.ChoosePlayerPage;
import edu.ntnu.idatt2003.ui.view.SettingsPage;
import edu.ntnu.idatt2003.ui.view.SnlPage;
import edu.ntnu.idatt2003.utils.Dialogs;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class SnlPageController {

    private final SnlPage view;
    private final GameGateway gameGateway;

    public SnlPageController(SnlPage view, GameGateway gamegGateway) {
        this.view = view;
        this.gameGateway = gamegGateway;

        gamegGateway.newGame(90); 
        initializeButtonHandlers();
    }

    private void activateButtonFunctions() {
        view.getChooseBoardButton().setOnAction(null);
    }

    private void showPlayerDialog() {
        ChoosePlayerPage choosePlayerPage = new ChoosePlayerPage();
        new ChoosePlayerController(choosePlayerPage, gameGateway);
        createModalPopup("Players", choosePlayerPage.getView(), 800, 700).showAndWait();
        refreshUi();
    }

    private void initializeButtonHandlers() {
        setupChooseBoardButton();
        setupChoosePlayerButton();
        setupStartButton();
        setupResetGameButton();
        setupSettingsButton();
    }

    private void setupChooseBoardButton() {
        view.getChooseBoardButton().setOnAction(e -> {
            BoardSizePage page = new BoardSizePage();
            var root = page.getBoardSizeView();
            new BoardSizeController(page, gameGateway);
            createModalPopup("Choose board size", root, 600, 500)
                    .showAndWait();
            view.enableChoosePlayerButton();
        });
    }

    private void setupChoosePlayerButton() {
        view.getChoosePlayerButton().setOnAction(e -> {
            ChoosePlayerPage page = new ChoosePlayerPage();
            var root = page.getView();
            new ChoosePlayerController(page, gameGateway);

            createModalPopup("Choose players", root, 1000, 800).showAndWait();
            refreshUi();
        });
    }

    private void setupStartButton() {
        view.getStartButton().setOnAction(e -> {
            if (gameGateway.players().size() < 2) {
                view.alertUserAboutUnfinishedSetup();
                return;
            }

            int boardSize = gameGateway.boardSize();

            BoardView board = new BoardView(boardSize);
            BoardController boardController = new BoardController(board, gameGateway);

            /* first snapshot so the board has something to render */
            board.setPlayers(gameGateway.players(), gameGateway.boardOverlays());

            board.getRollButton() .setOnAction(evt -> boardController.playTurn());
            board.getAgainButton().setOnAction(evt -> boardController.resetGame());

            Stage stage = (Stage) view.getStartButton().getScene().getWindow();
            stage.setScene(board.start());
        });
    }

    private void setupResetGameButton() {
        view.getResetGameButton().setOnAction(e -> {
            gameGateway.newGame(gameGateway.boardSize());
            refreshUi();
            
            showResetConfirmation();
        });
    }

    private void setupSettingsButton() {
        view.getSettingsButton().setOnAction(e -> {
            SettingsPage page = new SettingsPage();
            //new SettingsController(page, gameGateway);

            Scene scene = new Scene(page.getView(), 400, 300);
            scene.getStylesheets().add(getClass().getResource("/styles/style.css").toExternalForm());

            Stage popup = new Stage();
            popup.initModality(Modality.APPLICATION_MODAL);
            popup.setTitle("Settings");
            popup.setScene(scene);
            popup.showAndWait();

            refreshUi();
        });
    }

    private void updateUIAfterReset() {
        view.disableChoosePlayerButton();
        view.disableStartButton();
    }

    private void refreshUi() {
        boolean ready = gameGateway.players().size() >= 2;
        updateUIAfterReset();
        view.getStartButton().setDisable(!ready);
    }

    private void showResetConfirmation() {
        Dialogs.info("Game reset", "The game has been reset successfully. Please choose a board to continue");
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