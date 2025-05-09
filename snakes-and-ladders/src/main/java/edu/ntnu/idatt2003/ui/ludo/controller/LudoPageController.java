package edu.ntnu.idatt2003.ui.ludo.controller;

import edu.ntnu.idatt2003.ui.common.controller.AbstractPageController;
import edu.ntnu.idatt2003.gateway.CompleteBoardGame;
import edu.ntnu.idatt2003.ui.ludo.view.LudoBoardView;
import edu.ntnu.idatt2003.ui.ludo.view.LudoPage;
import edu.ntnu.idatt2003.ui.shared.controller.ChoosePlayerController;
import edu.ntnu.idatt2003.ui.shared.view.ChoosePlayerPage;
import edu.ntnu.idatt2003.utils.ResourcePaths;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

public final class LudoPageController extends AbstractPageController<LudoPage> {

    public LudoPageController(LudoPage view, CompleteBoardGame gateway) {
        super(view, gateway);

        gateway.newGame(0);
        initializeEventHandlers();
    }

    /* --------------------- helpers --------------------- */

    private void showPlayerDialog() {
        String[] ludoTokens = { "BLUE", "GREEN", "RED", "YELLOW" };
        ChoosePlayerPage p = new ChoosePlayerPage(ludoTokens);
        new ChoosePlayerController(p, gateway);
        createModalPopup("Players", p.getView(), 1000, 800).showAndWait();
    }

    private void startGame() {
        LudoBoardView boardView = new LudoBoardView();
        
        // Connect view to observe model
        boardView.connectToModel(gateway);
        
        LudoBoardController boardCtrl = new LudoBoardController(boardView, gateway);
    
        Stage stage = (Stage) view.startButton().getScene().getWindow();
        stage.setScene(boardView.getScene());
    
        // Initial state
        boardView.showDice(1);
    }

    @Override
    protected Stage createModalPopup(String title, javafx.scene.Parent root, int width, int height) {
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

    @Override
    protected void initializeEventHandlers() {
        view.choosePlayerButton().setOnAction(e -> showPlayerDialog());
        view.startButton().setOnAction(e -> startGame());
        view.resetButton().setOnAction(e -> gateway.resetGame());
    }
}