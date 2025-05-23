package edu.ntnu.idatt2003.presentation.snl.view;

import edu.ntnu.idatt2003.presentation.MenuUIService;
import edu.ntnu.idatt2003.presentation.common.view.AbstractMenuView;
import edu.ntnu.idatt2003.utils.Dialogs;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * The main menu and front page for the Snakes and Ladders game.
 * Provides the initial interface where players can set up their game by
 * choosing
 * a board, selecting players, and starting the game. Shows game rules and
 * manages the setup flow to ensure everything is configured before starting.
 */
public class SnlFrontPage extends AbstractMenuView {
  private Scene scene;
  private final Button chooseBoardButton;
  private final MenuUIService menuUIService;
  private BorderPane rootLayout;
  private ImageView boardPreview;

  /**
   * Creates the SNL front page with all UI components and layout.
   * Sets up the menu buttons, board preview, and navigation elements.
   * Initializes the page in a state where users need to choose a board first.
   */
  public SnlFrontPage() {
    this.menuUIService = new MenuUIService();
    this.rootLayout = new BorderPane();
    this.chooseBoardButton = menuUIService.createMenuButton("Choose board", "start-page-button");

    buildUi();
  }

  /**
   * Constructs the entire UI layout for the front page.
   * Creates the title, board preview, action buttons, and help information.
   * Sets up the scene with proper styling and arranges all components
   * in a user-friendly layout with the board preview on the left and controls on
   * the right.
   */
  private void buildUi() {
    Label titleLabel = menuUIService.createTitleLabel("Snakes & Ladders", "start-page-title");
    titleLabel.setWrapText(true);
    boardPreview = menuUIService.createBoardPreview("/images/snakeAndLadder.png", 350, 350);
    boardPreview.setPreserveRatio(true);

    startButton = menuUIService.createMenuButton("Start game", "confirm-button");
    choosePlayerButton = menuUIService.createMenuButton("Choose players", "start-page-button");
    resetButton = menuUIService.createMenuButton("Reset game", "exit-button");

    Button howToButton = createHowToPlayButton(
        "How to play - Snakes & Ladders",
        """
            - Roll the dice and be the first to the goal!
            - If you land on an enemy player(s), they are sent back to start.
            - Roll a pair for an extra turn.
            - If you roll a pair of six, your turn is skipped!
            - Be aware of the snakes and go for the ladders!
            """);
    statusLabel = new Label("Start by choosing a board");
    statusLabel.getStyleClass().add("status-label");
    statusLabel.setWrapText(true);

    disableStartButton();
    disableChoosePlayerButton();

    VBox leftPanel = menuUIService.createLeftPanel(titleLabel, boardPreview);
    VBox menuPanel = menuUIService.createMenuPanel(
        chooseBoardButton, choosePlayerButton, startButton, resetButton);
    menuPanel.getChildren().add(statusLabel);
    HBox mainContent = menuUIService.createMainLayout(leftPanel, menuPanel);
    mainContent.setAlignment(Pos.TOP_CENTER);
    leftPanel.setTranslateY(-50);

    rootLayout.setCenter(mainContent);

    addNavigationAndHelpToBorderPane(rootLayout, false, howToButton);

    scene = new Scene(rootLayout, 1100, 750);
    scene.getStylesheets().add(getStylesheet());
  }

  /**
   * Gets the JavaFX scene for this front page.
   * Used by the application to display this view in the main window.
   *
   * @return the configured scene with all UI components
   */
  @Override
  public Scene getScene() {
    return scene;
  }

  /**
   * Shows a warning dialog when the user tries to start an incomplete game.
   * Alerts the user that they need to choose a board and add players
   * before the game can begin. Helps guide users through the setup process.
   */
  public void alertUserAboutUnfinishedSetup() {
    Dialogs.warn(
        "Incomplete game setup",
        "Please complete the game setup by selecting a board and adding at least one player before"
            + " starting the game.");
  }

  /**
   * Disables the "Choose board" button.
   * Used when board selection should be temporarily unavailable,
   * such as when a game is already in progress.
   */
  public void disableChooseBoardButton() {
    chooseBoardButton.setDisable(true);
  }

  /**
   * Enables the "Choose board" button.
   * Allows users to select or change the game board.
   */
  public void enableChooseBoardButton() {
    chooseBoardButton.setDisable(false);
  }

  /**
   * Gets the board selection button for external event handling.
   * Allows other components to attach click handlers to the choose board button.
   *
   * @return the button used for board selection
   */
  public Button getChooseBoardButton() {
    return chooseBoardButton;
  }

  /**
   * Handles the transition when a game is started.
   * Re-enables the board selection (in case users want to change boards later)
   * and disables player selection while a game is active.
   * Inherits additional start-game behavior from the parent class.
   */
  @Override
  protected void handleGameStarted() {
    super.handleGameStarted();
    enableChooseBoardButton();
    disableChoosePlayerButton();
  }
}