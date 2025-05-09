package edu.ntnu.idatt2003.ui.shared.view;

import edu.ntnu.idatt2003.gateway.CompleteBoardGame;
import edu.ntnu.idatt2003.gateway.GameGateway;
import edu.ntnu.idatt2003.gateway.LudoGateway;
import edu.ntnu.idatt2003.gateway.SnlGatewayFactory;
import edu.ntnu.idatt2003.ui.ludo.controller.LudoPageController;
import edu.ntnu.idatt2003.ui.ludo.view.LudoPage;
import edu.ntnu.idatt2003.ui.snl.controller.SnlPageController;
import edu.ntnu.idatt2003.ui.snl.view.SnlPage;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Main entry point for the application.
 * This class is responsible for displaying the home screen and initializing
 * the chosen game mode.
 */
public class HomePage extends Application {

    // UI components
    private Label title;
    private ImageView laddersBtn;
    private ImageView ludoBtn;
    private HBox buttonBox;
    private VBox root;
    private Scene scene;

    @Override
    public void start(Stage stage) {
        show(stage);
    }

    /**
     * Creates and displays the home screen UI
     */
    public void show(Stage stage) {
        buildUI();
        setupEventHandlers(stage);
        
        stage.setTitle("Retro Roll & Rise");
        stage.setScene(scene);
        stage.show();
    }
    
    /**
     * Builds the UI components
     */
    private void buildUI() {
        // Create main title
        title = new Label("Retro Roll & Rise");
        title.getStyleClass().add("home-page-title");

        // Create game selection buttons
        laddersBtn = createGameButton("SnakeAndLadder.png");
        ludoBtn = createGameButton("Ludo.png");

        // Create layout containers
        buttonBox = new HBox(40, laddersBtn, ludoBtn);
        buttonBox.setAlignment(Pos.CENTER);

        root = new VBox(40, title, buttonBox);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(40));
        root.getStyleClass().add("page-background");

        // Create scene
        scene = new Scene(root, 800, 600);
        scene.getStylesheets().add(getClass().getResource("/styles/style.css").toExternalForm());
    }
    
    /**
     * Creates a styled game button with an image
     */
    private ImageView createGameButton(String imageName) {
        ImageView button = new ImageView(new Image(getClass().getResourceAsStream("/images/" + imageName)));
        button.setFitWidth(250);
        button.setFitHeight(250);
        button.getStyleClass().add("menu-button-image");
        
        // Add hover effects
        button.setOnMouseEntered(e -> {
            button.setScaleX(1.1);
            button.setScaleY(1.1);
        });
        button.setOnMouseExited(e -> {
            button.setScaleX(1.0);
            button.setScaleY(1.0);
        });
        
        return button;
    }
    
    /**
     * Sets up event handlers for UI components
     */
    private void setupEventHandlers(Stage stage) {
    // Snakes & Ladders button click handler
    laddersBtn.setOnMouseClicked((MouseEvent e) -> {
        try {
            // Create gateway and connect to view using observer pattern
            GameGateway snlGateway = SnlGatewayFactory.createDefault();
            SnlPage snlPage = new SnlPage();
            
            // Cast to CompleteBoardGame since SnlGateway implements it
            CompleteBoardGame completeBoardGame = (CompleteBoardGame) snlGateway;
            
            // Connect view to observe the model
            snlPage.connectToModel(completeBoardGame);
            
            // Create controller
            new SnlPageController(snlPage, completeBoardGame);
            
            stage.setScene(snlPage.getScene());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    });

    // Ludo button click handler
    ludoBtn.setOnMouseClicked((MouseEvent e) -> {
        try {
            // Create gateway and connect to view using observer pattern
            GameGateway ludoGateway = LudoGateway.createDefault();
            LudoPage ludoPage = new LudoPage();
            
            // Cast to CompleteBoardGame since LudoGateway implements it 
            CompleteBoardGame completeBoardGame = (CompleteBoardGame) ludoGateway;
            
            // Connect view to observe the model
            ludoPage.connectToModel(completeBoardGame);
            
            // Create controller
            new LudoPageController(ludoPage, completeBoardGame);
            
            stage.setScene(ludoPage.getScene());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    });
}
}