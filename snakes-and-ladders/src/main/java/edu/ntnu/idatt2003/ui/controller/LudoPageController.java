package edu.ntnu.idatt2003.ui.controller;

import edu.ntnu.idatt2003.gateway.GameGateway;
import edu.ntnu.idatt2003.ui.view.*;
import edu.ntnu.idatt2003.utils.ResourcePaths;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

public final class LudoPageController {

    private final LudoPage view;
    private final GameGateway gameGateway;

    public LudoPageController(LudoPage view, GameGateway gameGateway) {
        this.view = view;
        this.gameGateway   = gameGateway;

        gameGateway.newGame(0);                         // 57 felter – ingen boardsize‑dialog
        initButtons();
    }

    private void initButtons() {
        view.choosePlayerButton().setOnAction(e -> showPlayerDialog());
        view.startButton()       .setOnAction(e -> startGame());
        view.resetButton()       .setOnAction(e -> gameGateway.resetGame());
    }

    /* --------------------- helpers --------------------- */

    private void showPlayerDialog() {
        ChoosePlayerPage p = new ChoosePlayerPage();
        new ChoosePlayerController(p, gameGateway);
        createModalPopup("Players", p.getView(), 1000, 800).showAndWait();
    }

    private void startGame() {
        // if (gameGateway.players().size() < 2) {
        //     Dialogs.warn(
        //             "Need 2 players", "Please add at least two players first");
        //     return;
        // }

        LudoBoardView boardView = new LudoBoardView();
        LudoBoardController boardCtrl = new LudoBoardController(boardView, gameGateway);

        Stage stage = (Stage) view.startButton().getScene().getWindow();
        stage.setScene(boardView.getScene());          // vis brettet

        boardView.showDice(1);
    }

    private Stage createModalPopup(String title, javafx.scene.Parent root, int width, int height) {
        Stage popupStage = new Stage();
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.setTitle(title);

        Scene scene = new Scene(root, width, height);

        scene.getStylesheets().add(
            getClass().getResource(ResourcePaths.STYLE_SHEET)
                .toExternalForm()
        );

        scene.getRoot().requestFocus();

        popupStage.setScene(scene);
        return popupStage;
    }
}