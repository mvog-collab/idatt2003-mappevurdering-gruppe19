package edu.ntnu.idatt2003.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.VBox;

public class SettingsPage {

  private VBox view;
  private Button loadPlayersButton;
  private Button savePlayersButton;
  private Button backButton;

  public SettingsPage() {
    Label title = new Label("Settings");
    title.getStyleClass().add("settings-title");

    loadPlayersButton = new Button("Load Players from File");
    savePlayersButton = new Button("Save Players to File");
    backButton = new Button("Back");

    VBox buttonsBox = new VBox(10, loadPlayersButton, savePlayersButton, backButton);
    buttonsBox.setAlignment(Pos.CENTER);
    buttonsBox.getStyleClass().add("settings-button-box");

    view = new VBox(20, title, new Separator(), buttonsBox);
    view.setPadding(new Insets(30));
    view.setAlignment(Pos.CENTER);
    view.getStyleClass().add("settings-page");
  }

  public VBox getView() {
    return view;
  }

  public Button getLoadPlayersButton() {
    return loadPlayersButton;
  }

  public Button getSavePlayersButton() {
    return savePlayersButton;
  }

  public Button getBackButton() {
    return backButton;
  }
}
