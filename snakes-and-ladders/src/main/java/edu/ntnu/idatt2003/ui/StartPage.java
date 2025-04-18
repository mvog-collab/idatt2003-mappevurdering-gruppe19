package edu.ntnu.idatt2003.ui;

import java.time.LocalDate;

import edu.ntnu.idatt2003.controllers.ChoosePlayerController;
import edu.ntnu.idatt2003.controllers.StartPageController;
import edu.ntnu.idatt2003.models.GameModel;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class StartPage extends Application {

  private Button startButton;
  private Button choosePlayerButton;
  private Button chooseBoardButton;
  private Button resetGameButton;
  private ChoosePlayerPage choosePlayerPage;
  private BoardSizePage boardSizePage;

  @Override
  public void start(Stage primaryStage) throws Exception {
    Label title = new Label("Snakes & Ladders");
    VBox titleBox = new VBox(title);

    choosePlayerButton = new Button("Choose players");
    disableChoosePlayerButton();
    startButton = new Button("Start game");
    disableStartButton();
    chooseBoardButton = new Button("Choose board");
    resetGameButton = new Button("Reset game");
    VBox menu = new VBox(chooseBoardButton, choosePlayerButton, startButton, resetGameButton);

    StartPageController startPageController = new StartPageController(this);

    Image snakeAndLadderImage = new Image(getClass().getResource("/images/snakeAndLadder.png").toExternalForm());
    ImageView imageView = new ImageView(snakeAndLadderImage);
    imageView.setFitWidth(400);
    imageView.setFitHeight(400);
    imageView.setPreserveRatio(true);

    StackPane image = new StackPane(imageView);

    VBox leftSide = new VBox(titleBox, image);

    /* Button menu styling */
    menu.setSpacing(50);
    menu.getStyleClass().add("menu-start-buttons");
    menu.setAlignment(Pos.CENTER_RIGHT);

    chooseBoardButton.getStyleClass().add("start-page-button");
    startButton.getStyleClass().add("start-page-button");
    choosePlayerButton.getStyleClass().add("start-page-button");
    resetGameButton.getStyleClass().add("start-page-button");

    /* Main Start page */
    HBox mainStartPage = new HBox(leftSide, menu);
    mainStartPage.getStyleClass().add("page-background");


    /* Label title styling */
    title.getStyleClass().add("start-page-title");
    title.setAlignment(Pos.CENTER_LEFT);

    /* Scene */
    Scene scene = new Scene(mainStartPage, 1000, 700);

    scene.getStylesheets().add(getClass().getResource("/styles/style.css").toExternalForm());
    primaryStage.setTitle("Snakes and ladders");
    primaryStage.setScene(scene);
    primaryStage.show();
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
  
  public ChoosePlayerPage getChoosePlayerPage() {
    return choosePlayerPage;
  }
  
  public BoardSizePage getBoardSizePage() {
    return boardSizePage;
  }
  
  public static void main(String[] args) {launch(args);}
}
