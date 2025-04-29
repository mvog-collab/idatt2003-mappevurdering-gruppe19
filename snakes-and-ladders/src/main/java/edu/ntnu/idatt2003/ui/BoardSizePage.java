package edu.ntnu.idatt2003.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class BoardSizePage {

  //TODO: Choose what to be final and fix setters
  private Button sixtyFourTiles;
  private Button ninetyTiles;
  private Button oneTwentyTiles;
  private Button cancelButton;
  private Button continueButton;
  private Button saveBoardButton;
  
  public VBox getBoardSizeView(){
    Label title = new Label("Choose Board");
    VBox titleBox = new VBox(title);

    sixtyFourTiles = new Button("64 tiles");
    ninetyTiles = new Button("90 tiles");
    oneTwentyTiles = new Button("120 tiles");
    cancelButton = new Button("Cancel");
    continueButton = new Button("Confirm");
    saveBoardButton = new Button("Save");


    VBox buttonBox = new VBox(20, sixtyFourTiles, ninetyTiles, oneTwentyTiles);

    HBox statusBox = new HBox(60, cancelButton, saveBoardButton, continueButton);

    VBox boardPopup = new VBox(10, titleBox, buttonBox, statusBox);

    VBox root = new VBox(70, boardPopup);

    /* Title Styling */
    title.getStyleClass().add("popup-title");
    title.setAlignment(Pos.CENTER);
    titleBox.setAlignment(Pos.CENTER);

    /* Button Styling */
    sixtyFourTiles.getStyleClass().add("board-size-button");
    ninetyTiles.getStyleClass().add("board-size-button");
    oneTwentyTiles.getStyleClass().add("board-size-button");
    cancelButton.getStyleClass().add("exit-button");
    saveBoardButton.getStyleClass().add("board-size-button");
    continueButton.getStyleClass().add("confirm-button");

    buttonBox.setAlignment(Pos.CENTER);
    statusBox.setAlignment(Pos.CENTER);
    statusBox.setPadding(new Insets(40));
    root.setAlignment(Pos.CENTER);

    root.getStyleClass().add("page-background");

    root.getStylesheets().add(getClass().getResource("/styles/style.css").toExternalForm());
    
    return root;
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
