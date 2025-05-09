package edu.ntnu.idatt2003.ui.common.controller;

import edu.ntnu.idatt2003.gateway.CompleteBoardGame;
import edu.ntnu.idatt2003.utils.ResourcePaths;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

public abstract class AbstractController {
    protected final CompleteBoardGame gateway;
    
    public AbstractController(CompleteBoardGame gateway) {
        this.gateway = gateway;
    }
    
    // Common controller utilities
    protected Stage createModalPopup(String title, Parent root, int width, int height) {
        Stage popupStage = new Stage();
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.setTitle(title);

        Scene scene = new Scene(root, width, height);
        scene.getStylesheets().add(
            getClass().getResource(ResourcePaths.STYLE_SHEET).toExternalForm()
        );

        popupStage.setScene(scene);
        return popupStage;
    }
}