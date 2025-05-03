package edu.ntnu.idatt2003.ui.view;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;

public class ChoosePlayerPage {

    private final VBox root;

    private TextField nameField;
    private DatePicker birthdayPicker;
    private Button cancelButton;
    private Button addPlayerButton;
    private Button savePlayerButton;
    private Button loadPlayersButton;
    private Button continueButton;
    private HBox addedPlayersBox;
    private ToggleGroup tokenToggleGroup;
    private Map<String, ToggleButton> tokenButtons = new HashMap<>();

    private static final String[] TOKEN_NAMES = { "BLUE","GREEN","YELLOW","RED","PURPLE" };

    public ChoosePlayerPage() {
        root = new VBox();
        buildUI();
    }

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
        addPlayerButton    = new Button("Add Player");     addPlayerButton   .getStyleClass().add("confirm-button");
        continueButton     = new Button("Confirm");        continueButton    .getStyleClass().add("confirm-button");
        cancelButton       = new Button("Cancel");         cancelButton      .getStyleClass().add("exit-button");
        savePlayerButton   = new Button("Save Players");   savePlayerButton  .getStyleClass().add("board-size-button");
        loadPlayersButton  = new Button("Load Players");   loadPlayersButton .getStyleClass().add("board-size-button");

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
        popupContent.getStyleClass().add("page-background");

        root.getChildren().setAll(popupContent);
        root.setAlignment(Pos.CENTER);
        root.setSpacing(20);

        // sluttvisning
    }

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

    /** Returnerer den ferdige view-rooten */
    public VBox getView() {
        return root;
    }

    // --- øvrige hjelpe‐metoder og getters ---
    public String getSelectedToken() {
        Toggle t = tokenToggleGroup.getSelectedToggle();
        return t == null ? null : t.getUserData().toString();
    }

    public void disableToken(String token) {
        ToggleButton btn = tokenButtons.get(token);
        if (btn != null) btn.setDisable(true);
    }

    public Button getAddPlayerButton()   { return addPlayerButton; }
    public Button getContinueButton()    { return continueButton; }
    public Button getCancelButton()      { return cancelButton; }
    public Button getSavePlayerButton()  { return savePlayerButton; }
    public Button getLoadPlayersButton() { return loadPlayersButton; }
    public HBox   getAddedPlayersBox()   { return addedPlayersBox; }
    public TextField getNameField()      { return nameField; }
    public DatePicker getBirthdayPicker(){ return birthdayPicker; }
}