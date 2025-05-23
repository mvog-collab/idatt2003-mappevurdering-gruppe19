package edu.ntnu.idatt2003.presentation;

import edu.ntnu.idatt2003.exception.ResourceNotFoundException;
import edu.ntnu.idatt2003.presentation.navigation.NavigationService;
import edu.ntnu.idatt2003.utils.Errors;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.beans.binding.Bindings;
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

public class HomePage extends Application {

    private static final Logger LOG = Logger.getLogger(HomePage.class.getName());

    private Label title;
    private ImageView laddersBtn;
    private ImageView ludoBtn;
    private HBox buttonBox;
    private VBox root;
    private Scene homeScene;

    @Override
    public void start(Stage stage) {
        LOG.info("HomePage starting...");
        try {
            NavigationService.getInstance().initialize(stage);
            stage.setScene(createScene(stage));
            stage.setTitle("Retro Roll & Rise");
            stage.show();
            LOG.info("HomePage started successfully.");
        } catch (ResourceNotFoundException e) {
            LOG.log(Level.SEVERE, "Failed to load resources for HomePage", e);
            Errors.handle(
                    "Could not load essential resources for the home page. The application might not work correctly.",
                    e);
            // Optionally, show a dialog here directly if Errors.handle doesn't, or rethrow
            // to be caught by main
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "An unexpected error occurred during HomePage startup", e);
            Errors.handle("An unexpected error occurred while starting the application.", e);
        }
    }

    public Scene createScene(Stage stageForEventHandlers) {
        LOG.fine("Creating Home Scene...");
        buildUI();
        setupEventHandlers(stageForEventHandlers);
        homeScene = new Scene(root, 800, 600);
        try {
            String cssPath = "/styles/style.css";
            String cssExternalForm = getClass().getResource(cssPath).toExternalForm();
            if (cssExternalForm == null) {
                throw new ResourceNotFoundException(cssPath);
            }
            homeScene.getStylesheets().add(cssExternalForm);
        } catch (NullPointerException | ResourceNotFoundException e) {
            LOG.log(Level.WARNING, "Failed to load stylesheet /styles/style.css", e);
            // Application can continue without stylesheet, but log a warning.
        }

        double imageButtonWidthFactor = 0.22;
        double imageButtonHeightFactor = 0.30;
        double minImageWidth = 220;
        double minImageHeight = 220;

        laddersBtn.fitWidthProperty().bind(
                Bindings.createDoubleBinding(
                        () -> Math.max(
                                homeScene.getWidth() * imageButtonWidthFactor,
                                minImageWidth),
                        homeScene.widthProperty()));
        laddersBtn.fitHeightProperty().bind(
                Bindings.createDoubleBinding(
                        () -> Math.max(
                                homeScene.getHeight() * imageButtonHeightFactor,
                                minImageHeight),
                        homeScene.heightProperty()));

        ludoBtn.fitWidthProperty().bind(
                Bindings.createDoubleBinding(
                        () -> Math.max(
                                homeScene.getWidth() * imageButtonWidthFactor,
                                minImageWidth),
                        homeScene.widthProperty()));
        ludoBtn.fitHeightProperty().bind(
                Bindings.createDoubleBinding(
                        () -> Math.max(
                                homeScene.getHeight() * imageButtonHeightFactor,
                                minImageHeight),
                        homeScene.heightProperty()));

        buttonBox.spacingProperty().bind(homeScene.widthProperty().multiply(0.05));
        root.spacingProperty().bind(homeScene.heightProperty().multiply(0.05));

        root.paddingProperty()
                .bind(
                        Bindings.createObjectBinding(
                                () -> new Insets(
                                        homeScene.heightProperty().doubleValue() * 0.05,
                                        homeScene.widthProperty().doubleValue() * 0.05,
                                        homeScene.heightProperty().doubleValue() * 0.05,
                                        homeScene.widthProperty().doubleValue() * 0.05),
                                homeScene.widthProperty(),
                                homeScene.heightProperty()));
        LOG.fine("Home Scene created.");
        return homeScene;
    }

    private void buildUI() {
        title = new Label("Retro Roll & Rise");
        title.getStyleClass().add("home-page-title");
        title.setWrapText(true);
        laddersBtn = createGameButton("SnakeAndLadder.png");
        ludoBtn = createGameButton("Ludo.png");
        buttonBox = new HBox(40, laddersBtn, ludoBtn);
        buttonBox.setAlignment(Pos.CENTER);
        root = new VBox(40, title, buttonBox);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(40));
        root.getStyleClass().add("page-background");
    }

    private ImageView createGameButton(String imageName) {
        String imagePath = "/images/" + imageName;
        InputStream imageStream = getClass().getResourceAsStream(imagePath);
        if (imageStream == null) {
            LOG.log(Level.SEVERE, "Game button image not found: " + imagePath);
            throw new ResourceNotFoundException(imagePath);
        }
        ImageView button = new ImageView(new Image(imageStream));
        button.setFitWidth(250);
        button.setFitHeight(250);
        button.setPickOnBounds(true);
        button.setPreserveRatio(true);
        button.getStyleClass().add("menu-button-image");
        button.setOnMouseEntered(
                e -> {
                    button.setScaleX(1.1);
                    button.setScaleY(1.1);
                });
        button.setOnMouseExited(
                e -> {
                    button.setScaleX(1.0);
                    button.setScaleY(1.0);
                });
        return button;
    }

    private void setupEventHandlers(Stage stage) {
        laddersBtn.setOnMouseClicked(
                (MouseEvent e) -> {
                    LOG.info("Ladders button clicked.");
                    NavigationService.getInstance().navigateToSnlPage();
                });

        ludoBtn.setOnMouseClicked(
                (MouseEvent e) -> {
                    LOG.info("Ludo button clicked.");
                    NavigationService.getInstance().navigateToLudoPage();
                });
    }
}