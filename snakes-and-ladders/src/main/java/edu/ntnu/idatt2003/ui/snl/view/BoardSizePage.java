package edu.ntnu.idatt2003.ui.snl.view;

import edu.games.engine.observer.BoardGameEvent;
import edu.games.engine.observer.BoardGameObserver;
import edu.ntnu.idatt2003.gateway.GameGateway;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/** View component for board size selection */
public class BoardSizePage implements BoardGameObserver {

  // UI components
  private Button sixtyFourTiles;
  private Button ninetyTiles;
  private Button oneTwentyTiles;
  private Button cancelButton;
  private Button continueButton;
  private Button saveBoardButton;
  private Label statusLabel;

  // Root container for the view
  private VBox root;

  /** Constructor - initializes the UI */
  public BoardSizePage() {
    buildUI();
  }

  /** Connects this view to the model and registers as an observer */
  public void connectToModel(GameGateway gateway) {
    gateway.addObserver(this);
  }

  /** Handles model events */
  @Override
  public void update(BoardGameEvent event) {
    Platform.runLater(
        () -> {
          switch (event.getType()) {
            case GAME_STARTED:
              handleGameStarted(event.getData());
              break;
          }
        });
  }

  /** Updates the UI when a game is started with a specific board size */
  private void handleGameStarted(Object data) {
    if (data instanceof Integer) {
      int boardSize = (Integer) data;
      updateSelectedBoardSize(boardSize);
    }
  }

  /** Updates the UI to highlight the selected board size */
  private void updateSelectedBoardSize(int size) {
    // Reset all button styles
    sixtyFourTiles.getStyleClass().remove("selected-board-size");
    ninetyTiles.getStyleClass().remove("selected-board-size");
    oneTwentyTiles.getStyleClass().remove("selected-board-size");

    // Highlight the selected board size
    switch (size) {
      case 64:
        sixtyFourTiles.getStyleClass().add("selected-board-size");
        break;
      case 90:
        ninetyTiles.getStyleClass().add("selected-board-size");
        break;
      case 120:
        oneTwentyTiles.getStyleClass().add("selected-board-size");
        break;
    }

    // Update status message
    statusLabel.setText("Selected board size: " + size + " tiles");
  }

  /** Constructs the UI components */
  private void buildUI() {
    Label title = new Label("Choose Board");
    title.getStyleClass().add("popup-title");
    title.setAlignment(Pos.CENTER);

    VBox titleBox = new VBox(title);
    titleBox.setAlignment(Pos.CENTER);

    sixtyFourTiles = new Button("64 tiles");
    sixtyFourTiles.getStyleClass().add("board-size-button");

    ninetyTiles = new Button("90 tiles");
    ninetyTiles.getStyleClass().add("board-size-button");

    oneTwentyTiles = new Button("120 tiles");
    oneTwentyTiles.getStyleClass().add("board-size-button");

    cancelButton = new Button("Cancel");
    cancelButton.getStyleClass().add("exit-button");

    continueButton = new Button("Confirm");
    continueButton.getStyleClass().add("confirm-button");

    saveBoardButton = new Button("Save");
    saveBoardButton.getStyleClass().add("board-size-button");

    // Status label
    statusLabel = new Label("Please select a board size");
    statusLabel.getStyleClass().add("status-label");

    // Layout containers
    VBox buttonBox = new VBox(20, sixtyFourTiles, ninetyTiles, oneTwentyTiles, statusLabel);
    buttonBox.setAlignment(Pos.CENTER);

    HBox statusBox = new HBox(60, cancelButton, saveBoardButton, continueButton);
    statusBox.setAlignment(Pos.CENTER);
    statusBox.setPadding(new Insets(40));

    VBox boardPopup = new VBox(10, titleBox, buttonBox, statusBox);

    root = new VBox(70, boardPopup);
    root.setAlignment(Pos.CENTER);
    root.getStyleClass().add("page-background");
    root.getStylesheets().add(getClass().getResource("/styles/style.css").toExternalForm());
  }

  /** Returns the root container of this view */
  public VBox getBoardSizeView() {
    return root;
  }

  // --- Getters and setters ---

  public Button getSixtyFourTiles() {
    return sixtyFourTiles;
  }

  public Button getNinetyTiles() {
    return ninetyTiles;
  }

  public Button getOneTwentyTiles() {
    return oneTwentyTiles;
  }

  public Button getCancelButton() {
    return cancelButton;
  }

  public Button getContinueButton() {
    return continueButton;
  }

  public Button getSaveBoardButton() {
    return saveBoardButton;
  }

  public Label getStatusLabel() {
    return statusLabel;
  }
}
