package edu.ntnu.idatt2003.ui;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
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

  @Override
  public void start(Stage primaryStage) throws Exception {
    Label title = new Label("Snakes & Ladders");
    VBox titleBox = new VBox(title);

    startButton = new Button("Start game");
    disableStartButton();
    chooseBoardButton = new Button("Choose board");
    disableChooseBoardButton();
    choosePlayerButton = new Button("Choose players");
    VBox menu = new VBox(choosePlayerButton, chooseBoardButton, startButton);

    Image snakeAndLadderImage = new Image(getClass().getResource("/images/snakeAndLadder.png").toExternalForm());
    ImageView imageView = new ImageView(snakeAndLadderImage);
    imageView.setFitWidth(400);
    imageView.setFitHeight(400);
    imageView.setPreserveRatio(true);

    StackPane image = new StackPane(imageView);

    VBox leftSide = new VBox(titleBox, image);

    startButton.setOnAction(e -> {
      BoardView gameBoard = new BoardView();
      gameBoard.start(primaryStage);
    });

    choosePlayerButton.setOnAction(e -> {
      Stage choosePlayerPopup = new Stage();
      choosePlayerPopup.initModality(Modality.APPLICATION_MODAL);
      choosePlayerPopup.setTitle("Choose players");

      ChoosePlayerPage choosePlayerPage = new ChoosePlayerPage();

      Scene scene = new Scene(choosePlayerPage.getView(), 500, 350);
      choosePlayerPopup.setScene(scene);
      choosePlayerPopup.showAndWait();
      enableChooseBoardButton();
    });

    chooseBoardButton.setOnAction(e -> {
      Stage chooseBoardPopup = new Stage();
      chooseBoardPopup.initModality(Modality.APPLICATION_MODAL);
      chooseBoardPopup.setTitle("Choose board size");

      BoardSizePage boardSizePage = new BoardSizePage();

      Scene scene = new Scene(boardSizePage.getBoardSizeView(), 500, 350);
      chooseBoardPopup.setScene(scene);
      chooseBoardPopup.showAndWait();
      enableStartButton();
    });

    /* Button menu styling */
    menu.setSpacing(50);
    menu.getStyleClass().add("menu-start-buttons");
    menu.setAlignment(Pos.CENTER_RIGHT);

    chooseBoardButton.getStyleClass().add("start-page-button");
    startButton.getStyleClass().add("start-page-button");
    choosePlayerButton.getStyleClass().add("start-page-button");

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

  private void disableStartButton() {
    startButton.setDisable(true);
  }

  private void enableStartButton() {
      startButton.setDisable(false);
  }

  private void disableChoosePlayerButton() {
    choosePlayerButton.setDisable(true);
  }

  private void enableChoosePlayerButton() {
      choosePlayerButton.setDisable(false);
  }

  private void disableChooseBoardButton() {
    chooseBoardButton.setDisable(true);
  }

  private void enableChooseBoardButton() {
    chooseBoardButton.setDisable(false);
  }

  public static void main(String[] args) {launch(args);}
}
