package edu.ntnu.idatt2003.presentation.snl.view;

import edu.games.engine.model.BoardGame;
import edu.games.engine.model.Player;
import edu.ntnu.idatt2003.gateway.GameGateway;
import edu.ntnu.idatt2003.gateway.view.PlayerView;
import edu.ntnu.idatt2003.presentation.common.view.AbstractGameView;
import edu.ntnu.idatt2003.presentation.fx.OverlayParams;
import edu.ntnu.idatt2003.presentation.service.animation.AnimationService;
import edu.ntnu.idatt2003.presentation.service.board.BoardUIService;
import edu.ntnu.idatt2003.presentation.service.board.SnlBoardUIService;
import edu.ntnu.idatt2003.presentation.service.dice.DiceService;
import edu.ntnu.idatt2003.presentation.service.player.PlayerUIService;
import edu.ntnu.idatt2003.presentation.service.ViewServiceFactory;
import edu.ntnu.idatt2003.utils.Dialogs;
import edu.ntnu.idatt2003.utils.ResourcePaths;
import java.util.*;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;

/**
 * Main game board view for Snakes and Ladders.
 * This view handles displaying the game board, player tokens, dice,
 * animations, and all game-related UI components. It coordinates between
 * various UI services to provide a complete gaming experience.
 */
public class SnlBoardView extends AbstractGameView {
  private final BoardUIService boardUIService;
  private final PlayerUIService playerUIService;
  private AnimationService animationService;
  private final DiceService diceService;

  private BorderPane rootLayout;
  private StackPane boardStack;
  private Node playerTurnBox;
  private final Pane tokenPane;
  private final Pane overlayPane;
  private final VBox controlPanel;
  private final HBox diceContainer;

  private final int boardSize;
  private final Map<String, ImageView> tokenImages = new HashMap<>();
  private boolean hasActiveAnimation = false;
  private List<PlayerView> currentPlayers = new ArrayList<>();

  /**
   * Creates a new SNL board view with specific services.
   * This constructor allows full control over which services are used
   * for different aspects of the game display.
   *
   * @param boardSize        the size of the board (number of tiles)
   * @param boardUIService   service for handling board display
   * @param playerUIService  service for handling player UI elements
   * @param animationService service for handling token animations
   * @param diceService      service for handling dice display and rolling
   */
  public SnlBoardView(
      int boardSize,
      BoardUIService boardUIService,
      PlayerUIService playerUIService,
      AnimationService animationService,
      DiceService diceService) {
    this.boardSize = boardSize;
    this.boardUIService = boardUIService;
    this.playerUIService = playerUIService;
    this.animationService = animationService;
    this.diceService = diceService;

    this.rootLayout = new BorderPane();
    this.tokenPane = new Pane();
    this.overlayPane = new Pane();

    this.controlPanel = new VBox(15);
    this.diceContainer = new HBox(10);

    this.rollButton = new Button("Roll Dice");
    this.playAgainButton = new Button("Play Again");

    buildUI();
  }

  /**
   * Creates a new SNL board view with default services.
   * This convenience constructor automatically creates the appropriate
   * services for Snakes and Ladders gameplay.
   *
   * @param boardSize the size of the board (number of tiles)
   */
  public SnlBoardView(int boardSize) {
    this(
        boardSize,
        ViewServiceFactory.createBoardUIService("SNL", boardSize),
        ViewServiceFactory.createPlayerUIService("SNL"),
        null,
        ViewServiceFactory.createDiceService("SNL"));

    Map<Integer, Point2D> coordinates = boardUIService.getTileCoordinates();
    AnimationService newAnimationService = ViewServiceFactory.createAnimationService("SNL", coordinates,
        this.tokenPane);
    this.animationService = newAnimationService;
  }

  /**
   * Constructs the main UI layout.
   * Sets up the board display, control panel, dice area, and arranges
   * all components in their proper positions. Also configures sizing
   * and styling for all UI elements.
   */
  private void buildUI() {
    this.boardStack = boardUIService.createBoardPane(boardSize);
    Pane visualBoardPane = (Pane) this.boardStack.getChildren().get(0);

    double boardDisplayWidth = visualBoardPane.getPrefWidth();
    double boardDisplayHeight = visualBoardPane.getPrefHeight();

    this.overlayPane.setPrefSize(boardDisplayWidth, boardDisplayHeight);
    this.overlayPane.setMinSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
    this.overlayPane.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);

    this.tokenPane.setPrefSize(boardDisplayWidth, boardDisplayHeight);
    this.tokenPane.setMinSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
    this.tokenPane.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);

    this.boardStack.getChildren().addAll(this.overlayPane, this.tokenPane);

    this.boardStack.setPrefSize(boardDisplayWidth, boardDisplayHeight);
    this.boardStack.setMinSize(boardDisplayWidth, boardDisplayHeight);
    this.boardStack.setMaxSize(boardDisplayWidth, boardDisplayHeight);

    PlayerView currentPlayer = getCurrentPlayer();
    playerTurnBox = playerUIService.createCurrentPlayerTurnBox(currentPlayer);

    diceContainer.setAlignment(Pos.CENTER);
    diceContainer.setPadding(new Insets(10));
    diceContainer.getStyleClass().add("dice-box");
    diceContainer.setMinHeight(220);
    diceService.initializeDice(diceContainer);

    rollButton.getStyleClass().add("roll-dice-button");
    playAgainButton.getStyleClass().add("play-again-button");

    Button howToButton = createHowToPlayButton(
        "How to play - Snakes & Ladders",
        """
            - Roll the dice and be the first to the goal!
            - If you land on an enemy player(s), they are sent back to start.
            - Roll a pair for an extra turn.
            - If you roll a pair of six, your turn is skipped!
            - Be aware of the snakes and go for the ladders!
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

    rootLayout.setCenter(boardStack);
    rootLayout.setRight(controlPanel);
    rootLayout.setPadding(new Insets(10, 15, 10, 15));
    rootLayout.getStyleClass().add("page-background");

    addNavigationAndHelpToBorderPane(rootLayout, true, howToButton);

    BorderPane.setAlignment(boardStack, Pos.CENTER);
    BorderPane.setMargin(boardStack, new Insets(0, 10, 0, 0));
    BorderPane.setMargin(controlPanel, new Insets(0, 0, 0, 10));
  }

  /**
   * Gets the current player who has the turn (if any).
   * 
   * @return an Optional containing the current player, or empty if no one has a
   *         turn
   */
  private Optional<PlayerView> getOptCurrentPlayer() {
    return currentPlayers.stream().filter(PlayerView::hasTurn).findFirst();
  }

  /**
   * Gets the current player who has the turn (if any).
   * 
   * @return the current player, or null if no one has a turn
   */
  private PlayerView getCurrentPlayer() {
    return currentPlayers.stream().filter(PlayerView::hasTurn).findFirst().orElse(null);
  }

  /**
   * Updates the player turn display to show whose turn it is.
   * Refreshes the turn indicator box with the current player's information.
   */
  private void updatePlayerTurnDisplay() {
    PlayerView currentPlayer = getOptCurrentPlayer().orElse(null);
    playerUIService.updateCurrentPlayerTurnBox(playerTurnBox, currentPlayer, null);
  }

  /**
   * Creates and returns the scene for this view.
   * Sets up the scene with proper dimensions and applies the stylesheet.
   *
   * @return a Scene containing this view's UI
   */
  @Override
  public Scene getScene() {
    Scene scene = new Scene(rootLayout, 1200, 800);
    scene.getStylesheets().add(getClass().getResource(ResourcePaths.STYLE_SHEET).toExternalForm());
    return scene;
  }

  /**
   * Sets up the players and overlays on the board.
   * Clears existing tokens, creates new ones for each player,
   * positions them correctly, and adds any board overlays (like snakes/ladders).
   *
   * @param players  list of players to display on the board
   * @param overlays list of overlay elements to add to the board
   */
  @Override
  public void setPlayers(List<PlayerView> players, List<OverlayParams> overlays) {
    this.currentPlayers = new ArrayList<>(players);

    this.tokenPane.getChildren().clear();
    tokenImages.clear();

    for (PlayerView player : players) {
      ImageView tokenImage = playerUIService.createTokenImage(player.playerToken());
      tokenImages.put(player.playerToken(), tokenImage);

      if (player.tileId() > 0) {
        boardUIService.placeTokenOnTile(this.tokenPane, tokenImage, player.tileId());
      } else {
        boardUIService.placeTokenAtStart(this.tokenPane, tokenImage);
      }
    }
    boardUIService.addOverlays(this.overlayPane, overlays);
    updatePlayerTurnDisplay();
  }

  /**
   * Handles dice roll events from the game model.
   * Shows the dice results, calculates movement, and triggers animations.
   * Also handles special cases like double rolls and winning conditions.
   *
   * @param data the dice roll data from the model
   */
  @Override
  protected void handleDiceRolled(Object data) {
    if (hasActiveAnimation)
      return;

    int[] diceValues = diceService.parseDiceRoll(data);
    if (diceValues.length < 2) {
      return;
    }
    diceService.showDice(diceContainer, diceValues);

    Optional<PlayerView> currentPlayer = currentPlayers.stream().filter(PlayerView::hasTurn).findFirst();

    if (currentPlayer.isPresent()) {
      PlayerView player = currentPlayer.get();

      int startPosition = player.tileId();
      int rolled = diceValues[0] + diceValues[1];
      int endPosition = Math.min(startPosition + rolled, boardSize);

      String message = player.playerName() + " rolled " + rolled + "! ";
      if (diceValues[0] == diceValues[1]) {
        if (diceValues[0] == 6) {
          message += "Double 6 - turn skipped!";
        } else {
          message += "Got a double - gets an extra turn!";
        }
      }

      playerUIService.updateCurrentPlayerTurnBox(playerTurnBox, player, message);

      if (rolled != 12) {
        hasActiveAnimation = true;

        animationService.animateMove(
            player.playerToken(),
            startPosition,
            endPosition,
            () -> {
              hasActiveAnimation = false;

              // Check for winner
              if (gateway != null && gateway.hasWinner()) {
                String winnerName = player.playerName();
                announceWinner(winnerName);
              } else {
                enableRollButton();
              }
            });
      } else {
        enableRollButton();
      }
    }
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
   * Handles player movement events from the game model.
   * Animates the player token moving from one tile to another.
   *
   * @param data the player movement data
   */
  @Override
  protected void handlePlayerMoved(Object data) {
    if (hasActiveAnimation)
      return;

    if (data instanceof BoardGame.PlayerMoveData moveData) {
      String token = moveData.getPlayer().getToken().name();
      int fromId = moveData.getFromTile().tileId();
      int toId = moveData.getToTile().tileId();

      hasActiveAnimation = true;

      animationService.animateMove(
          token,
          fromId,
          toId,
          () -> {
            hasActiveAnimation = false;
            enableRollButton();
          });
    }
  }

  /**
   * Handles winner declaration events.
   * Shows a congratulations message for the winning player.
   *
   * @param data the winner data
   */
  @Override
  protected void handleWinnerDeclared(Object data) {
    if (data instanceof Player winner) {
      announceWinner(winner.getName());
    }
  }

  /**
   * Handles game reset events.
   * Refreshes the board display and re-enables game controls.
   */
  @Override
  protected void handleGameReset() {
    if (gateway != null) {
      setPlayers(gateway.players(), gateway.boardOverlays());
      enableRollButton();
    }
  }

  /**
   * Handles turn change events.
   * Updates the display to show whose turn it is now.
   *
   * @param data the turn change data
   */
  @Override
  protected void handleTurnChanged(Object data) {
    if (hasActiveAnimation) {
      return;
    }
    updatePlayerTurnDisplay();
  }

  /**
   * Shows a single die value on the dice display.
   * 
   * @param values the die value to show
   */
  @Override
  public void showDice(int values) {
    diceService.showDice(diceContainer, new int[] { values });
  }

  /**
   * Shows two dice values on the dice display.
   * 
   * @param value1 the first die value
   * @param value2 the second die value
   */
  public void showDice(int value1, int value2) {
    diceService.showDice(diceContainer, new int[] { value1, value2 });
  }

  /**
   * Announces the winner of the game.
   * Disables game controls and shows a congratulations dialog.
   *
   * @param name the name of the winning player
   */
  @Override
  public void announceWinner(String name) {
    disableRollButton();
    Dialogs.info("Winner!", "Congratulations, " + name + "! You won the game");
  }

  /**
   * Connects this view to the game model.
   * Registers this view as an observer to receive game events.
   *
   * @param gateway the game gateway to observe
   */
  @Override
  public void connectToModel(GameGateway gateway) {
    gateway.addObserver(this);
  }

  /**
   * Enables the roll dice button.
   * Only enables if there's no active animation running.
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
   * Disables the roll dice button.
   * Used during animations or when the game is over.
   */
  @Override
  public void disableRollButton() {
    Platform.runLater(() -> rollButton.setDisable(true));
  }

  /**
   * Animates a token moving from one tile to another.
   * Prevents multiple animations from running at once and calls the
   * provided callback when the animation completes.
   *
   * @param tokenName  the name of the token to animate
   * @param startId    the starting tile ID
   * @param endId      the ending tile ID
   * @param onFinished callback to run when animation completes
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

  /**
   * Applies special styling to snake and ladder tiles.
   * This method highlights the special tiles on the board to make
   * snakes and ladders more visible to players.
   *
   * @param snakes  map of snake head positions to tail positions
   * @param ladders map of ladder bottom positions to top positions
   */
  public void applySpecialStylingWhenReady(
      Map<Integer, Integer> snakes, Map<Integer, Integer> ladders) {
    // Store the data for application when the stage is shown
    final Map<Integer, Integer> finalSnakes = new HashMap<>(snakes);
    final Map<Integer, Integer> finalLadders = new HashMap<>(ladders);

    Platform.runLater(
        () -> {
          if (boardUIService instanceof SnlBoardUIService snlBoardUIService) {

            snlBoardUIService.applySpecialTileStyling(finalSnakes, finalLadders, overlayPane);
          }
        });
  }

  /**
   * Gets the board UI service used by this view.
   * 
   * @return the board UI service
   */
  public BoardUIService getBoardUIService() {
    return boardUIService;
  }

  /**
   * Gets the overlay pane where special elements are drawn.
   * 
   * @return the overlay pane
   */
  public Pane getOverlayPane() {
    return overlayPane;
  }
}