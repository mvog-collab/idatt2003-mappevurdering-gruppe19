package edu.ntnu.idatt2003.ui.view;

import edu.ntnu.idatt2003.gateway.GameGateway;
import edu.ntnu.idatt2003.gateway.SnlGatewayFactory;
import edu.ntnu.idatt2003.ui.controller.SnlPageController;
import edu.ntnu.idatt2003.utils.Dialogs;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;

public class SnlPage {

  private Scene scene;
  private Button startButton;
  private Button choosePlayerButton;
  private Button chooseBoardButton;
  private Button resetGameButton;
  private Button settingsButton;
  private ChoosePlayerPage choosePlayerPage;
  private BoardSizePage boardSizePage;

  public SnlPage() {
    buildUI();
  }

  public Scene getScene() {
    return scene;
  }

  private void buildUI() {
    GameGateway gameGateway = SnlGatewayFactory.createDefault();

    Label title = new Label("Snakes & Ladders");
    VBox titleBox = new VBox(title);

    choosePlayerButton = new Button("Choose players");
    disableChoosePlayerButton();
    startButton = new Button("Start game");
    disableStartButton();
    chooseBoardButton = new Button("Choose board");
    settingsButton = new Button();
    settingsButton.setGraphic(new ImageView(new Image("images/settings.png")));
    resetGameButton = new Button("Reset game");

    VBox menu = new VBox(chooseBoardButton, choosePlayerButton, startButton, resetGameButton);
    new SnlPageController(this, gameGateway);

    Image snakeAndLadderImage = new Image(getClass().getResource("/images/snakeAndLadder.png").toExternalForm());
    ImageView imageView = new ImageView(snakeAndLadderImage);
    imageView.setFitWidth(400);
    imageView.setFitHeight(400);
    imageView.setPreserveRatio(true);

    StackPane image = new StackPane(imageView);
    VBox leftSide = new VBox(titleBox, image);

    menu.setSpacing(50);
    menu.getStyleClass().add("menu-start-buttons");
    menu.setAlignment(Pos.CENTER_RIGHT);

    chooseBoardButton.getStyleClass().add("start-page-button");
    startButton.getStyleClass().add("start-page-button");
    choosePlayerButton.getStyleClass().add("start-page-button");
    resetGameButton.getStyleClass().add("exit-button");
    settingsButton.getStyleClass().add("icon-button");

    HBox mainStartPage = new HBox(leftSide, menu);
    mainStartPage.getStyleClass().add("page-background");

    StackPane layout = new StackPane(mainStartPage, settingsButton);
    StackPane.setAlignment(settingsButton, Pos.TOP_RIGHT);
    StackPane.setMargin(settingsButton, new Insets(10));

    title.getStyleClass().add("start-page-title");
    title.setAlignment(Pos.CENTER_LEFT);

    scene = new Scene(layout, 1000, 700);
    scene.getStylesheets().add(getClass().getResource("/styles/style.css").toExternalForm());
  }

  public void alertUserAboutUnfinishedSetup() {
    Dialogs.warn("Incomplete game setup",
        "Please complete the game setup by selecting a board and adding at least one player before starting the game.");
  }

  public void disableStartButton() {
    startButton.setDisable(true);
  }

  public void enableStartButton() {
    startButton.setDisable(false);
  }

  public void disableChoosePlayerButton() {
    choosePlayerButton.setDisable(true);
  }

  public void enableChoosePlayerButton() {
    choosePlayerButton.setDisable(false);
  }

  public void disableChooseBoardButton() {
    chooseBoardButton.setDisable(true);
  }

  public void enableChooseBoardButton() {
    chooseBoardButton.setDisable(false);
  }

  public Button getStartButton() {
    return startButton;
  }

  public Button getChoosePlayerButton() {
    return choosePlayerButton;
  }

  public Button getChooseBoardButton() {
    return chooseBoardButton;
  }

  public Button getSettingsButton() {
    return settingsButton;
  }

  public Button getResetGameButton() {
    return resetGameButton;
  }

  public ChoosePlayerPage getChoosePlayerPage() {
    return choosePlayerPage;
  }

  public BoardSizePage getBoardSizePage() {
    return boardSizePage;
  }
}
