package edu.ntnu.idatt2003.presentation.shared.view;

import edu.games.engine.observer.BoardGameEvent;
import edu.games.engine.observer.BoardGameObserver;
import edu.ntnu.idatt2003.gateway.CompleteBoardGame;
import edu.ntnu.idatt2003.gateway.view.PlayerView;
import edu.ntnu.idatt2003.utils.ResourcePaths;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;

/**
 * JavaFX view for player selection and management interface.
 * <p>
 * Provides UI elements for adding players, selecting tokens, managing
 * birthdays, and displaying current player roster. Observes game
 * events to update the display automatically.
 * </p>
 */
public class ChoosePlayerPage implements BoardGameObserver {

  private static final Logger LOG = Logger.getLogger(ChoosePlayerPage.class.getName());
  private final VBox root;
  private final Map<String, ToggleButton> tokenButtons = new HashMap<>();
  private final String[] tokenNames;

  // UI Components
  private TextField nameField;
  private DatePicker birthdayPicker;
  private Button cancelButton;
  private Button addPlayerButton;
  private Button savePlayerButton;
  private Button loadPlayersButton;
  private Button continueButton;
  private FlowPane addedPlayersBox;
  private ToggleGroup tokenToggleGroup;

  /** Connected game gateway */
  private CompleteBoardGame gameGateway;

  /**
   * Constructs a new ChoosePlayerPage with the specified token names.
   *
   * @param tokenNames array of available token names for player selection
   */
  public ChoosePlayerPage(String[] tokenNames) {
    this.tokenNames = tokenNames;
    this.root = new VBox();
    buildUI();
  }

  /**
   * Constructs a new ChoosePlayerPage with default token names.
   */
  public ChoosePlayerPage() {
    this(new String[] { "BLUE", "GREEN", "YELLOW", "RED", "PURPLE" });
  }

  /**
   * Connects this view to the specified game gateway.
   *
   * @param gateway the game gateway to observe for updates
   */
  public void connectToModel(CompleteBoardGame gateway) {
    this.gameGateway = gateway;
    gateway.addObserver(this);
  }

  /**
   * Handles game events and updates the UI accordingly.
   *
   * @param event the board game event to process
   */
  @Override
  public void update(BoardGameEvent event) {
    Platform.runLater(
        () -> {
          switch (event.getTypeOfEvent()) {
            case PLAYER_ADDED:
              LOG.fine("Player added event received, updating display.");
              handlePlayerAdded();
              break;
            case GAME_RESET:
              LOG.fine("Game reset event received, resetting player UI.");
              handleGameReset();
              break;
            default:
              LOG.finest("Received unhandled event type: " + event.getTypeOfEvent());
              break;
          }
        });
  }

  /**
   * Handles player added events by updating the display.
   */
  private void handlePlayerAdded() {
    if (gameGateway != null) {
      updatePlayerDisplay(gameGateway.players());
    }
  }

  /**
   * Handles game reset events by clearing the UI state.
   */
  private void handleGameReset() {
    addedPlayersBox.getChildren().clear();
    tokenButtons.values().forEach(tb -> tb.setDisable(false));
  }

  /**
   * Updates the player display with the provided list of players.
   *
   * @param players list of players to display
   */
  public void updatePlayerDisplay(List<PlayerView> players) {
    List<PlayerView> sorted = players.stream().sorted(Comparator.comparing(PlayerView::birthday).reversed()).toList();

    addedPlayersBox.getChildren().clear();

    for (PlayerView pv : sorted) {
      VBox playerBox = createPlayerBox(pv);
      addedPlayersBox.getChildren().add(playerBox);
      disableToken(pv.playerToken());
    }
  }

  /**
   * Creates a visual representation box for a player.
   *
   * @param player the player to create a box for
   * @return the created player display box
   */
  private VBox createPlayerBox(PlayerView player) {
    int yearsOld = java.time.Period.between(player.birthday(), LocalDate.now()).getYears();
    Label name = new Label(player.playerName());
    name.getStyleClass().add("player-name");
    Label age = new Label(yearsOld + " yrs");
    age.getStyleClass().add("player-age");
    VBox box = new VBox(name, age);
    box.setAlignment(Pos.CENTER);
    box.setSpacing(2);
    return box;
  }

  /**
   * Constructs the complete UI layout and components.
   */
  private void buildUI() {
    Label title = new Label("Add Players");
    title.getStyleClass().add("choose-player-title-label");
    title.setAlignment(Pos.CENTER);
    HBox titleBox = new HBox(title);
    titleBox.setAlignment(Pos.CENTER);

    Label playerName = new Label("Player Name");
    playerName.getStyleClass().add("popup-label");
    nameField = new TextField();
    nameField.setPromptText("Enter name");
    nameField.getStyleClass().add("name-field-style");
    nameField.setPrefWidth(200);
    HBox nameBox = new HBox(10, playerName, nameField);
    nameBox.getStyleClass().add("player-info-box");
    nameBox.setAlignment(Pos.CENTER);

    Label playerBirthday = new Label("Player Birthday");
    playerBirthday.getStyleClass().add("popup-label");
    birthdayPicker = new DatePicker(LocalDate.of(2001, 1, 1));
    birthdayPicker.getStyleClass().add("birthday-picker-style");
    birthdayPicker.setPromptText("Select your birthday");
    HBox birthdayBox = new HBox(10, playerBirthday, birthdayPicker);
    birthdayBox.getStyleClass().add("player-info-box");
    birthdayBox.setAlignment(Pos.CENTER);

    tokenToggleGroup = new ToggleGroup();
    FlowPane tokenSelectionBox = new FlowPane(10, 10);
    tokenSelectionBox.setAlignment(Pos.CENTER);
    tokenSelectionBox.getStyleClass().add("token-selection-box");
    tokenSelectionBox.setPrefWrapLength(450);
    for (String token : tokenNames) {
      ToggleButton tb = buildTokenButton(token);
      tokenButtons.put(token, tb);
      tokenSelectionBox.getChildren().add(tb);
    }

    addedPlayersBox = new FlowPane(15, 10);
    addedPlayersBox.setAlignment(Pos.CENTER);

    addPlayerButton = new Button("Add Player");
    addPlayerButton.getStyleClass().add("add-player-button");
    continueButton = new Button("Confirm");
    continueButton.getStyleClass().add("confirm-button");
    cancelButton = new Button("Cancel");
    cancelButton.getStyleClass().add("exit-button");
    savePlayerButton = new Button("Save Players");
    savePlayerButton.getStyleClass().add("board-size-button");
    loadPlayersButton = new Button("Load Players");
    loadPlayersButton.getStyleClass().add("board-size-button");

    HBox actionBox = new HBox(20, cancelButton, addPlayerButton, continueButton);
    actionBox.setAlignment(Pos.CENTER);
    actionBox.setPadding(new Insets(10));
    HBox loadSaveBox = new HBox(20, savePlayerButton, loadPlayersButton);
    loadSaveBox.setAlignment(Pos.CENTER);
    loadSaveBox.setPadding(new Insets(10));

    VBox popupContent = new VBox(
        15,
        titleBox,
        nameBox,
        birthdayBox,
        tokenSelectionBox,
        addedPlayersBox,
        actionBox,
        loadSaveBox);
    popupContent.setPadding(new Insets(20));
    popupContent.setSpacing(15);

    root.getStyleClass().add("page-background");
    root.getChildren().setAll(popupContent);
    root.setAlignment(Pos.CENTER);
    // Stylesheet typically applied by UiDialogs or scene creator
  }

  /**
   * Creates a toggle button for token selection with appropriate image.
   *
   * @param token the token name to create a button for
   * @return the created toggle button
   */
  private ToggleButton buildTokenButton(String token) {
    ToggleButton tb = new ToggleButton();
    String imagePath = ResourcePaths.IMAGE_DIR + token.toLowerCase() + "Piece.png";
    InputStream imageStream = getClass().getResourceAsStream(imagePath);
    ImageView iv;
    if (imageStream == null) {
      LOG.log(Level.WARNING, "Token image not found: {0}. Using placeholder.", imagePath);
      iv = new ImageView();
    } else {
      iv = new ImageView(new Image(imageStream));
    }
    iv.setFitWidth(50);
    iv.setFitHeight(50);
    tb.setGraphic(iv);
    tb.setUserData(token);
    tb.setToggleGroup(tokenToggleGroup);
    tb.getStyleClass().add("token-button");
    return tb;
  }

  /**
   * Gets the root view container.
   *
   * @return the root VBox container
   */
  public VBox getView() {
    return root;
  }

  /**
   * Gets the currently selected token name.
   *
   * @return the selected token name, or null if none selected
   */
  public String getSelectedToken() {
    Toggle t = tokenToggleGroup.getSelectedToggle();
    return t == null ? null : t.getUserData().toString();
  }

  /**
   * Disables the specified token button to prevent duplicate selection.
   *
   * @param token the token name to disable
   */
  public void disableToken(String token) {
    ToggleButton btn = tokenButtons.get(token);
    if (btn != null) {
      btn.setDisable(true);
      btn.setSelected(false);
    }
  }

  // Getter methods for UI components
  public Button getAddPlayerButton() {
    return addPlayerButton;
  }

  public Button getContinueButton() {
    return continueButton;
  }

  public Button getCancelButton() {
    return cancelButton;
  }

  public Button getSavePlayerButton() {
    return savePlayerButton;
  }

  public Button getLoadPlayersButton() {
    return loadPlayersButton;
  }

  public TextField getNameField() {
    return nameField;
  }

  public DatePicker getBirthdayPicker() {
    return birthdayPicker;
  }
}