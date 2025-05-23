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

public class SnlBoardView extends AbstractGameView { // AbstractGameView should extend AbstractView
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

  private void buildUI() {
    // boardUIService.createBoardPane returns a StackPane containing the GridPane
    // (visual board)
    this.boardStack = boardUIService.createBoardPane(boardSize);
    Pane visualBoardPane = (Pane) this.boardStack.getChildren().get(0); // This is the GridPane

    double boardDisplayWidth = visualBoardPane.getPrefWidth(); // Assuming service sets prefSize on GridPane
    double boardDisplayHeight = visualBoardPane.getPrefHeight();

    // Configure the view's overlayPane and tokenPane
    this.overlayPane.setPrefSize(boardDisplayWidth, boardDisplayHeight);
    this.overlayPane.setMinSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE); // Allow shrinking
    this.overlayPane.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE); // Allow growing

    this.tokenPane.setPrefSize(boardDisplayWidth, boardDisplayHeight);
    this.tokenPane.setMinSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
    this.tokenPane.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);

    // Add the view's overlay and token panes to the boardStack from the service
    this.boardStack.getChildren().addAll(this.overlayPane, this.tokenPane);

    // Ensure the main boardStack itself has its size fixed
    this.boardStack.setPrefSize(boardDisplayWidth, boardDisplayHeight);
    this.boardStack.setMinSize(boardDisplayWidth, boardDisplayHeight);
    this.boardStack.setMaxSize(boardDisplayWidth, boardDisplayHeight);

    PlayerView currentPlayer = getCurrentPlayer(); // Can be null
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

  private Optional<PlayerView> getOptCurrentPlayer() {
    return currentPlayers.stream().filter(PlayerView::hasTurn).findFirst();
  }

  private PlayerView getCurrentPlayer() {
    return currentPlayers.stream().filter(PlayerView::hasTurn).findFirst().orElse(null);
  }

  private void updatePlayerTurnDisplay() {
    PlayerView currentPlayer = getOptCurrentPlayer().orElse(null);
    playerUIService.updateCurrentPlayerTurnBox(playerTurnBox, currentPlayer, null);
  }

  @Override
  public Scene getScene() {
    Scene scene = new Scene(rootLayout, 1200, 800);
    scene.getStylesheets().add(getClass().getResource(ResourcePaths.STYLE_SHEET).toExternalForm());
    return scene;
  }

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
    boardUIService.addOverlays(this.overlayPane, overlays); // Use the view's overlayPane
    updatePlayerTurnDisplay();
  }

  @Override
  protected void handleDiceRolled(Object data) {
    if (hasActiveAnimation)
      return;

    // Parse dice values
    int[] diceValues = diceService.parseDiceRoll(data);
    if (diceValues.length < 2) {
      return;
    }
    // Show dice
    diceService.showDice(diceContainer, diceValues);

    // Find current player
    Optional<PlayerView> currentPlayer = currentPlayers.stream().filter(PlayerView::hasTurn).findFirst();

    if (currentPlayer.isPresent()) {
      PlayerView player = currentPlayer.get();

      // Calculate move
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

      // Animate move (unless it's special case)
      if (rolled != 12) { // Example special case
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

  @Override
  protected void handleWinnerDeclared(Object data) {
    if (data instanceof Player winner) {
      announceWinner(winner.getName());
    }
  }

  @Override
  protected void handleGameReset() {
    if (gateway != null) {
      setPlayers(gateway.players(), gateway.boardOverlays());
      enableRollButton();
    }
  }

  @Override
  protected void handleTurnChanged(Object data) {
    if (hasActiveAnimation) {
      return;
    }
    updatePlayerTurnDisplay();
  }

  @Override
  public void showDice(int values) {
    diceService.showDice(diceContainer, new int[] { values });
  }

  @Override
  public void announceWinner(String name) {
    disableRollButton();
    Dialogs.info("Winner!", "Congratulations, " + name + "! You won the game");
  }

  @Override
  public void connectToModel(GameGateway gateway) {
    gateway.addObserver(this);
  }

  @Override
  public void enableRollButton() {
    Platform.runLater(
        () -> {
          if (!hasActiveAnimation) {
            rollButton.setDisable(false);
          }
        });
  }

  @Override
  public void disableRollButton() {
    Platform.runLater(() -> rollButton.setDisable(true));
  }

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

  public void showDice(int value1, int value2) {
    diceService.showDice(diceContainer, new int[] { value1, value2 });
  }

  public BoardUIService getBoardUIService() {
    return boardUIService;
  }

  public Pane getOverlayPane() {
    return overlayPane;
  }
}
