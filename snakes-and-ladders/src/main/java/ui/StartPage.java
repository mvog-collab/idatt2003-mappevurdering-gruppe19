package ui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import ui.BoardPage;

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

    HBox mainStartPage = new HBox(titleBox, menu);

    Scene scene = new Scene(mainStartPage, 1000, 700);
    primaryStage.setTitle("Snakes and ladders");
    primaryStage.setScene(scene);
    primaryStage.show();
  }




  public static void main(String[] args) {launch(args);}
}
