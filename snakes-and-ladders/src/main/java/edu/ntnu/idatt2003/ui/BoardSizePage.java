package edu.ntnu.idatt2003.ui;

import javafx.application.Application;
import javafx.geometry.Pos;
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

    VBox buttonBox = new VBox(sixtyTiles, ninetyTiles, oneTwentyTiles);

    HBox statusBox = new HBox(cancelButton, continueButton);

    VBox background = new VBox(titleBox, buttonBox, statusBox);

    VBox popup = new VBox(background);

    /* Title Styling */
    title.getStyleClass().add("popup-title");
    title.setAlignment(Pos.CENTER);
    titleBox.setAlignment(Pos.CENTER);

    /* Button Styling */
    sixtyTiles.getStyleClass().add("popup-button");
    ninetyTiles.getStyleClass().add("popup-button");
    oneTwentyTiles.getStyleClass().add("popup-button");
    cancelButton.getStyleClass().add("popup-button");
    continueButton.getStyleClass().add("popup-button");

    buttonBox.setAlignment(Pos.CENTER);
    buttonBox.setSpacing(10);
    statusBox.setAlignment(Pos.CENTER);
    statusBox.setSpacing(70);
    popup.setAlignment(Pos.CENTER);
    popup.setSpacing(70);

    popup.getStyleClass().add("popup-background");


    Scene scene = new Scene(popup, 500, 350);

    scene.getStylesheets().add(getClass().getResource("/styles/style.css").toExternalForm());
    primaryStage.setScene(scene);
    primaryStage.show();
  }
}
