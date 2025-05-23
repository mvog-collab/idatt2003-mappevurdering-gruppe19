package edu.ntnu.idatt2003.presentation.common.view;

import edu.games.engine.observer.BoardGameEvent;
import edu.games.engine.observer.BoardGameObserver;
import edu.ntnu.idatt2003.gateway.CompleteBoardGame;
import edu.ntnu.idatt2003.presentation.navigation.NavigationService;
import edu.ntnu.idatt2003.utils.ResourcePaths;
import java.io.InputStream;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;

/**
 * Base class for all application views.
 * <p>
 * Implements {@link BoardGameObserver} to receive game events,
 * handles threading concerns, and provides utilities for common
 * UI components like navigation and help buttons.
 * </p>
 */
public abstract class AbstractView implements BoardGameObserver {
  private static final Logger LOG = Logger.getLogger(AbstractView.class.getName());
  protected CompleteBoardGame gateway;
  private static final String iconStyling = "icon-button";

  /**
   * Connects this view to the given game gateway and registers as an observer.
   *
   * @param gateway the shared {@link CompleteBoardGame} instance
   */
  public void connectToModel(CompleteBoardGame gateway) {
    this.gateway = gateway;
    if (gateway != null) {
      gateway.addObserver(this);
    }
  }

  /**
   * Receives game events and dispatches to {@link #handleEvent(BoardGameEvent)}
   * on the JavaFX application thread.
   *
   * @param event the game event
   */
  @Override
  public void update(BoardGameEvent event) {
    Platform.runLater(() -> handleEvent(event));
  }

  /**
   * Handles a single {@link BoardGameEvent}.
   * <p>
   * Subclasses must implement to react to specific event types.
   * </p>
   *
   * @param event the event to handle
   */
  protected abstract void handleEvent(BoardGameEvent event);

  /**
   * Loads an image resource and returns an {@link ImageView} with standard
   * sizing.
   * Uses a placeholder if the image cannot be found.
   *
   * @param imagePath the classpath resource path
   * @param altText   descriptive text for logging if the image is missing
   * @return an {@link ImageView} sized to 24×24 pixels
   */
  private ImageView createIcon(String imagePath, String altText) {
    InputStream imageStream = getClass().getResourceAsStream(imagePath);
    if (imageStream == null) {
      LOG.warning("Icon image not found: " + imagePath + ". Using placeholder for " + altText);
      ImageView placeholder = new ImageView();
      placeholder.setFitWidth(24);
      placeholder.setFitHeight(24);
      return placeholder;
    }
    ImageView icon = new ImageView(new Image(imageStream));
    icon.setFitWidth(24);
    icon.setFitHeight(24);
    icon.setPreserveRatio(true);
    return icon;
  }

  /**
   * Creates a "How to Play" button that shows an informational dialog
   * with the given title and instructions.
   *
   * @param title        the dialog title
   * @param instructions the content text to display
   * @return a configured {@link Button} with a question-mark icon
   */
  protected Button createHowToPlayButton(String title, String instructions) {

    ImageView icon = createIcon("/images/question-sign.png", "How to Play");
    Button howToPlayButton = new Button();
    howToPlayButton.setGraphic(icon);
    howToPlayButton.getStyleClass().add(iconStyling);
    howToPlayButton.setOnAction(e -> {
      Alert alert = new Alert(Alert.AlertType.INFORMATION);
      alert.setTitle(title);
      alert.setHeaderText(null);
      alert.setContentText(instructions);
      alert.getDialogPane().getStyleClass().add("how-to-alert");
      java.net.URL cssUrl = getClass().getResource(ResourcePaths.STYLE_SHEET);
      if (cssUrl != null) {
        alert.getDialogPane().getStylesheets().add(cssUrl.toExternalForm());
      } else {
        LOG.warning("Stylesheet for 'How to play' alert not found: " + ResourcePaths.STYLE_SHEET);
      }
      alert.showAndWait();
    });
    return howToPlayButton;
  }

  /**
   * Creates the top navigation bar containing Home, optional Back, and Help
   * buttons.
   *
   * @param includeBackButtonToGameSetup whether to include a "Back to Setup"
   *                                     button
   * @param helpButtonInstance           an already-configured Help button, or
   *                                     null
   * @return an {@link HBox} containing the navigation controls
   */
  protected HBox createTopBarWithNavigationAndHelp(
      boolean includeBackButtonToGameSetup, Button helpButtonInstance) {
    HBox topBar = new HBox();
    topBar.setPadding(new Insets(10));
    topBar.setAlignment(Pos.CENTER_LEFT);

    HBox leftAligned = new HBox(10);
    leftAligned.setAlignment(Pos.CENTER_LEFT);
    leftAligned.getChildren().add(createGoToHomeButton());
    if (includeBackButtonToGameSetup) {
      leftAligned.getChildren().add(createGoBackToGameSetupButton());
    }
    topBar.getChildren().add(leftAligned);

    if (helpButtonInstance != null) {
      Region spacer = new Region();
      HBox.setHgrow(spacer, Priority.ALWAYS);
      HBox rightAligned = new HBox(helpButtonInstance);
      rightAligned.setAlignment(Pos.CENTER_RIGHT);
      topBar.getChildren().addAll(spacer, rightAligned);
    }

    topBar.getStyleClass().add("page-background");
    return topBar;
  }

  /**
   * Adds the navigation-and-help bar to the top region of the given
   * {@link BorderPane}.
   *
   * @param root                         the layout to receive the top bar
   * @param includeBackButtonToGameSetup whether to include a "Back" button
   * @param helpButton                   the Help button instance, or null
   */
  protected void addNavigationAndHelpToBorderPane(
      BorderPane root, boolean includeBackButtonToGameSetup, Button helpButton) {
    root.setTop(createTopBarWithNavigationAndHelp(includeBackButtonToGameSetup, helpButton));
  }

  // Private helpers for creating Home and Back buttons:

  private Button createGoToHomeButton() {
    ImageView icon = createIcon("/images/home.png", "Go to Home");
    Button home = new Button();
    home.setGraphic(icon);
    home.getStyleClass().add(iconStyling);
    home.setOnAction(e -> NavigationService.getInstance().navigateToHome());
    return home;
  }

  private Button createGoBackToGameSetupButton() {
    ImageView icon = createIcon("/images/back.png", "Go Back");
    Button back = new Button();
    back.setGraphic(icon);
    back.getStyleClass().add(iconStyling);
    back.setOnAction(e -> NavigationService.getInstance().goBackToGameSetupPage());
    return back;
  }
}