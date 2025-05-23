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

/**
 * The application's home page, offering navigation to available games.
 * <p>
 * Displays buttons for "Snake & Ladders" and "Ludo", with responsive
 * sizing and hover effects. Initializes the {@link NavigationService}.
 * </p>
 */
public class HomePage extends Application {

    private static final Logger LOG = Logger.getLogger(HomePage.class.getName());
    private ImageView laddersBtn;
    private ImageView ludoBtn;
    private HBox buttonBox;
    private VBox root;

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
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "An unexpected error occurred during HomePage startup", e);
            Errors.handle("An unexpected error occurred while starting the application.", e);
        }
    }

    /**
     * Builds and returns the home page scene.
     * <p>
     * Sets up UI elements, binds their sizes to the scene dimensions,
     * applies stylesheet, and configures responsive layout.
     * </p>
     *
     * @param stageForEventHandlers the stage used for attaching event handlers
     * @return the fully constructed {@link Scene}
     */
    public Scene createScene(Stage stageForEventHandlers) {
        LOG.fine("Creating Home Scene...");
        buildUI();
        setupEventHandlers(stageForEventHandlers);

        Scene homeScene = new Scene(root, 800, 600);
        try {
            String cssPath = "/styles/style.css";
            String cssExternalForm = getClass().getResource(cssPath).toExternalForm();
            if (cssExternalForm == null) {
                throw new ResourceNotFoundException(cssPath);
            }
            homeScene.getStylesheets().add(cssExternalForm);
        } catch (NullPointerException | ResourceNotFoundException e) {
            LOG.log(Level.WARNING, "Failed to load stylesheet /styles/style.css", e);
        }

        double widthFactor = 0.22;
        double heightFactor = 0.30;
        double minW = 220;
        double minH = 220;

        laddersBtn.fitWidthProperty().bind(
                Bindings.createDoubleBinding(
                        () -> Math.max(homeScene.getWidth() * widthFactor, minW),
                        homeScene.widthProperty()));
        laddersBtn.fitHeightProperty().bind(
                Bindings.createDoubleBinding(
                        () -> Math.max(homeScene.getHeight() * heightFactor, minH),
                        homeScene.heightProperty()));

        ludoBtn.fitWidthProperty().bind(
                Bindings.createDoubleBinding(
                        () -> Math.max(homeScene.getWidth() * widthFactor, minW),
                        homeScene.widthProperty()));
        ludoBtn.fitHeightProperty().bind(
                Bindings.createDoubleBinding(
                        () -> Math.max(homeScene.getHeight() * heightFactor, minH),
                        homeScene.heightProperty()));

        buttonBox.spacingProperty().bind(homeScene.widthProperty().multiply(0.05));
        root.spacingProperty().bind(homeScene.heightProperty().multiply(0.05));
        root.paddingProperty().bind(
                Bindings.createObjectBinding(
                        () -> new Insets(
                                homeScene.getHeight() * 0.05,
                                homeScene.getWidth() * 0.05,
                                homeScene.getHeight() * 0.05,
                                homeScene.getWidth() * 0.05),
                        homeScene.widthProperty(),
                        homeScene.heightProperty()));

        LOG.fine("Home Scene created.");
        return homeScene;
    }

    /**
     * Constructs the UI nodes for the home page.
     * <p>
     * Creates the title label and game buttons, arranges them in a VBox.
     * </p>
     */
    private void buildUI() {
        Label title = new Label("Retro Roll & Rise");
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

    /**
     * Creates a game-selection button with hover animation.
     *
     * @param imageName the filename of the game icon in /images/
     * @return a configured {@link ImageView} acting as a button
     * @throws ResourceNotFoundException if the image resource is missing
     */
    private ImageView createGameButton(String imageName) {
        String path = "/images/" + imageName;
        InputStream stream = getClass().getResourceAsStream(path);
        if (stream == null) {
            LOG.log(Level.SEVERE, "Game button image not found: {0} ", path);
            throw new ResourceNotFoundException(path);
        }
        ImageView iv = new ImageView(new Image(stream));
        iv.setFitWidth(250);
        iv.setFitHeight(250);
        iv.setPickOnBounds(true);
        iv.setPreserveRatio(true);
        iv.getStyleClass().add("menu-button-image");
        iv.setOnMouseEntered(e -> {
            iv.setScaleX(1.1);
            iv.setScaleY(1.1);
        });
        iv.setOnMouseExited(e -> {
            iv.setScaleX(1.0);
            iv.setScaleY(1.0);
        });
        return iv;
    }

    /**
     * Attaches click handlers to the game buttons.
     *
     * @param stage the stage for potential context use (unused here)
     */
    private void setupEventHandlers(Stage stage) {
        laddersBtn.setOnMouseClicked((MouseEvent e) -> {
            LOG.info("Ladders button clicked.");
            NavigationService.getInstance().navigateToSnlPage();
        });
        ludoBtn.setOnMouseClicked((MouseEvent e) -> {
            LOG.info("Ludo button clicked.");
            NavigationService.getInstance().navigateToLudoPage();
        });
    }
}