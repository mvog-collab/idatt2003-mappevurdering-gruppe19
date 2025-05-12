package edu.ntnu.idatt2003.ui.snl.view;

import edu.ntnu.idatt2003.ui.MenuUIService;
import edu.ntnu.idatt2003.ui.common.view.AbstractMenuView;
import edu.ntnu.idatt2003.utils.Dialogs;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class SnlPage extends AbstractMenuView {
  // Additional UI components specific to SnL
  private final Scene scene;
  private final Button chooseBoardButton;
  private final Button settingsButton;
  private final MenuUIService menuUIService;

  public SnlPage() {
    // Create service for UI construction
    menuUIService = new MenuUIService();

    // Create UI components using the service
    Label titleLabel = menuUIService.createTitleLabel("Snakes & Ladders", "start-page-title");
    ImageView boardPreview =
        menuUIService.createBoardPreview("/images/snakeAndLadder.png", 400, 400);

    startButton = menuUIService.createMenuButton("Start game", "start-page-button");
    choosePlayerButton = menuUIService.createMenuButton("Choose players", "start-page-button");
    resetButton = menuUIService.createMenuButton("Reset game", "exit-button");
    chooseBoardButton = menuUIService.createMenuButton("Choose board", "start-page-button");
    statusLabel = new Label("Start by choosing a board");
    statusLabel.getStyleClass().add("status-label");

    // Set up settings button with icon
    settingsButton = new Button();
    settingsButton.setGraphic(new ImageView(new Image("images/settings.png")));
    settingsButton.getStyleClass().add("icon-button");

    // Initial button states
    disableStartButton();
    disableChoosePlayerButton();

    // Create layouts using the service
    VBox leftPanel = menuUIService.createLeftPanel(titleLabel, boardPreview);
    VBox menuPanel =
        menuUIService.createMenuPanel(
            chooseBoardButton, choosePlayerButton, startButton, resetButton);
    menuPanel.getChildren().add(statusLabel);

    HBox mainLayout = menuUIService.createMainLayout(leftPanel, menuPanel);

    // Position settings button in top-right corner
    StackPane layout = new StackPane(mainLayout, settingsButton);
    StackPane.setAlignment(settingsButton, Pos.TOP_RIGHT);
    StackPane.setMargin(settingsButton, new Insets(10));

    // Create scene
    scene = new Scene(layout, 1000, 700);
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

  public Button getSettingsButton() {
    return settingsButton;
  }

  @Override
  protected void handleGameStarted() {
    super.handleGameStarted();
    enableChooseBoardButton();
    disableChoosePlayerButton();
  }
}
