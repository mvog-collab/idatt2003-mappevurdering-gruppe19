package edu.ntnu.idatt2003.ui.snl.view;

import edu.ntnu.idatt2003.ui.MenuUIService;
import edu.ntnu.idatt2003.ui.common.view.AbstractMenuView;
import edu.ntnu.idatt2003.utils.Dialogs;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class SnlFrontPage extends AbstractMenuView {
  private Scene scene;
  private final Button chooseBoardButton;
  private final MenuUIService menuUIService;
  private BorderPane rootLayout;
  private ImageView boardPreview;

  public SnlFrontPage() {
    this.menuUIService = new MenuUIService();
    this.rootLayout = new BorderPane(); // Initialize BorderPane
    this.chooseBoardButton = menuUIService.createMenuButton("Choose board", "start-page-button");

    buildUi();
  }

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

    // Add navigation controls to the top of the BorderPane
    addNavigationAndHelpToBorderPane(rootLayout, false, howToButton);

    scene = new Scene(rootLayout, 1100, 750);
    scene.getStylesheets().add(getStylesheet());
  }

  @Override
  public Scene getScene() {
    return scene;
  }

  public void alertUserAboutUnfinishedSetup() {
    Dialogs.warn(
        "Incomplete game setup",
        "Please complete the game setup by selecting a board and adding at least one player before"
            + " starting the game.");
  }

  public void disableChooseBoardButton() {
    chooseBoardButton.setDisable(true);
  }

  public void enableChooseBoardButton() {
    chooseBoardButton.setDisable(false);
  }

  public Button getChooseBoardButton() {
    return chooseBoardButton;
  }

  @Override
  protected void handleGameStarted() {
    super.handleGameStarted();
    enableChooseBoardButton();
    disableChoosePlayerButton();
  }
}
