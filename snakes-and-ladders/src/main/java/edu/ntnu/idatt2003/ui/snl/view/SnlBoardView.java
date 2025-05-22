package edu.ntnu.idatt2003.ui.snl.view;

import edu.games.engine.model.BoardGame;
import edu.games.engine.model.Player;
import edu.ntnu.idatt2003.gateway.GameGateway;
import edu.ntnu.idatt2003.gateway.view.PlayerView;
import edu.ntnu.idatt2003.ui.common.view.AbstractGameView;
import edu.ntnu.idatt2003.ui.fx.OverlayParams;
import edu.ntnu.idatt2003.ui.service.animation.AnimationService;
import edu.ntnu.idatt2003.ui.service.board.BoardUIService;
import edu.ntnu.idatt2003.ui.service.board.SnlBoardUIService;
import edu.ntnu.idatt2003.ui.service.dice.DiceService;
import edu.ntnu.idatt2003.ui.service.player.PlayerUIService;
import edu.ntnu.idatt2003.ui.shared.view.ViewServiceFactory;
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

  private BorderPane rootLayout; // Change mainLayout to BorderPane and use as root
  private StackPane boardStack;
  private Pane boardPane;
  private final Pane tokenPane;
  private final Pane overlayPane;
  private final VBox controlPanel;
  private final HBox diceContainer;
  private FlowPane playerPanel;

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

    this.rootLayout = new BorderPane(); // Initialize rootLayout
    this.boardStack = new StackPane();
    this.tokenPane = new Pane();
    this.overlayPane = new Pane();
    this.controlPanel = new VBox(20);
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
    AnimationService newAnimationService =
        ViewServiceFactory.createAnimationService("SNL", coordinates, tokenPane);
    this.animationService = newAnimationService;
  }

  private void buildUI() {
    boardStack = boardUIService.createBoardPane(boardSize);
    if (!boardStack.getChildren().isEmpty()) {
      boardPane = (Pane) boardStack.getChildren().get(0);
    } else {
      boardPane = new GridPane();
      boardStack.getChildren().add(boardPane);
    }

    double boardWidth = boardPane.getPrefWidth();
    double boardHeight = boardPane.getPrefHeight();
    boardStack.setPrefSize(boardWidth, boardHeight);
    boardStack.setMinSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
    boardStack.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
    overlayPane.setPrefSize(boardWidth, boardHeight);
    overlayPane.setMinSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
    overlayPane.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
    tokenPane.setPrefSize(boardWidth, boardHeight);
    tokenPane.setMinSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
    tokenPane.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
    boardStack.getChildren().clear();
    boardStack.getChildren().addAll(boardPane, overlayPane, tokenPane);

    playerPanel = new FlowPane();
    playerPanel.setHgap(15);
    playerPanel.setVgap(15);
    playerPanel.setPrefWrapLength(300);

    diceContainer.setAlignment(Pos.CENTER);
    diceContainer.setPadding(new Insets(20));
    diceContainer.getStyleClass().add("dice-box");
    diceContainer.setPrefSize(250, 250);
    diceContainer.setMinSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
    diceContainer.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
    diceService.initializeDice(diceContainer);

    rollButton.getStyleClass().add("roll-dice-button");
    playAgainButton.getStyleClass().add("play-again-button");

    Button howToButton =
        createHowToPlayButton(
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

    controlPanel.getChildren().addAll(howToButton, playerPanel, diceContainer, buttonContainer);
    controlPanel.setAlignment(Pos.TOP_CENTER);
    controlPanel.setPrefWidth(400);
    controlPanel.getStyleClass().add("game-control");

    // Set up rootLayout (BorderPane)
    rootLayout.setCenter(boardStack);
    rootLayout.setRight(controlPanel);
    rootLayout.setPadding(new Insets(20));
    rootLayout.getStyleClass().add("page-background"); // Add main-box if needed

    // Add navigation controls to the top of the BorderPane
    addNavigationAndHelpToBorderPane(
        rootLayout, true, howToButton); // true = include "Back to Game Setup"

    BorderPane.setMargin(boardStack, new Insets(0, 20, 0, 0));
  }

  @Override
  public Scene getScene() {
    Scene scene = new Scene(rootLayout, 1000, 700); // Use rootLayout
    scene.getStylesheets().add(getClass().getResource(ResourcePaths.STYLE_SHEET).toExternalForm());
    return scene;
  }

  @Override
  public void setPlayers(List<PlayerView> players, List<OverlayParams> overlays) {
    // Store current players
    this.currentPlayers = new ArrayList<>(players);

    // Clear existing player UI
    playerPanel.getChildren().clear();

    // Clear playerToken pane and playerToken images
    tokenPane.getChildren().clear();
    tokenImages.clear();

    // Create player boxes
    for (PlayerView player : players) {
      VBox playerBox = (VBox) playerUIService.createPlayerBox(player, player.hasTurn());
      playerPanel.getChildren().add(playerBox);

      // Create playerToken image and add to playerToken pane
      ImageView tokenImage = playerUIService.createTokenImage(player.playerToken());
      tokenImages.put(player.playerToken(), tokenImage);

      // Place playerToken on board
      if (player.tileId() > 0) {
        boardUIService.placeTokenOnTile(tokenPane, tokenImage, player.tileId());
      } else {
        boardUIService.placeTokenAtStart(tokenPane, tokenImage);
      }
    }

    // Add overlays to overlay pane
    boardUIService.addOverlays(overlayPane, overlays);
  }

  @Override
  protected void handleDiceRolled(Object data) {
    if (hasActiveAnimation) return;

    // Parse dice values
    int[] diceValues = diceService.parseDiceRoll(data);
    if (diceValues.length < 2) return;

    // Show dice
    diceService.showDice(diceContainer, diceValues);

    // Find current player
    Optional<PlayerView> currentPlayer =
        currentPlayers.stream().filter(PlayerView::hasTurn).findFirst();

    if (currentPlayer.isPresent()) {
      PlayerView player = currentPlayer.get();

      // Calculate move
      int startPosition = player.tileId();
      int rolled = diceValues[0] + diceValues[1];
      int endPosition = Math.min(startPosition + rolled, boardSize);

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
    if (hasActiveAnimation) return;

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
    if (hasActiveAnimation) return;

    if (data instanceof Player currentPlayer) {
      String currentToken = currentPlayer.getToken().name();

      // Update player UI to show current player
      for (PlayerView player : currentPlayers) {
        boolean isCurrentPlayer = player.playerToken().equals(currentToken);
        Node playerBox =
            playerPanel.getChildren().stream()
                .filter(
                    node -> {
                      if (node instanceof VBox box) {
                        return box.getUserData() != null
                            && box.getUserData().equals(player.playerToken());
                      }
                      return false;
                    })
                .findFirst()
                .orElse(null);

        if (playerBox != null) {
          playerUIService.updateTurnIndicator(playerBox, isCurrentPlayer);
        }
      }
    }
  }

  @Override
  public void showDice(int values) {
    diceService.showDice(diceContainer, new int[] {values});
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
      if (onFinished != null) onFinished.run();
      return;
    }

    hasActiveAnimation = true;
    animationService.animateMove(
        tokenName,
        startId,
        endId,
        () -> {
          hasActiveAnimation = false;
          if (onFinished != null) onFinished.run();
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
    diceService.showDice(diceContainer, new int[] {value1, value2});
  }

  public BoardUIService getBoardUIService() {
    return boardUIService;
  }

  public Pane getOverlayPane() {
    return overlayPane;
  }
}
