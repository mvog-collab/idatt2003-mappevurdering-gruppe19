package edu.ntnu.idatt2003.presentation.snl.view;

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

/**
 * View component for selecting the board size in Snakes and Ladders.
 * Presents three board size options (64, 90, or 120 tiles) and allows
 * the user to choose which one they want to play on. Updates the UI
 * to highlight the selected board size and provides feedback to the user.
 */
public class SnlBoardSizePage implements BoardGameObserver {

  // UI components
  private Button sixtyFourTiles;
  private Button ninetyTiles;
  private Button oneTwentyTiles;
  private Button cancelButton;
  private Button continueButton;
  private Label statusLabel;
  private VBox root;

  private static final String SELECTED_BOARD_BUTTON = "selected-board-size";
  private static final String BOARD_SIZE_BUTTON = "board-size-button";

  /**
   * Creates a new board size selection page.
   * Initializes all UI components and sets up the layout.
   */
  public SnlBoardSizePage() {
    buildUI();
  }

  /**
   * Connects this view to the game model and registers as an observer.
   * This allows the view to receive updates when game events occur,
   * like when a board size is selected and a game is started.
   *
   * @param gateway the game gateway to observe
   */
  public void connectToModel(GameGateway gateway) {
    gateway.addObserver(this);
  }

  /**
   * Handles events from the game model.
   * Currently responds to GAME_STARTED events to update the UI
   * with the selected board size.
   *
   * @param event the board game event that occurred
   */
  @Override
  public void update(BoardGameEvent event) {
    Platform.runLater(
        () -> {
          switch (event.getTypeOfEvent()) {
            case GAME_STARTED:
              handleGameStarted(event.getData());
              break;
          }
        });
  }

  /**
   * Updates the UI when a game is started with a specific board size.
   * Extracts the board size from the event data and highlights the
   * corresponding button in the interface.
   *
   * @param data the event data containing the board size
   */
  private void handleGameStarted(Object data) {
    if (data instanceof Integer) {
      int boardSize = (Integer) data;
      updateSelectedBoardSize(boardSize);
    }
  }

  /**
   * Updates the UI to highlight the selected board size.
   * Removes highlighting from all buttons, then adds it to the selected one.
   * Also updates the status message to show which size was chosen.
   *
   * @param size the selected board size (64, 90, or 120)
   */
  private void updateSelectedBoardSize(int size) {
    sixtyFourTiles.getStyleClass().remove(SELECTED_BOARD_BUTTON);
    ninetyTiles.getStyleClass().remove(SELECTED_BOARD_BUTTON);
    oneTwentyTiles.getStyleClass().remove(SELECTED_BOARD_BUTTON);

    switch (size) {
      case 64:
        sixtyFourTiles.getStyleClass().add(SELECTED_BOARD_BUTTON);
        break;
      case 90:
        ninetyTiles.getStyleClass().add(SELECTED_BOARD_BUTTON);
        break;
      case 120:
        oneTwentyTiles.getStyleClass().add(SELECTED_BOARD_BUTTON);
        break;
    }

    statusLabel.setText("Selected board size: " + size + " tiles");
  }

  /**
   * Constructs all the UI components and arranges them in the layout.
   * Creates the title, board size buttons, control buttons, status label,
   * and organizes them in containers with proper spacing and styling.
   */
  private void buildUI() {
    Label title = new Label("Choose Board");
    title.getStyleClass().add("popup-title");
    title.setAlignment(Pos.CENTER);

    VBox titleBox = new VBox(title);
    titleBox.setAlignment(Pos.CENTER);

    sixtyFourTiles = new Button("64 tiles");
    sixtyFourTiles.getStyleClass().add(BOARD_SIZE_BUTTON);

    ninetyTiles = new Button("90 tiles");
    ninetyTiles.getStyleClass().add(BOARD_SIZE_BUTTON);

    oneTwentyTiles = new Button("120 tiles");
    oneTwentyTiles.getStyleClass().add(BOARD_SIZE_BUTTON);

    cancelButton = new Button("Cancel");
    cancelButton.getStyleClass().add("exit-button");

    continueButton = new Button("Confirm");
    continueButton.getStyleClass().add("confirm-button");

    statusLabel = new Label("Please select a board size");
    statusLabel.getStyleClass().add("status-label");

    VBox buttonBox = new VBox(20, sixtyFourTiles, ninetyTiles, oneTwentyTiles, statusLabel);
    buttonBox.setAlignment(Pos.CENTER);

    HBox statusBox = new HBox(60, cancelButton, continueButton);
    statusBox.setAlignment(Pos.CENTER);
    statusBox.setPadding(new Insets(40));

    VBox boardPopup = new VBox(10, titleBox, buttonBox, statusBox);
    boardPopup.setPadding(new Insets(20));

    root = new VBox(70, boardPopup);
    root.setAlignment(Pos.CENTER);

    root.getStyleClass().add("page-background");
    root.getStylesheets().add(getClass().getResource("/styles/style.css").toExternalForm());
    root.setPadding(new Insets(30, 20, 30, 20));
  }

  /**
   * Returns the root container of this view.
   * This is what should be added to the scene or parent container
   * to display the board size selection interface.
   *
   * @return the root VBox containing all UI elements
   */
  public VBox getBoardSizeView() {
    return root;
  }

  // --- Getters for UI components ---

  /**
   * Gets the 64 tiles board size button.
   * 
   * @return the button for selecting 64-tile board
   */
  public Button getSixtyFourTiles() {
    return sixtyFourTiles;
  }

  /**
   * Gets the 90 tiles board size button.
   * 
   * @return the button for selecting 90-tile board
   */
  public Button getNinetyTiles() {
    return ninetyTiles;
  }

  /**
   * Gets the 120 tiles board size button.
   * 
   * @return the button for selecting 120-tile board
   */
  public Button getOneTwentyTiles() {
    return oneTwentyTiles;
  }

  /**
   * Gets the cancel button.
   * 
   * @return the button for canceling board size selection
   */
  public Button getCancelButton() {
    return cancelButton;
  }

  /**
   * Gets the continue/confirm button.
   * 
   * @return the button for confirming the selected board size
   */
  public Button getContinueButton() {
    return continueButton;
  }

  /**
   * Gets the status label that shows feedback to the user.
   * 
   * @return the label displaying current selection status
   */
  public Label getStatusLabel() {
    return statusLabel;
  }
}