package edu.ntnu.idatt2003.ui.shared.view;

import edu.games.engine.observer.BoardGameEvent;
import edu.games.engine.observer.BoardGameObserver;
import edu.ntnu.idatt2003.exception.ResourceNotFoundException;
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

public class ChoosePlayerPage implements BoardGameObserver {
  private static final Logger LOG = Logger.getLogger(ChoosePlayerPage.class.getName());

  private final VBox root;
  private final Map<String, ToggleButton> tokenButtons = new HashMap<>();
  private final String[] TOKEN_NAMES;

  private TextField nameField;
  private DatePicker birthdayPicker;
  private Button cancelButton;
  private Button addPlayerButton;
  private Button savePlayerButton;
  private Button loadPlayersButton;
  private Button continueButton;
  private FlowPane addedPlayersBox;
  private FlowPane tokenSelectionBox;
  private ToggleGroup tokenToggleGroup;

  private CompleteBoardGame gameGateway;

  public ChoosePlayerPage(String[] TOKEN_NAMES) {
    this.TOKEN_NAMES = TOKEN_NAMES;
    this.root = new VBox();
    buildUI();
  }

  public ChoosePlayerPage() {
    this(new String[] { "BLUE", "GREEN", "YELLOW", "RED", "PURPLE" });
  }

  public void connectToModel(CompleteBoardGame gateway) {
    this.gameGateway = gateway;
    gateway.addObserver(this);
  }

  @Override
  public void update(BoardGameEvent event) {
    Platform.runLater(
        () -> {
          switch (event.getType()) {
            case PLAYER_ADDED:
              LOG.fine("Player added event received, updating display.");
              handlePlayerAdded();
              break;
            case GAME_RESET:
              LOG.fine("Game reset event received, resetting player UI.");
              handleGameReset();
              break;
            default:
              LOG.finest("Received unhandled event type: " + event.getType());
              break;
          }
        });
  }

  private void handlePlayerAdded() {
    if (gameGateway != null) {
      updatePlayerDisplay(gameGateway.players());
    }
  }

  private void handleGameReset() {
    addedPlayersBox.getChildren().clear();
    tokenButtons.values().forEach(tb -> tb.setDisable(false));
  }

  public void updatePlayerDisplay(List<PlayerView> players) {
    List<PlayerView> sorted = players.stream().sorted(Comparator.comparing(PlayerView::birthday).reversed()).toList();

    addedPlayersBox.getChildren().clear();

    for (PlayerView pv : sorted) {
      VBox playerBox = createPlayerBox(pv);
      addedPlayersBox.getChildren().add(playerBox);
      disableToken(pv.token());
    }
  }

  private VBox createPlayerBox(PlayerView player) {
    int yearsOld = java.time.Period.between(player.birthday(), LocalDate.now()).getYears();
    Label name = new Label(player.name());
    name.getStyleClass().add("player-name");
    Label age = new Label(yearsOld + " yrs");
    age.getStyleClass().add("player-age");
    VBox box = new VBox(name, age);
    box.setAlignment(Pos.CENTER);
    box.setSpacing(2);
    return box;
  }

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
    tokenSelectionBox = new FlowPane(10, 10);
    tokenSelectionBox.setAlignment(Pos.CENTER);
    tokenSelectionBox.getStyleClass().add("token-selection-box");
    tokenSelectionBox.setPrefWrapLength(450);
    for (String token : TOKEN_NAMES) {
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

  private ToggleButton buildTokenButton(String token) {
    ToggleButton tb = new ToggleButton();
    String imagePath = ResourcePaths.IMAGE_DIR + token.toLowerCase() + "Piece.png";
    InputStream imageStream = getClass().getResourceAsStream(imagePath);
    ImageView iv;
    if (imageStream == null) {
      LOG.warning("Token image not found: " + imagePath + ". Using placeholder.");
      iv = new ImageView(); // Placeholder
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

  public VBox getView() {
    return root;
  }

  public String getSelectedToken() {
    Toggle t = tokenToggleGroup.getSelectedToggle();
    return t == null ? null : t.getUserData().toString();
  }

  public void disableToken(String token) {
    ToggleButton btn = tokenButtons.get(token);
    if (btn != null) {
      btn.setDisable(true);
      btn.setSelected(false);
    }
  }

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