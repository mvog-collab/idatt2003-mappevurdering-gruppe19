package edu.ntnu.idatt2003.ui;

import edu.ntnu.idatt2003.models.Player;
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
  private HBox addedPlayersBox;

  public VBox getView(){
    Label title = new Label("Add Players");
    VBox titleBox = new VBox(title);

    Label playerName = new Label("Player Name");
    nameField = new TextField();
    nameField.setPromptText("Enter name");

    Label playerBirthday = new Label("Player Birthday");
    birthdayPicker = new DatePicker();
    birthdayPicker.setValue(LocalDate.of(2001,1,1));
    birthdayPicker.getStyleClass().add("date-picker");
    birthdayPicker.setPromptText("Select your birthday");

    addPlayerButton = new Button("Add Player");
    cancelButton = new Button("Cancel");
    continueButton = new Button("Continue");

    HBox nameBox = new HBox(playerName, nameField);
    HBox birthdayBox = new HBox(playerBirthday, birthdayPicker);

    Label addedPlayersLabel = new Label("Added Players");
    addedPlayersLabel.getStyleClass().add("added-players");
    addedPlayersBox = new HBox(addedPlayersLabel);

    HBox statusBox = new HBox(cancelButton, addPlayerButton, continueButton);

    VBox playerPopup = new VBox(titleBox, nameBox, birthdayBox, addedPlayersBox, statusBox);

    VBox background = new VBox(playerPopup);



    /* Title Styling */
    title.getStyleClass().add("popup-title");
    title.setAlignment(Pos.CENTER);
    titleBox.setAlignment(Pos.CENTER);

    /* Player-Info Styling */

    nameBox.setAlignment(Pos.CENTER_LEFT);
    nameBox.setSpacing(70);
    playerName.getStyleClass().add("popup-label");


    birthdayBox.setAlignment(Pos.CENTER_LEFT);
    birthdayBox.setSpacing(50);
    playerBirthday.getStyleClass().add("popup-label");

    /* Button Styling */

    addPlayerButton.getStyleClass().add("popup-button");
    cancelButton.getStyleClass().add("popup-button");
    continueButton.getStyleClass().add("popup-button");

    statusBox.setAlignment(Pos.CENTER);
    statusBox.setSpacing(70);
    statusBox.setPadding(new Insets(30));
    background.setAlignment(Pos.CENTER);
    background.setSpacing(70);

    playerPopup.setSpacing(10);

    addedPlayersBox.setSpacing(30);

    /* Background Styling */

    background.getStyleClass().add("page-background");

    background.getStylesheets().add(getClass().getResource("/styles/style.css").toExternalForm());
    return background;
  }

  public VBox displayPlayer(Player player) {
    VBox playerName = new VBox(new Label(player.getName()));
    playerName.getStyleClass().add("player-name");
    playerName.setAlignment(Pos.CENTER);
  return playerName;
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

  public HBox getAddedPlayersBox() {
    return addedPlayersBox;
  }
}

