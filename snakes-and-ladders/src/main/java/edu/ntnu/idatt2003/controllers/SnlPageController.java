package edu.ntnu.idatt2003.controllers;

import edu.ntnu.idatt2003.gateway.GameGateway;
import edu.ntnu.idatt2003.ui.BoardSizePage;
import edu.ntnu.idatt2003.ui.BoardView;
import edu.ntnu.idatt2003.ui.ChoosePlayerPage;
import edu.ntnu.idatt2003.ui.SettingsPage;
import edu.ntnu.idatt2003.ui.SnlPage;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
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

            createModalPopup("Choose players", root, 800, 700).showAndWait();
            refreshUi();
        });
    }

    private void setupStartButton() {
        view.getStartButton().setOnAction(e -> {
            if (gameGateway.players().size() < 2) {
                view.alertUserAboutUnfinishedSetup();
                return;
            }

            BoardView board = new BoardView();
            BoardController c = new BoardController(board, gameGateway);

            /* first snapshot so the board has something to render */
            board.setPlayers(gameGateway.players(), gameGateway.boardOverlays());

            board.getRollButton() .setOnAction(evt -> c.playTurn());
            board.getAgainButton().setOnAction(evt -> c.resetGame());

            Stage stage = (Stage) view.getStartButton().getScene().getWindow();
            stage.setScene(board.start());
        });
    }

    private void setupResetGameButton() {
        view.getResetGameButton().setOnAction(e -> {
            gameGateway.newGame(gameGateway.boardSize());
            refreshUi();
            new Alert(Alert.AlertType.INFORMATION,
                      "Game has been reset. Pick players and start again.").showAndWait();
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
        view.getChoosePlayerButton().setDisable(false);
        view.getStartButton().setDisable(!ready);
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