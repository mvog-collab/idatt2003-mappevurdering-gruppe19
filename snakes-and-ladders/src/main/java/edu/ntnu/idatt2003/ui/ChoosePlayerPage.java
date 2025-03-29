package edu.ntnu.idatt2003.ui;

import java.time.LocalDate;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class ChoosePlayerPage {

  //TODO: Choose what to be final and fix setters
  private TextField nameField;
  private DatePicker birthdayPicker;
  private Button cancelButton;
  private Button addPlayerButton;
  private Button continueButton;

  public VBox getView(){
    Label title = new Label("Add Players");
    VBox titleBox = new VBox(title);

    Label playerName = new Label("Player Name");
    nameField = new TextField();
    nameField.setPromptText("Enter name");

    Label playerBirthday = new Label("Player Birthday");
    birthdayPicker = new DatePicker();
    birthdayPicker.setPromptText("Select your birthday");

    addPlayerButton = new Button("Add Player");
    cancelButton = new Button("Cancel");
    continueButton = new Button("Continue");

    HBox nameBox = new HBox(playerName, nameField);
    HBox birthdayBox = new HBox(playerBirthday, birthdayPicker);

    HBox statusBox = new HBox(cancelButton, addPlayerButton, continueButton);

    VBox playerPopup = new VBox(titleBox, nameBox, birthdayBox, statusBox);

    VBox background = new VBox(playerPopup);

    /* Title Styling */
    title.getStyleClass().add("popup-title");
    title.setAlignment(Pos.CENTER);
    titleBox.setAlignment(Pos.CENTER);

    /* Player-Info Styling */

    nameBox.setAlignment(Pos.CENTER_LEFT);
    nameBox.setSpacing(30);
    playerName.getStyleClass().add("popup-label");

    birthdayBox.setAlignment(Pos.CENTER_LEFT);
    playerBirthday.getStyleClass().add("popup-label");

    /* Button Styling */

    addPlayerButton.getStyleClass().add("popup-button");
    cancelButton.getStyleClass().add("popup-button");
    continueButton.getStyleClass().add("popup-button");

    statusBox.setAlignment(Pos.CENTER);
    statusBox.setSpacing(70);
    statusBox.setPadding(new Insets(40));
    background.setAlignment(Pos.CENTER);
    background.setSpacing(70);

    playerPopup.setSpacing(20);


    /* Background Styling */

    background.getStyleClass().add("popup-background");

    background.getStylesheets().add(getClass().getResource("/styles/style.css").toExternalForm());
    return background;
  }

  public TextField getNameField() {
    return nameField;
  }

  public void setNameField(TextField nameField) {
    this.nameField = nameField;
  }

  public DatePicker getBirthdayPicker() {
    return birthdayPicker;
  }

  public void setBirthdayPicker(DatePicker birthdayPicker) {
    this.birthdayPicker = birthdayPicker;
  }

  public Button getCancelButton() {
    return cancelButton;
  }

  public void setCancelButton(Button cancelButton) {
    this.cancelButton = cancelButton;
  }

  public Button getAddPlayerButton() {
    return addPlayerButton;
  }

  public void setAddPlayerButton(Button addPlayerButton) {
    this.addPlayerButton = addPlayerButton;
  }

  public Button getContinueButton() {
    return continueButton;
  }

  public void setContinueButton(Button continueButton) {
    this.continueButton = continueButton;
  }
}

