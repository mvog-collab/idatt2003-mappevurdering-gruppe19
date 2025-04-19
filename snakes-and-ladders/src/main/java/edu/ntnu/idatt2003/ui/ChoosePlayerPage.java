package edu.ntnu.idatt2003.ui;

import edu.ntnu.idatt2003.models.Player;
import edu.ntnu.idatt2003.models.PlayerTokens;

import java.io.InputStream;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class ChoosePlayerPage {

  //TODO: Choose what to be final and fix setters
  private TextField nameField;
  private DatePicker birthdayPicker;
  private Button cancelButton;
  private Button addPlayerButton;
  private Button continueButton;
  private HBox addedPlayersLine;
  private HBox addedPlayersBox;
  private HBox playerTokensBox;
  private ToggleGroup tokenToggleGroup;
  private Map<PlayerTokens, ToggleButton> tokenButtons = new HashMap<>();

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
    continueButton = new Button("Confirm");

    HBox nameBox = new HBox(playerName, nameField);
    HBox birthdayBox = new HBox(playerBirthday, birthdayPicker);
    addedPlayersBox = new HBox();
    playerTokensBox = new HBox();

    Label addedPlayersLabel = new Label("Added Players");
    addedPlayersLabel.getStyleClass().add("added-players");
    addedPlayersLine = new HBox(addedPlayersLabel, addedPlayersBox);
    addedPlayersLine.setSpacing(20);
    HBox statusBox = new HBox(cancelButton, addPlayerButton, continueButton);

    Label chooseTokenLabel = new Label("Choose player token");
    tokenToggleGroup = new ToggleGroup();

    HBox tokenSelectionBox = new HBox(10);
    tokenSelectionBox.setSpacing(10);
  
  for (PlayerTokens token : PlayerTokens.values()) {
    ToggleButton toggleButton = new ToggleButton();
    InputStream inputStream = getClass().getResourceAsStream(token.getImagePath());
    if (inputStream == null) {
      throw new RuntimeException("Could not load image: " + token.getImagePath());
    }
    ImageView imageView = new ImageView(new Image(inputStream));
    imageView.setFitWidth(50);
    imageView.setFitHeight(50);

    toggleButton.setGraphic(imageView);
    toggleButton.setUserData(token);
    toggleButton.setToggleGroup(tokenToggleGroup);

    tokenButtons.put(token, toggleButton);
    tokenSelectionBox.getChildren().add(toggleButton);
  }

  VBox playerPopup = new VBox(titleBox, nameBox, birthdayBox, chooseTokenLabel, tokenSelectionBox, addedPlayersLine, statusBox);

  VBox background = new VBox(playerPopup);

    /* Title Styling */
    title.getStyleClass().add("popup-title");
    title.setAlignment(Pos.CENTER);
    titleBox.setAlignment(Pos.CENTER);

    /* Player-Info Styling */

    nameBox.setAlignment(Pos.CENTER);
    nameBox.setSpacing(70);
    playerName.getStyleClass().add("popup-label");


    birthdayBox.setAlignment(Pos.CENTER);
    birthdayBox.setSpacing(50);
    playerBirthday.getStyleClass().add("popup-label");

    /* Button Styling */

    addPlayerButton.getStyleClass().add("confirm-button");
    cancelButton.getStyleClass().add("exit-button");
    continueButton.getStyleClass().add("confirm-button");

    statusBox.setAlignment(Pos.CENTER);
    statusBox.setSpacing(70);
    statusBox.setPadding(new Insets(30));
    playerPopup.setAlignment(Pos.CENTER);
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

  public PlayerTokens getSelectedToken() {
    Toggle selected = tokenToggleGroup.getSelectedToggle();
    return selected == null ? null : (PlayerTokens)selected.getUserData();
  }

  public void disableToken(PlayerTokens token) {
    ToggleButton button = tokenButtons.get(token);
    if (button != null) {
      button.setDisable(true);
      if (button.isSelected()) {
        tokenToggleGroup.selectToggle(null);
      }
    }
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

