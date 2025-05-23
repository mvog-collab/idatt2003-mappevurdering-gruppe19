package edu.ntnu.idatt2003.presentation.ludo.view;

import edu.ntnu.idatt2003.exception.ResourceNotFoundException;
import edu.ntnu.idatt2003.presentation.MenuUIService;
import edu.ntnu.idatt2003.presentation.common.view.AbstractMenuView;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

/**
 * The main page view for Ludo, displaying title, board preview and menu buttons
 * for
 * choosing players, starting, and resetting the game.
 */
public class LudoPage extends AbstractMenuView {
  private static final Logger LOG = Logger.getLogger(LudoPage.class.getName());
  private final MenuUIService menuUIService;
  private BorderPane rootLayout;
  private ImageView boardPreview;
  private Scene scene;

  /**
   * Constructs the Ludo page UI.
   */
  public LudoPage() {
    this.menuUIService = new MenuUIService();
    this.rootLayout = new BorderPane();
    buildUi();
  }

  private void buildUi() {
    Label titleLabel = menuUIService.createTitleLabel("Ludo", "ludo-page-title");
    titleLabel.setWrapText(true);
    try {
      boardPreview = menuUIService.createBoardPreview("/images/ludoBoard.jpg", 400, 400);
    } catch (ResourceNotFoundException e) {
      LOG.log(Level.WARNING, "Ludo board preview image not found, using placeholder.", e);
      boardPreview = new ImageView(); // Placeholder
      boardPreview.setFitWidth(400);
      boardPreview.setFitHeight(400);
    }
    boardPreview.setPreserveRatio(true);

    startButton = menuUIService.createMenuButton("Start game", "confirm-button");
    choosePlayerButton = menuUIService.createMenuButton("Choose players", "choose-button");
    resetButton = menuUIService.createMenuButton("Reset game", "exit-button");
    statusLabel = new Label("Start by choosing players");
    statusLabel.getStyleClass().add("status-label");
    startButton.setDisable(true);

    Button howToButton = createHowToPlayButton(
        "How to play - Ludo",
        """
            - Throw a 6 to get a piece out of your home.
            - If you land on an enemy player(s), they are sent back to their home.
            - The first player with all pieces in the goal is the winner!
            """);

    VBox leftPanel = menuUIService.createLeftPanel(titleLabel, boardPreview);
    VBox menuPanel = menuUIService.createMenuPanel(howToButton, choosePlayerButton, startButton, resetButton);
    menuPanel.getChildren().add(statusLabel);
    menuPanel.setAlignment(Pos.CENTER);

    HBox mainContent = menuUIService.createMainLayout(leftPanel, menuPanel);
    HBox.setHgrow(leftPanel, Priority.NEVER);
    HBox.setHgrow(menuPanel, Priority.ALWAYS);

    rootLayout.setCenter(mainContent);
    addNavigationAndHelpToBorderPane(rootLayout, false, howToButton);

    scene = new Scene(rootLayout, 1100, 750);
    String cssPath = getStylesheet();
    if (cssPath != null) {
      scene.getStylesheets().add(cssPath);
    }
  }

  /** {@inheritDoc} */
  @Override
  public Scene getScene() {
    return scene;
  }

  /** @return the "Choose players" button */
  public Button choosePlayerButton() {
    return choosePlayerButton;
  }

  /** @return the "Start game" button */
  public Button startButton() {
    return startButton;
  }

  /** @return the "Reset game" button */
  public Button resetButton() {
    return resetButton;
  }
}