package edu.ntnu.idatt2003.ui.ludo.view;

import edu.ntnu.idatt2003.ui.MenuUIService;
import edu.ntnu.idatt2003.ui.common.view.AbstractMenuView;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class LudoPage extends AbstractMenuView {
  private Scene scene;
  private final MenuUIService menuUIService;
  private BorderPane rootLayout;

  public LudoPage() {
    this.menuUIService = new MenuUIService();
    this.rootLayout = new BorderPane();
    buildUi();
  }

  private void buildUi() {

    Label titleLabel = menuUIService.createTitleLabel("Ludo", "ludo-page-title");
    ImageView boardPreview = menuUIService.createBoardPreview("/images/ludoBoard.jpg", 350, 350);

    startButton = menuUIService.createMenuButton("Start game", "confirm-button");
    choosePlayerButton = menuUIService.createMenuButton("Choose players", "choose-button");
    resetButton = menuUIService.createMenuButton("Reset game", "exit-button");
    statusLabel = new Label("Start by choosing players");
    statusLabel.getStyleClass().add("status-label");
    startButton.setDisable(true);

    Button howToButton =
        createHowToPlayButton(
            "How to play - Ludo",
            """
            - Throw a 6 to get a piece out of your home.
            - If you land on an enemy player(s), they are sent back to their home.
            - The first player with all pieces in the goal is the winner!
            """);

    VBox leftPanel = menuUIService.createLeftPanel(titleLabel, boardPreview);
    VBox menuPanel =
        menuUIService.createMenuPanel(howToButton, choosePlayerButton, startButton, resetButton);
    menuPanel.getChildren().add(statusLabel);
    HBox mainContent = menuUIService.createMainLayout(leftPanel, menuPanel);

    rootLayout.setCenter(mainContent);

    addNavigationAndHelpToBorderPane(rootLayout, false, howToButton);

    scene = new Scene(rootLayout, 1000, 700);
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
