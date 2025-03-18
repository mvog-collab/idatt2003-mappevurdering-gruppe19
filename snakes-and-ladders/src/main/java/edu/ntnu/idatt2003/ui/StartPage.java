package edu.ntnu.idatt2003.ui;

import edu.ntnu.idatt2003.ui.BoardPage;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
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
    VBox menu = new VBox(startButton, choosePlayerButton, chooseBoardButton);

    startButton.setOnAction(e -> {
      BoardPage gameBoard = new BoardPage();
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
    HBox mainStartPage = new HBox(titleBox, menu);
    mainStartPage.getStyleClass().add("start-page");


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

  public static void main(String[] args) {launch(args);}
}
