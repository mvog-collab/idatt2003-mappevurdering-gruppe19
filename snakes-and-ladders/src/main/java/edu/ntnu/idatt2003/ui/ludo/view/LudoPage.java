package edu.ntnu.idatt2003.ui.ludo.view;

import edu.ntnu.idatt2003.ui.common.view.AbstractMenuView;
import edu.ntnu.idatt2003.ui.MenuUIService;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class LudoPage extends AbstractMenuView {
    // UI components
    private final Scene scene;
    private final MenuUIService menuUIService;
    
    public LudoPage() {
        // Create service for UI construction
        menuUIService = new MenuUIService();
        
        // Create UI components using the service
        Label titleLabel = menuUIService.createTitleLabel("Ludo", "ludo-page-title");
        ImageView boardPreview = menuUIService.createBoardPreview("/images/ludoBoard.jpg", 350, 350);
        
        startButton = menuUIService.createMenuButton("Start game", "confirm-button");
        choosePlayerButton = menuUIService.createMenuButton("Choose players", "choose-button");
        resetButton = menuUIService.createMenuButton("Reset game", "exit-button");
        statusLabel = new Label("Start by choosing players");
        statusLabel.getStyleClass().add("status-label");
        
        // Initial button states
        startButton.setDisable(true);
        
        // Create layouts using the service
        VBox leftPanel = menuUIService.createLeftPanel(titleLabel, boardPreview);
        VBox menuPanel = menuUIService.createMenuPanel(choosePlayerButton, startButton, resetButton);
        menuPanel.getChildren().add(statusLabel);
        
        HBox mainLayout = menuUIService.createMainLayout(leftPanel, menuPanel);
        
        // Create scene
        scene = new Scene(new StackPane(mainLayout), 1000, 700);
        scene.getStylesheets().add(getStylesheet());
    }
    
    @Override
    public Scene getScene() {
        return scene;
    }

    public Button choosePlayerButton() {
        return choosePlayerButton;
    }

    public Button startButton() {
        return startButton;
    }

    public Button resetButton() {
        return resetButton;
    }
}