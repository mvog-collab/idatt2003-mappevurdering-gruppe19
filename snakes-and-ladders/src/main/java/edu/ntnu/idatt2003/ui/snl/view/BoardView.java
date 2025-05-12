package edu.ntnu.idatt2003.ui.snl.view;

import edu.games.engine.model.BoardGame;
import edu.games.engine.model.Player;
import edu.ntnu.idatt2003.gateway.GameGateway;
import edu.ntnu.idatt2003.gateway.view.PlayerView;
import edu.ntnu.idatt2003.ui.common.view.AbstractGameView;
import edu.ntnu.idatt2003.ui.fx.OverlayParams;
import edu.ntnu.idatt2003.ui.service.animation.AnimationService;
import edu.ntnu.idatt2003.ui.service.board.BoardUIService;
import edu.ntnu.idatt2003.ui.service.dice.DiceService;
import edu.ntnu.idatt2003.ui.service.player.PlayerUIService;
import edu.ntnu.idatt2003.ui.shared.view.ViewServiceFactory;
import edu.ntnu.idatt2003.utils.Dialogs;
import edu.ntnu.idatt2003.utils.ResourcePaths;
import java.util.*;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;

public class BoardView extends AbstractGameView {
  // Services
  private final BoardUIService boardUIService;
  private final PlayerUIService playerUIService;
  private AnimationService animationService;
  private final DiceService diceService;

  // UI Components
  private final BorderPane mainLayout;
  private StackPane boardStack; // Added this to hold all board components
  private Pane boardPane;
  private final Pane tokenPane;
  private final Pane overlayPane;
  private final VBox controlPanel;
  private final HBox diceContainer;
  private FlowPane playerPanel;

  // State
  private final int boardSize;
  private final Map<String, ImageView> tokenImages = new HashMap<>();
  private boolean hasActiveAnimation = false;
  private List<PlayerView> currentPlayers = new ArrayList<>();

  public BoardView(
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

    // Initialize UI components
    this.boardStack = new StackPane(); // Initialize the stack pane
    this.tokenPane = new Pane();
    this.overlayPane = new Pane();
    this.controlPanel = new VBox(20);
    this.diceContainer = new HBox(10);
    this.mainLayout = new BorderPane();

    // Create roll and play again buttons
    this.rollButton = new Button("Roll Dice");
    this.playAgainButton = new Button("Play Again");

    // Build the UI
    buildUI();
  }

  public BoardView(int boardSize) {
    this(
        boardSize,
        ViewServiceFactory.createBoardUIService("SNL", boardSize),
        ViewServiceFactory.createPlayerUIService("SNL"),
        null, // Will be initialized properly after we have coordinates
        ViewServiceFactory.createDiceService("SNL"));

    // Complete initialization of services after we have coordinates
    Map<Integer, Point2D> coordinates = boardUIService.getTileCoordinates();
    AnimationService newAnimationService =
        ViewServiceFactory.createAnimationService("SNL", coordinates, tokenPane);

    // Replace the null animation service with the proper one
    this.animationService = newAnimationService;
  }

  private void buildUI() {
    // Create the board pane using the service
    boardStack = boardUIService.createBoardPane(boardSize);

    // Keep a reference to the actual board grid (first child of boardStack)
    if (!boardStack.getChildren().isEmpty()) {
      boardPane = (Pane) boardStack.getChildren().get(0);
    } else {
      boardPane = new GridPane(); // Fallback
      boardStack.getChildren().add(boardPane);
    }

    // Get the dimensions to ensure consistent sizing
    double boardWidth = boardPane.getPrefWidth();
    double boardHeight = boardPane.getPrefHeight();

    // Ensure the stack pane has the correct size
    boardStack.setPrefSize(boardWidth, boardHeight);
    boardStack.setMinSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
    boardStack.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);

    // Configure overlay and token panes with explicit pref/min/max sizes
    overlayPane.setPrefSize(boardWidth, boardHeight);
    overlayPane.setMinSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
    overlayPane.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);

    tokenPane.setPrefSize(boardWidth, boardHeight);
    tokenPane.setMinSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
    tokenPane.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);

    // Clear and rebuild the stack with all components in the correct order
    boardStack.getChildren().clear();
    boardStack.getChildren().addAll(boardPane, overlayPane, tokenPane);

    // Create player panel
    playerPanel = new FlowPane();
    playerPanel.setHgap(15);
    playerPanel.setVgap(15);
    playerPanel.setPrefWrapLength(300);

    // Set up dice area
    diceContainer.setAlignment(Pos.CENTER);
    diceContainer.setPadding(new Insets(20));
    diceContainer.getStyleClass().add("dice-box");

    // Initialize dice
    diceService.initializeDice(diceContainer);

    // Set up buttons
    rollButton.getStyleClass().add("roll-dice-button");
    playAgainButton.getStyleClass().add("play-again-button");

    HBox buttonContainer = new HBox(10, rollButton, playAgainButton);
    buttonContainer.setAlignment(Pos.CENTER);

    // Add everything to control panel
    controlPanel.getChildren().addAll(playerPanel, diceContainer, buttonContainer);
    controlPanel.setAlignment(Pos.TOP_CENTER);
    controlPanel.setPrefWidth(400);
    controlPanel.getStyleClass().add("game-control");

    // Set up main layout
    mainLayout.setCenter(boardStack);
    mainLayout.setRight(controlPanel);
    mainLayout.setPadding(new Insets(20));
    mainLayout.getStyleClass().add("page-background");

    // Add border padding
    BorderPane.setMargin(boardStack, new Insets(0, 20, 0, 0));
  }

  @Override
  public Scene getScene() {
    Scene scene = new Scene(mainLayout, 1000, 700);
    scene.getStylesheets().add(getClass().getResource(ResourcePaths.STYLE_SHEET).toExternalForm());
    return scene;
  }

  @Override
  public void setPlayers(List<PlayerView> players, List<OverlayParams> overlays) {
    // Store current players
    this.currentPlayers = new ArrayList<>(players);

    // Clear existing player UI
    playerPanel.getChildren().clear();

    // Clear token pane and token images
    tokenPane.getChildren().clear();
    tokenImages.clear();

    // Create player boxes
    for (PlayerView player : players) {
      VBox playerBox = (VBox) playerUIService.createPlayerBox(player, player.hasTurn());
      playerPanel.getChildren().add(playerBox);

      // Create token image and add to token pane
      ImageView tokenImage = playerUIService.createTokenImage(player.token());
      tokenImages.put(player.token(), tokenImage);

      // Place token on board
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
            player.token(),
            startPosition,
            endPosition,
            () -> {
              hasActiveAnimation = false;

              // Check for winner
              if (gateway != null && gateway.hasWinner()) {
                String winnerName = player.name();
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
      int fromId = moveData.getFromTile().id();
      int toId = moveData.getToTile().id();

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
        boolean isCurrentPlayer = player.token().equals(currentToken);
        Node playerBox =
            playerPanel.getChildren().stream()
                .filter(
                    node -> {
                      if (node instanceof VBox box) {
                        return box.getUserData() != null
                            && box.getUserData().equals(player.token());
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

  public void showDice(int value1, int value2) {
    diceService.showDice(diceContainer, new int[] {value1, value2});
  }
}
