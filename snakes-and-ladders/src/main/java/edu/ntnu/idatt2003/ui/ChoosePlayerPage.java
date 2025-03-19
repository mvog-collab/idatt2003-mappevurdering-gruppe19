package edu.ntnu.idatt2003.ui;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ChoosePlayerPage extends Application {


    @Override
    public void start(Stage primaryStage){
      Label title = new Label("Add Players");
      VBox titleBox = new VBox(title);

      Label playerName = new Label("Player Name");
      TextField nameField = new TextField();
      nameField.setPromptText("Enter name");

      Label playerBirthday = new Label("Player Birthday");
      DatePicker birthdayPicker = new DatePicker();
      birthdayPicker.setPromptText("Select your birthday");

      Button addPlayerButton = new Button("Add Player");
      Button cancelButton = new Button("Cancel");
      Button continueButton = new Button("Continue");

      HBox nameBox = new HBox(playerName, nameField);
      HBox birthdayBox = new HBox(playerBirthday, birthdayPicker);

      HBox statusBox = new HBox(cancelButton, addPlayerButton, continueButton);

      VBox background = new VBox(titleBox, nameBox, birthdayBox, statusBox);

      VBox popup = new VBox(background);

      /* Title Styling */
      title.getStyleClass().add("popup-title");
      title.setAlignment(Pos.CENTER);
      titleBox.setAlignment(Pos.CENTER);

      /* Button Styling */

      addPlayerButton.getStyleClass().add("popup-button");
      cancelButton.getStyleClass().add("board-size-button");
      continueButton.getStyleClass().add("board-size-button");

      statusBox.setAlignment(Pos.CENTER);
      statusBox.setSpacing(70);
      popup.setAlignment(Pos.CENTER);
      popup.setSpacing(70);

      popup.getStyleClass().add("board-size-popup");


      Scene scene = new Scene(popup, 500, 350);

      scene.getStylesheets().add(getClass().getResource("/styles/style.css").toExternalForm());
      primaryStage.setScene(scene);
      primaryStage.show();
    }
  }

