package edu.ntnu.idatt2003.ui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class BoardSizePage extends Application {

  @Override
  public void start(Stage primaryStage){
    Label title = new Label("Choose Board");
    VBox titleBox = new VBox(title);

    Button sixtyTiles = new Button("60 tiles");
    Button ninetyTiles = new Button("90 tiles");
    Button oneTwentyTiles = new Button("120 tiles");
    Button cancelButton = new Button("Cancel");
    Button continueButton = new Button("Continue");

    HBox buttonBox = new HBox(sixtyTiles, ninetyTiles, oneTwentyTiles);

    VBox statusBox = new VBox(cancelButton, continueButton);

    HBox background = new HBox(titleBox, buttonBox, statusBox);

    VBox popup = new VBox(background);

    Scene scene = new Scene(popup, 500, 350);

    scene.getStylesheets().add(getClass().getResource("/styles/style.css").toExternalForm());
    primaryStage.setScene(scene);
    primaryStage.show();
  }
}
