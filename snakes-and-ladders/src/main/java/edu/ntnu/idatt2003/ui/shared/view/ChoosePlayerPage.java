package edu.ntnu.idatt2003.ui.shared.view;

import edu.games.engine.observer.BoardGameEvent;
import edu.games.engine.observer.BoardGameObserver;
import edu.ntnu.idatt2003.gateway.CompleteBoardGame;
import edu.ntnu.idatt2003.gateway.GameGateway;
import edu.ntnu.idatt2003.gateway.view.PlayerView;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChoosePlayerPage implements BoardGameObserver {

    private final VBox root;
    private final Map<String, ToggleButton> tokenButtons = new HashMap<>();
    private final String[] TOKEN_NAMES;
    
    // UI components
    private TextField nameField;
    private DatePicker birthdayPicker;
    private Button cancelButton;
    private Button addPlayerButton;
    private Button savePlayerButton;
    private Button loadPlayersButton;
    private Button continueButton;
    private HBox addedPlayersBox;
    private ToggleGroup tokenToggleGroup;
    
    // Gateway reference
    private CompleteBoardGame gameGateway;

    /**
     * Constructor with custom token names
     */
    public ChoosePlayerPage(String[] TOKEN_NAMES) {
        this.TOKEN_NAMES = TOKEN_NAMES;
        this.root = new VBox();
        buildUI();
    }

    /**
     * Default constructor with standard tokens
     */
    public ChoosePlayerPage() {
        this(new String[] { "BLUE","GREEN","YELLOW","RED","PURPLE" });
    }
    
    /**
     * Connects this view to the model and registers as an observer
     */
    public void connectToModel(CompleteBoardGame gateway) {
        this.gameGateway = gateway;
        gateway.addObserver(this);
    }
    
    /**
     * Handles model events by delegating to specific handlers
     */
    @Override
    public void update(BoardGameEvent event) {
        Platform.runLater(() -> {
            switch (event.getType()) {
                case PLAYER_ADDED:
                    handlePlayerAdded();
                    break;
                case GAME_RESET:
                    handleGameReset();
                    break;
            }
        });
    }
    
    /**
     * Updates the UI when a player is added to the model
     */
    private void handlePlayerAdded() {
        if (gameGateway != null) {
            updatePlayerDisplay(gameGateway.players());
        }
    }
    
    /**
     * Resets the UI when the game is reset
     */
    private void handleGameReset() {
        // Clear player list
        addedPlayersBox.getChildren().clear();
        
        // Re-enable all token buttons
        tokenButtons.values().forEach(tb -> tb.setDisable(false));
    }
    
    /**
     * Updates the player display with the current list of players
     */
    public void updatePlayerDisplay(List<PlayerView> players) {
        // Sort players by birthday (oldest first)
        List<PlayerView> sorted = players.stream()
            .sorted(Comparator.comparing(PlayerView::birthday).reversed())
            .toList();
        
        // Clear and rebuild player display
        addedPlayersBox.getChildren().clear();
        
        for (PlayerView pv : sorted) {
            VBox playerBox = createPlayerBox(pv);
            addedPlayersBox.getChildren().add(playerBox);
            
            // Disable used tokens
            disableToken(pv.token());
        }
    }
    
    /**
     * Creates a player box UI component for a player
     */
    private VBox createPlayerBox(PlayerView pv) {
        int years = java.time.Period.between(pv.birthday(), LocalDate.now()).getYears();

        Label name = new Label(pv.name());
        name.getStyleClass().add("player-name");

        Label age = new Label(years + " yrs");
        age.getStyleClass().add("player-age");

        VBox box = new VBox(name, age);
        box.setAlignment(Pos.CENTER);
        box.setSpacing(2);
        return box;
    }
    
    /**
     * Constructs the UI components
     */
    private void buildUI() {
        // --- Title ---
        Label title = new Label("Add Players");
        title.getStyleClass().add("choose-player-title-label");
        title.setAlignment(Pos.CENTER);
        HBox titleBox = new HBox(title);
        titleBox.setAlignment(Pos.CENTER);

        // --- Name & birthday fields ---
        Label playerName = new Label("Player Name");
        playerName.getStyleClass().add("popup-label");
        nameField = new TextField();
        nameField.setPromptText("Enter name");
        nameField.getStyleClass().add("name-field-style");
        HBox nameBox = new HBox(10, playerName, nameField);
        nameBox.getStyleClass().add("player-info-box");
        nameBox.setAlignment(Pos.CENTER);

        Label playerBirthday = new Label("Player Birthday");
        playerBirthday.getStyleClass().add("popup-label");
        birthdayPicker = new DatePicker(LocalDate.of(2001,1,1));
        birthdayPicker.getStyleClass().add("birthday-picker-style");
        birthdayPicker.setPromptText("Select your birthday");
        HBox birthdayBox = new HBox(10, playerBirthday, birthdayPicker);
        birthdayBox.getStyleClass().add("player-info-box");
        birthdayBox.setAlignment(Pos.CENTER);

        // --- Token selection ---
        tokenToggleGroup = new ToggleGroup();
        HBox tokenSelectionBox = new HBox(10);
        tokenSelectionBox.setAlignment(Pos.CENTER);
        tokenSelectionBox.getStyleClass().add("token-selection-box");
        for (String token : TOKEN_NAMES) {
            ToggleButton tb = buildTokenButton(token);
            tokenButtons.put(token, tb);
            tokenSelectionBox.getChildren().add(tb);
        }

        // --- Added players placeholder ---
        addedPlayersBox = new HBox(30);
        addedPlayersBox.setAlignment(Pos.CENTER);

        // --- Buttons ---
        addPlayerButton    = new Button("Add Player");     
        addPlayerButton.getStyleClass().add("add-player-button");
        
        continueButton     = new Button("Confirm");        
        continueButton.getStyleClass().add("confirm-button");
        
        cancelButton       = new Button("Cancel");         
        cancelButton.getStyleClass().add("exit-button");
        
        savePlayerButton   = new Button("Save Players");   
        savePlayerButton.getStyleClass().add("board-size-button");
        
        loadPlayersButton  = new Button("Load Players");   
        loadPlayersButton.getStyleClass().add("board-size-button");

        HBox actionBox = new HBox(20, cancelButton, addPlayerButton, continueButton);
        actionBox.setAlignment(Pos.CENTER);
        actionBox.setPadding(new Insets(10));

        HBox loadSaveBox = new HBox(20, savePlayerButton, loadPlayersButton);
        loadSaveBox.setAlignment(Pos.CENTER);
        loadSaveBox.setPadding(new Insets(10));

        // --- Assemble popup ---
        VBox popupContent = new VBox(15,
            titleBox,
            nameBox,
            birthdayBox,
            tokenSelectionBox,
            addedPlayersBox,
            actionBox,
            loadSaveBox
        );
        popupContent.setAlignment(Pos.CENTER);
        popupContent.setPadding(new Insets(20));

        root.getStyleClass().add("page-background");
        root.getChildren().setAll(popupContent);
        root.setAlignment(Pos.CENTER);
        root.setSpacing(20);
    }

    /**
     * Creates a styled token button
     */
    private ToggleButton buildTokenButton(String token) {
        ToggleButton tb = new ToggleButton();
        ImageView iv = new ImageView(new Image(getClass().getResourceAsStream(
            "/images/" + token.toLowerCase() + "Piece.png"
        )));
        iv.setFitWidth(50);
        iv.setFitHeight(50);
        tb.setGraphic(iv);
        tb.setUserData(token);
        tb.setToggleGroup(tokenToggleGroup);
        tb.getStyleClass().add("token-button");
        return tb;
    }

    // --- Getters and utility methods ---
    
    public VBox getView() {
        return root;
    }

    public String getSelectedToken() {
        Toggle t = tokenToggleGroup.getSelectedToggle();
        return t == null ? null : t.getUserData().toString();
    }

    public void disableToken(String token) {
        ToggleButton btn = tokenButtons.get(token);
        if (btn != null) btn.setDisable(true);
    }

    public Button getAddPlayerButton() { return addPlayerButton; }
    public Button getContinueButton() { return continueButton; }
    public Button getCancelButton() { return cancelButton; }
    public Button getSavePlayerButton() { return savePlayerButton; }
    public Button getLoadPlayersButton() { return loadPlayersButton; }
    public HBox getAddedPlayersBox() { return addedPlayersBox; }
    public TextField getNameField() { return nameField; }
    public DatePicker getBirthdayPicker() { return birthdayPicker; }
}