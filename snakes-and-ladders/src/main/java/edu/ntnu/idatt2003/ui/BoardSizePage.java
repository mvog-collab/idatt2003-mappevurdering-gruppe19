package edu.ntnu.idatt2003.ui;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class BoardSizePage {

  //TODO: Choose what to be final and fix setters
  private Button sixtyFourTiles;
  private Button ninetyTiles;
  private Button oneTwentyTiles;
  private Button cancelButton;
  private Button continueButton;
  
  public VBox getBoardSizeView(){
    Label title = new Label("Choose Board");
    VBox titleBox = new VBox(title);

    sixtyFourTiles = new Button("64 tiles");
    ninetyTiles = new Button("90 tiles");
    oneTwentyTiles = new Button("120 tiles");
    cancelButton = new Button("Cancel");
    continueButton = new Button("Continue");

    VBox buttonBox = new VBox(sixtyFourTiles, ninetyTiles, oneTwentyTiles);

    HBox statusBox = new HBox(cancelButton, continueButton);

    VBox BoardPopup = new VBox(titleBox, buttonBox, statusBox);

    VBox background = new VBox(BoardPopup);

    /* Title Styling */
    title.getStyleClass().add("popup-title");
    title.setAlignment(Pos.CENTER);
    titleBox.setAlignment(Pos.CENTER);

    /* Button Styling */
    sixtyFourTiles.getStyleClass().add("popup-button");
    ninetyTiles.getStyleClass().add("popup-button");
    oneTwentyTiles.getStyleClass().add("popup-button");
    cancelButton.getStyleClass().add("popup-button");
    continueButton.getStyleClass().add("popup-button");

    buttonBox.setAlignment(Pos.CENTER);
    buttonBox.setSpacing(10);
    statusBox.setAlignment(Pos.CENTER);
    statusBox.setSpacing(70);
    statusBox.setPadding(new Insets(40));

    background.setAlignment(Pos.CENTER);
    background.setSpacing(70);

    background.getStyleClass().add("page-background");

    background.getStylesheets().add(getClass().getResource("/styles/style.css").toExternalForm());
    
    return background;
  }

  public Button getSixtyFourTiles() {
    return sixtyFourTiles;
  }

  public void setSixtyFourTiles(Button sixtyTiles) {
    this.sixtyFourTiles = sixtyTiles;
  }

  public Button getNinetyTiles() {
    return ninetyTiles;
  }

  public void setNinetyTiles(Button ninetyTiles) {
    this.ninetyTiles = ninetyTiles;
  }

  public Button getOneTwentyTiles() {
    return oneTwentyTiles;
  }

  public void setOneTwentyTiles(Button oneTwentyTiles) {
    this.oneTwentyTiles = oneTwentyTiles;
  }

  public Button getCancelButton() {
    return cancelButton;
  }

  public void setCancelButton(Button cancelButton) {
    this.cancelButton = cancelButton;
  }

  public Button getContinueButton() {
    return continueButton;
  }

  public void setContinueButton(Button continueButton) {
    this.continueButton = continueButton;
  }
}
