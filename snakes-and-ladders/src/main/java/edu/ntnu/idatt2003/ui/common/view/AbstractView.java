package edu.ntnu.idatt2003.ui.common.view;

import edu.games.engine.observer.BoardGameEvent;
import edu.games.engine.observer.BoardGameObserver;
import edu.ntnu.idatt2003.gateway.CompleteBoardGame;
import edu.ntnu.idatt2003.utils.ResourcePaths;
import javafx.application.Platform;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

public abstract class AbstractView implements BoardGameObserver {
    protected CompleteBoardGame gateway;
    
    public void connectToModel(CompleteBoardGame gateway) {
        this.gateway = gateway;
        gateway.addObserver(this);
    }
    
    @Override
    public void update(BoardGameEvent event) {
        Platform.runLater(() -> {
            handleEvent(event);
        });
    }
    
    protected abstract void handleEvent(BoardGameEvent event);
    
    // Common UI utilities
    protected Stage createModalPopup(String title, Parent root, int width, int height) {
        Stage popupStage = new Stage();
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.setTitle(title);

        Scene scene = new Scene(root, width, height);
        scene.getStylesheets().add(
            getClass().getResource(ResourcePaths.STYLE_SHEET).toExternalForm()
        );

        scene.getRoot().requestFocus();
        popupStage.setScene(scene);
        return popupStage;
    }
}