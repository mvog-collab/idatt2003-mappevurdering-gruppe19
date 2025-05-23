package edu.ntnu.idatt2003.presentation.ludo.view;

import edu.games.engine.model.BoardGame.PlayerMoveData;
import edu.games.engine.model.Player;
import edu.ntnu.idatt2003.gateway.GameGateway;
import edu.ntnu.idatt2003.gateway.view.PlayerView;
import edu.ntnu.idatt2003.presentation.common.view.AbstractGameView;
import edu.ntnu.idatt2003.presentation.common.view.GameView;
import edu.ntnu.idatt2003.presentation.fx.OverlayParams;
import edu.ntnu.idatt2003.presentation.service.animation.AnimationService;
import edu.ntnu.idatt2003.presentation.service.board.LudoBoardUIService;
import edu.ntnu.idatt2003.presentation.service.dice.DiceService;
import edu.ntnu.idatt2003.presentation.service.player.PlayerUIService;
import edu.ntnu.idatt2003.presentation.service.ViewServiceFactory;
import edu.ntnu.idatt2003.utils.Dialogs;
import edu.ntnu.idatt2003.utils.ResourcePaths;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

/**
 * JavaFX-based view implementation for the Ludo board game interface.
 * <p>
 * This view manages the complete visual representation of a Ludo game,
 * including
 * board rendering, player piece positioning, dice display, game controls, and
 * animations. It serves as the primary user interface component that players
 * interact with during gameplay.
 * </p>
 */
public class LudoBoardView extends AbstractGameView implements GameView {

  // Services
  private final LudoBoardUIService boardUIService;
  private final PlayerUIService playerUIService;
  private AnimationService animationService;
  private final DiceService diceService;

  // UI Components
  private StackPane gameBoardArea;
  private final Pane tokenPane;
  private final Pane overlayPane;
  private final VBox controlPanel;
  private final HBox diceContainer;
  private Node playerTurnBox;
  private BorderPane rootLayout;

  // States
  private Consumer<Integer> pieceSelectedCallback;
  private boolean hasActiveAnimation = false;
  private List<PlayerView> players = new ArrayList<>();
  private int lastRoll = 0;
  private final Map<String, List<ImageView>> tokenImages = new HashMap<>();

  /**
   * Constructs a new LudoBoardView with the specified services.
   * <p>
   * This constructor allows for dependency injection of all required services
   * and provides full control over the view's behavior and appearance.
   * </p>
   *
   * @param boardUIService   service for managing board UI elements
   * @param playerUIService  service for managing player UI elements
   * @param animationService service for handling animations
   * @param diceService      service for managing dice display
   */
  public LudoBoardView(
      LudoBoardUIService boardUIService,
      PlayerUIService playerUIService,
      AnimationService animationService,
      DiceService diceService) {

    this.boardUIService = boardUIService;
    this.playerUIService = playerUIService;
    this.animationService = animationService;
    this.diceService = diceService;

    this.rootLayout = new BorderPane();
    this.tokenPane = new Pane();
    this.overlayPane = new Pane();
    this.gameBoardArea = new StackPane();

    this.controlPanel = new VBox(15);
    this.diceContainer = new HBox(10);
    this.playerTurnBox = this.playerUIService.createCurrentPlayerTurnBox(null);

    this.rollButton = new Button("Roll dice");
    this.playAgainButton = new Button("Play again");

    buildUI();
  }

  /**
   * Constructs a new LudoBoardView with default service implementations.
   * <p>
   * This constructor creates a fully functional view using factory-provided
   * services configured specifically for Ludo gameplay. The animation service
   * is initialized with board tile coordinates for smooth piece movement.
   * </p>
   */
  public LudoBoardView() {
    this(
        new LudoBoardUIService(),
        ViewServiceFactory.createPlayerUIService("LUDO"),
        null,
        ViewServiceFactory.createDiceService("LUDO"));

    Map<Integer, Point2D> coordinates = boardUIService.getTileCoordinates();
    this.animationService = ViewServiceFactory.createAnimationService(
        "LUDO", coordinates, this.tokenPane);
  }

  /**
   * Constructs and arranges all UI components for the game interface.
   * <p>
   * This method sets up the complete visual layout including the game board,
   * control panel, dice area, and navigation elements. It applies appropriate
   * styling and spacing to create a cohesive game interface.
   * </p>
   */
  private void buildUI() {
    boardUIService.initializeGameBoardArea(this.gameBoardArea, this.overlayPane, this.tokenPane);

    diceContainer.setAlignment(Pos.CENTER);
    diceContainer.setPadding(new Insets(10));
    diceContainer.setMinHeight(220);
    diceContainer.getStyleClass().add("dice-box");
    diceService.initializeDice(diceContainer);

    rollButton.getStyleClass().add("roll-dice-button");
    playAgainButton.getStyleClass().add("play-again-button");

    Button howToButton = createHowToPlayButton(
        "How to play - Ludo",
        """
            - Throw a 6 to get a piece out of your home.
            - If you land on an enemy player(s), they are sent back to their home.
            - The first player with all pieces in the goal is the winner!
            """);

    HBox buttonContainer = new HBox(10, rollButton, playAgainButton);
    buttonContainer.setAlignment(Pos.CENTER);

    controlPanel.getChildren().addAll(howToButton, playerTurnBox, diceContainer, buttonContainer);
    controlPanel.setAlignment(Pos.TOP_CENTER);
    controlPanel.setPadding(new Insets(10));
    controlPanel.getStyleClass().add("game-control");

    VBox.setVgrow(playerTurnBox, Priority.NEVER);
    VBox.setVgrow(diceContainer, Priority.NEVER);
    VBox.setVgrow(buttonContainer, Priority.NEVER);

    rootLayout.setCenter(gameBoardArea);
    rootLayout.setRight(controlPanel);
    rootLayout.setPadding(new Insets(10, 15, 10, 15));
    rootLayout.getStyleClass().add("page-background");

    addNavigationAndHelpToBorderPane(rootLayout, true, howToButton);

    BorderPane.setAlignment(gameBoardArea, Pos.CENTER);
    BorderPane.setMargin(gameBoardArea, new Insets(0, 10, 0, 0));
    BorderPane.setMargin(controlPanel, new Insets(0, 0, 0, 10));
  }

  /**
   * Creates and returns the main scene for this view.
   * <p>
   * Configures the scene with appropriate dimensions and applies the
   * game stylesheet for consistent visual styling.
   * </p>
   *
   * @return the configured Scene object ready for display
   */
  @Override
  public Scene getScene() {
    Scene scene = new Scene(rootLayout, 1100, 750);
    scene.getStylesheets().add(getClass().getResource(ResourcePaths.STYLE_SHEET).toExternalForm());
    return scene;
  }

  /**
   * Updates the view with current player information and board overlays.
   * <p>
   * This method refreshes all player pieces on the board, sets up click
   * handlers for piece selection, and updates overlay elements. It handles
   * the complete visual state synchronization between the game model and view.
   * </p>
   *
   * @param players  list of current players and their game states
   * @param overlays list of overlay parameters for board decoration
   */
  @Override
  public void setPlayers(List<PlayerView> players, List<OverlayParams> overlays) {
    try {
      this.players = new ArrayList<>(players);

      this.tokenPane.getChildren().clear();
      tokenImages.clear();

      for (PlayerView player : players) {
        List<ImageView> pieces = playerUIService.createPlayerPieces(player);
        tokenImages.put(player.playerToken(), pieces);

        for (int i = 0; i < pieces.size(); i++) {
          final int pieceIndex = i;
          ImageView piece = pieces.get(i);
          piece.setOnMouseClicked(e -> handlePieceClicked(player.playerToken(), pieceIndex));
          this.tokenPane.getChildren().add(piece);
        }
        updatePiecePositions(player);
      }
      boardUIService.addOverlays(this.overlayPane, overlays);
      updateStatusForCurrentPlayer();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Updates the visual positions of all pieces for a specific player.
   * <p>
   * This method handles both home positioning and board positioning of pieces,
   * applies appropriate visual highlights for the current player, and ensures
   * pieces are displayed at their correct locations based on game state.
   * </p>
   *
   * @param player the player whose pieces should be repositioned
   */
  private void updatePiecePositions(PlayerView player) {
    try {
      List<ImageView> pieces = tokenImages.get(player.playerToken());
      if (pieces == null)
        return;

      for (int i = 0; i < Math.min(player.piecePositions().size(), pieces.size()); i++) {
        int position = player.piecePositions().get(i);
        ImageView piece = pieces.get(i);

        if (player.hasTurn()) {
          boardUIService.highlightActivePiece(piece);
        } else {
          boardUIService.removeHighlight(piece);
        }

        if (position <= 0) {
          boardUIService.placePieceAtHome(this.tokenPane, piece, player.playerToken(), i);
        } else {
          boardUIService.placePieceOnBoard(this.tokenPane, piece, position, i);
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Handles mouse click events on player pieces.
   * <p>
   * Validates that the clicked piece belongs to the current player and
   * that the intended move is legal according to Ludo rules. Only valid
   * piece selections are forwarded to the registered callback.
   * </p>
   *
   * @param tokenName  the token/color of the clicked piece
   * @param pieceIndex the index of the clicked piece
   */
  private void handlePieceClicked(String tokenName, int pieceIndex) {
    // Only respond to clicks from the current player's pieces
    PlayerView currentPlayer = getCurrentPlayer();
    if (currentPlayer == null || !currentPlayer.playerToken().equals(tokenName)) {
      return;
    }

    // Check if the move is valid
    if (pieceIndex < currentPlayer.piecePositions().size()) {
      int position = currentPlayer.piecePositions().get(pieceIndex);

      // Piece is at home and roll isn't 6
      if (position <= 0 && lastRoll != 6) {
        showAlert("Invalid move", "You need to roll a 6 to move a piece from home.");
        return;
      }
    }

    // Tell the controller that this piece was selected
    if (pieceSelectedCallback != null) {
      pieceSelectedCallback.accept(pieceIndex);
    }
  }

  /**
   * Gets the player whose turn it currently is.
   *
   * @return the current player, or null if no player has the turn
   */
  private PlayerView getCurrentPlayer() {
    return players.stream().filter(PlayerView::hasTurn).findFirst().orElse(null);
  }

  /**
   * Updates the status display to show the current player's turn.
   * <p>
   * This method refreshes the player turn indicator in the UI to reflect
   * whose turn it is or displays a default message if no current player exists.
   * </p>
   */
  private void updateStatusForCurrentPlayer() {
    PlayerView currentPlayer = getCurrentPlayer();
    if (currentPlayer != null) {
      showStatusMessage(currentPlayer.playerName() + "'s turn");
    } else {
      showStatusMessage("Roll the dice to start");
    }
  }

  /**
   * Handles dice roll events from the game model.
   * <p>
   * Processes the rolled value, updates the dice display, and manages
   * the subsequent game flow including move validation and player feedback.
   * Special handling is provided for rolls of 6 which have unique significance in
   * Ludo.
   * </p>
   *
   * @param data the dice roll data containing the rolled value(s)
   */
  @Override
  protected void handleDiceRolled(Object data) {
    if (hasActiveAnimation)
      return;

    int[] diceValues = diceService.parseDiceRoll(data);
    if (diceValues.length == 0)
      return;

    int dieValue = diceValues[0];
    lastRoll = dieValue;

    diceService.showDice(diceContainer, diceValues);
    disableRollButton();

    PlayerView currentPlayer = getCurrentPlayer();
    if (currentPlayer != null) {
      boolean hasValidMove = checkForValidMoves(currentPlayer, dieValue);

      if (!hasValidMove) {
        showStatusMessage("No valid moves available. Next player's turn.");
        enableRollButton();
      } else if (dieValue == 6) {
        showStatusMessage(currentPlayer.playerName() + " rolled a 6! Select a piece to move.");
      } else {
        showStatusMessage(
            currentPlayer.playerName() + " rolled a " + dieValue + ". Select a piece to move.");
      }
    }
  }

  /**
   * Checks if a player has any valid moves available with the given dice roll.
   * <p>
   * A move is considered valid if the piece is on the board, or if the piece
   * is at home and the player rolled a 6 (required to move pieces from home in
   * Ludo).
   * </p>
   *
   * @param player the player to check for valid moves
   * @param roll   the dice value rolled
   * @return true if at least one valid move is available
   */
  private boolean checkForValidMoves(PlayerView player, int roll) {
    return player.piecePositions().stream().anyMatch(pos -> pos > 0 || (pos <= 0 && roll == 6));
  }

  /**
   * Handles player movement events from the game model.
   * <p>
   * Processes piece movement data, determines the affected piece, and
   * triggers appropriate animations to visually represent the move.
   * </p>
   *
   * @param data the player move data containing movement information
   */
  @Override
  protected void handlePlayerMoved(Object data) {
    if (hasActiveAnimation)
      return;

    try {
      hasActiveAnimation = true;

      if (data instanceof PlayerMoveData moveData) {
        String tokenName = moveData.getPlayer().getToken().name();
        int fromId = moveData.getFromTile() != null ? moveData.getFromTile().tileId() : 0;
        int toId = moveData.getToTile() != null ? moveData.getToTile().tileId() : 0;
        int pieceIndex = findPieceIndex(tokenName, fromId);

        List<Integer> path = new ArrayList<>();
        if (fromId > 0)
          path.add(fromId);
        path.add(toId);

        animatePlayerMove(tokenName, pieceIndex, path);
      }
    } finally {
      hasActiveAnimation = false;
      enableRollButton();
    }
  }

  /**
   * Animates a player piece movement along a specified path.
   * <p>
   * This method coordinates with the animation service to create smooth
   * visual transitions as pieces move across the board.
   * </p>
   *
   * @param tokenName  the token/color of the moving piece
   * @param pieceIndex the index of the piece to animate
   * @param path       the list of positions the piece should move through
   */
  private void animatePlayerMove(String tokenName, int pieceIndex, List<Integer> path) {
    List<ImageView> tokens = tokenImages.get(tokenName);
    if (tokens == null || pieceIndex >= tokens.size() || path.isEmpty()) {
      return;
    }
    animationService.animateMoveAlongPath(tokenName, pieceIndex, path, this::refreshTokens);
  }

  /**
   * Finds the index of a piece at a specific board position.
   * <p>
   * Searches through all pieces of a given token to find which piece
   * is currently at the specified tile. Falls back to active piece
   * or first piece if no exact match is found.
   * </p>
   *
   * @param token  the token/color to search within
   * @param tileId the tile position to search for
   * @return the index of the piece at the specified position
   */
  private int findPieceIndex(String token, int tileId) {
    for (PlayerView player : players) {
      if (player.playerToken().equals(token)) {
        for (int i = 0; i < player.piecePositions().size(); i++) {
          if (player.piecePositions().get(i) == tileId) {
            return i;
          }
        }
        // If not found, use active piece or first piece
        return player.activePieceIndex() >= 0 ? player.activePieceIndex() : 0;
      }
    }
    return 0;
  }

  /**
   * Handles winner declaration events from the game model.
   * <p>
   * Processes winner information and triggers the appropriate
   * celebration display for the victorious player.
   * </p>
   *
   * @param data the winner data containing player information
   */
  @Override
  protected void handleWinnerDeclared(Object data) {
    if (data instanceof Player winner) {
      announceWinner(winner.getName());
    }
  }

  /**
   * Handles game reset events from the game model.
   * <p>
   * Resets the view to its initial state with updated player
   * information and prepares for a new game session.
   * </p>
   */
  @Override
  protected void handleGameReset() {
    setPlayers(gateway.players(), gateway.boardOverlays());
    showStatusMessage("Roll the dice to start");
    enableRollButton();
  }

  /**
   * Handles turn change events from the game model.
   * <p>
   * Updates the UI to reflect the new current player, including
   * status messages and piece highlighting. Ensures only the
   * current player's pieces are visually active.
   * </p>
   *
   * @param data the turn change data containing new current player information
   */
  @Override
  protected void handleTurnChanged(Object data) {
    if (data instanceof Player player) {
      String tokenName = player.getToken().name();

      for (PlayerView pv : players) {
        boolean isCurrentPlayer = pv.playerToken().equals(tokenName);
        if (isCurrentPlayer) {
          showStatusMessage(pv.playerName() + "'s turn");
        }

        updatePieceHighlights(pv, isCurrentPlayer);
      }
    }
    enableRollButton();
  }

  /**
   * Updates visual highlighting for a player's pieces.
   * <p>
   * Applies or removes highlighting effects based on whether the player
   * is currently active and which piece (if any) is selected for movement.
   * </p>
   *
   * @param player          the player whose pieces to update
   * @param isCurrentPlayer whether this player is currently active
   */
  private void updatePieceHighlights(PlayerView player, boolean isCurrentPlayer) {
    List<ImageView> pieces = tokenImages.get(player.playerToken());
    if (pieces == null)
      return;

    for (int i = 0; i < pieces.size(); i++) {
      ImageView piece = pieces.get(i);

      if (isCurrentPlayer && player.activePieceIndex() == i) {
        boardUIService.highlightActivePiece(piece);
      } else {
        boardUIService.removeHighlight(piece);
      }
    }
  }

  /**
   * Refreshes all token positions on the board.
   * <p>
   * Updates the visual positions of all player pieces to match
   * their current state in the game model.
   * </p>
   */
  private void refreshTokens() {
    for (PlayerView player : players) {
      updatePiecePositions(player);
    }
  }

  /**
   * Displays a dice with the specified value.
   * <p>
   * Updates the dice display in the UI and stores the rolled
   * value for game logic validation.
   * </p>
   *
   * @param value the dice value to display
   */
  @Override
  public void showDice(int value) {
    diceService.showDice(diceContainer, new int[] { value });
    lastRoll = value;
  }

  /**
   * Announces a game winner with a congratulatory message.
   * <p>
   * Disables game controls and displays a celebration dialog
   * for the victorious player.
   * </p>
   *
   * @param name the name of the winning player
   */
  @Override
  public void announceWinner(String name) {
    disableRollButton();
    Dialogs.info("Winner!", "Congratulations, " + name + "! You won the game!");
  }

  /**
   * Sets the callback function for handling piece selection events.
   * <p>
   * This callback is invoked when a player clicks on a valid piece,
   * allowing the controller to process the selection appropriately.
   * </p>
   *
   * @param callback the function to call when a piece is selected
   */
  public void setPieceSelectedCallback(Consumer<Integer> callback) {
    this.pieceSelectedCallback = callback;
  }

  /**
   * Displays an informational alert dialog with the specified title and message.
   *
   * @param title   the title for the alert dialog
   * @param message the message content to display
   */
  public void showAlert(String title, String message) {
    Alert alert = new Alert(Alert.AlertType.INFORMATION);
    alert.setTitle(title);
    alert.setHeaderText(null);
    alert.setContentText(message);
    alert.showAndWait();
  }

  /**
   * Gets the last dice value that was rolled.
   *
   * @return the most recent dice roll value
   */
  public int getLastRoll() {
    return lastRoll;
  }

  /**
   * Connects this view to the specified game gateway for model updates.
   * <p>
   * Registers this view as an observer to receive game state change
   * notifications from the model.
   * </p>
   *
   * @param gateway the game gateway to observe for model changes
   */
  @Override
  public void connectToModel(GameGateway gateway) {
    gateway.addObserver(this);
  }

  /**
   * Updates the status message display for the current game state.
   * <p>
   * This method updates the player turn indicator with the specified
   * message, providing feedback about current game conditions.
   * </p>
   *
   * @param message the status message to display
   */
  public void showStatusMessage(String message) {
    PlayerView currentPlayer = getCurrentPlayer();
    playerUIService.updateCurrentPlayerTurnBox(playerTurnBox, currentPlayer, message);
  }

  /**
   * Animates a piece movement along a specified path with completion callback.
   * <p>
   * This method provides controlled animation execution, preventing concurrent
   * animations and ensuring proper state management throughout the animation
   * lifecycle.
   * </p>
   *
   * @param tokenName  the token/color of the piece to animate
   * @param pieceIndex the index of the piece to animate
   * @param path       the sequence of positions to move through
   * @param onFinished callback to execute when animation completes
   */
  public void animateMoveAlongPath(
      String tokenName, int pieceIndex, List<Integer> path, Runnable onFinished) {
    if (hasActiveAnimation) {
      if (onFinished != null)
        onFinished.run();
      return;
    }

    hasActiveAnimation = true;
    animationService.animateMoveAlongPath(
        tokenName,
        pieceIndex,
        path,
        () -> {
          hasActiveAnimation = false;
          if (onFinished != null) {
            onFinished.run();
          }
        });
  }

  /**
   * Updates the view with current player information without overlays.
   * <p>
   * Convenience method that calls the full setPlayers method with
   * an empty overlay list.
   * </p>
   *
   * @param players list of current players and their game states
   */
  public void setPlayers(List<PlayerView> players) {
    setPlayers(players, new ArrayList<>()); // Empty overlays list
  }

  /**
   * Updates the board overlay elements.
   * <p>
   * Applies visual overlay effects to the board such as highlights,
   * special indicators, or decorative elements.
   * </p>
   *
   * @param overlays list of overlay parameters to apply
   */
  public void setOverlays(List<OverlayParams> overlays) {
    boardUIService.addOverlays(overlayPane, overlays);
  }

  /**
   * Enables the dice roll button for player interaction.
   * <p>
   * This method safely enables the roll button using Platform.runLater
   * to ensure thread safety, and only enables if no animation is active.
   * </p>
   */
  @Override
  public void enableRollButton() {
    Platform.runLater(
        () -> {
          if (!hasActiveAnimation) {
            rollButton.setDisable(false);
          }
        });
  }

  /**
   * Animates a simple piece movement between two positions.
   * <p>
   * Provides a simpler animation interface for direct position-to-position
   * movement without requiring a full path specification.
   * </p>
   *
   * @param tokenName  the token/color of the piece to animate
   * @param startId    the starting position ID
   * @param endId      the ending position ID
   * @param onFinished callback to execute when animation completes
   */
  public void animateMove(String tokenName, int startId, int endId, Runnable onFinished) {
    if (hasActiveAnimation) {
      if (onFinished != null)
        onFinished.run();
      return;
    }

    hasActiveAnimation = true;
    animationService.animateMove(
        tokenName,
        startId,
        endId,
        () -> {
          hasActiveAnimation = false;
          if (onFinished != null)
            onFinished.run();
        });
  }
}