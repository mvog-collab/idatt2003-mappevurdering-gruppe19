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
import javafx.stage.Stage;

public class StartPage extends Application {

  @Override
  public void start(Stage primaryStage) throws Exception {
    Label title = new Label("Snakes & Ladders");
    VBox titleBox = new VBox(title);

    Button startButton = new Button("Start game");
    Button choosePlayerButton = new Button("Choose players");
    Button chooseBoardButton = new Button("Choose board");
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

    /* Button menu styling */
    menu.setSpacing(50);
    menu.getStyleClass().add("menu-start-buttons");
    menu.setAlignment(Pos.CENTER_RIGHT);

    chooseBoardButton.getStyleClass().add("start-page-button");
    startButton.getStyleClass().add("start-page-button");
    choosePlayerButton.getStyleClass().add("start-page-button");

    /* Main Start page */
    HBox mainStartPage = new HBox(leftSide, menu);
    mainStartPage.getStyleClass().add("start-page");


    /* Label title styling */
    title.getStyleClass().add("start-page-title");
    title.setAlignment(Pos.CENTER_LEFT);

    /* Image SnakeAndLadder */
    image.getStyleClass().add("start-page-image");

    /* Scene */
    Scene scene = new Scene(mainStartPage, 1000, 700);

    scene.getStylesheets().add(getClass().getResource("/styles/style.css").toExternalForm());
    primaryStage.setTitle("Snakes and ladders");
    primaryStage.setScene(scene);
    primaryStage.show();
  }

  public static void main(String[] args) {launch(args);}
}
