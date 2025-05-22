package edu.ntnu.idatt2003.ui.ludo.view;

import edu.games.engine.model.BoardGame.PlayerMoveData;
import edu.games.engine.model.Player;
import edu.ntnu.idatt2003.gateway.GameGateway;
import edu.ntnu.idatt2003.gateway.view.PlayerView;
import edu.ntnu.idatt2003.ui.common.view.AbstractGameView;
import edu.ntnu.idatt2003.ui.common.view.GameView;
import edu.ntnu.idatt2003.ui.fx.OverlayParams;
import edu.ntnu.idatt2003.ui.service.animation.AnimationService;
import edu.ntnu.idatt2003.ui.service.board.LudoBoardUIService;
import edu.ntnu.idatt2003.ui.service.dice.DiceService;
import edu.ntnu.idatt2003.ui.service.player.PlayerUIService;
import edu.ntnu.idatt2003.ui.shared.view.ViewServiceFactory;
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

  // State
  private Consumer<Integer> pieceSelectedCallback;
  private boolean hasActiveAnimation = false;
  private List<PlayerView> players = new ArrayList<>();
  private int lastRoll = 0;
  private final Map<String, List<ImageView>> tokenImages = new HashMap<>();

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

  @Override
  public Scene getScene() {
    Scene scene = new Scene(rootLayout, 1100, 750);
    scene.getStylesheets().add(getClass().getResource(ResourcePaths.STYLE_SHEET).toExternalForm());
    return scene;
  }

  @Override
  public void setPlayers(List<PlayerView> players, List<OverlayParams> overlays) {
    this.players = new ArrayList<>(players);

    this.tokenPane.getChildren().clear();
    tokenImages.clear();

    for (PlayerView player : players) {
      List<ImageView> pieces = playerUIService.createPlayerPieces(player);
      tokenImages.put(player.token(), pieces);

      for (int i = 0; i < pieces.size(); i++) {
        final int pieceIndex = i;
        ImageView piece = pieces.get(i);
        piece.setOnMouseClicked(e -> handlePieceClicked(player.token(), pieceIndex));
        this.tokenPane.getChildren().add(piece);
      }
      updatePiecePositions(player);
    }

    boardUIService.addOverlays(this.overlayPane, overlays);
    updateStatusForCurrentPlayer();
  }

  private void updatePiecePositions(PlayerView player) {
    List<ImageView> pieces = tokenImages.get(player.token());
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
        boardUIService.placePieceAtHome(this.tokenPane, piece, player.token(), i);
      } else {
        boardUIService.placePieceOnBoard(this.tokenPane, piece, position, i);
      }
    }
  }

  private void handlePieceClicked(String tokenName, int pieceIndex) {
    // Only respond to clicks from the current player's pieces
    PlayerView currentPlayer = getCurrentPlayer();
    if (currentPlayer == null || !currentPlayer.token().equals(tokenName)) {
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

  private PlayerView getCurrentPlayer() {
    return players.stream().filter(PlayerView::hasTurn).findFirst().orElse(null);
  }

  private void updateStatusForCurrentPlayer() {
    PlayerView currentPlayer = getCurrentPlayer();
    if (currentPlayer != null) {
      showStatusMessage(currentPlayer.name() + "'s turn");
    } else {
      showStatusMessage("Roll the dice to start");
    }
  }

  @Override
  protected void handleDiceRolled(Object data) {
    if (hasActiveAnimation)
      return;

    int[] diceValues = diceService.parseDiceRoll(data);
    if (diceValues.length == 0)
      return;

    int dieValue = diceValues[0];
    lastRoll = dieValue;

    // Show the die
    diceService.showDice(diceContainer, diceValues);

    // Update UI
    disableRollButton(); // Disable until animation completes or player makes a move

    // For Ludo, after rolling, player might need to select a piece
    PlayerView currentPlayer = getCurrentPlayer();
    if (currentPlayer != null) {
      // Special case: Check if no valid moves are available
      boolean hasValidMove = checkForValidMoves(currentPlayer, dieValue);

      if (!hasValidMove) {
        // No valid moves, proceed to next player
        showStatusMessage("No valid moves available. Next player's turn.");
        enableRollButton();
      } else if (dieValue == 6) {
        // Player rolled a 6, which has special meaning in Ludo
        showStatusMessage(currentPlayer.name() + " rolled a 6! Select a piece to move.");
      } else {
        // Normal roll
        showStatusMessage(
            currentPlayer.name() + " rolled a " + dieValue + ". Select a piece to move.");
      }
    }
  }

  private boolean checkForValidMoves(PlayerView player, int roll) {
    // Check if any pieces can move
    // For pieces at home, need a 6 to move out
    // For pieces on board, always valid
    return player.piecePositions().stream().anyMatch(pos -> pos > 0 || (pos <= 0 && roll == 6));
  }

  @Override
  protected void handlePlayerMoved(Object data) {
    if (hasActiveAnimation)
      return;

    try {
      hasActiveAnimation = true;

      if (data instanceof PlayerMoveData moveData) {
        String tokenName = moveData.getPlayer().getToken().name();
        int fromId = moveData.getFromTile() != null ? moveData.getFromTile().id() : 0;
        int toId = moveData.getToTile() != null ? moveData.getToTile().id() : 0;

        // Find piece index
        int pieceIndex = findPieceIndex(tokenName, fromId);

        // Build path
        List<Integer> path = new ArrayList<>();
        if (fromId > 0)
          path.add(fromId);
        path.add(toId);

        // Animate the move
        animatePlayerMove(tokenName, pieceIndex, path);
      }
    } finally {
      hasActiveAnimation = false;
      enableRollButton();
    }
  }

  private void animatePlayerMove(String tokenName, int pieceIndex, List<Integer> path) {
    List<ImageView> tokens = tokenImages.get(tokenName);
    if (tokens == null || pieceIndex >= tokens.size() || path.isEmpty()) {
      return;
    }

    ImageView token = tokens.get(pieceIndex);

    // Animate the move along the path
    animationService.animateMoveAlongPath(tokenName, pieceIndex, path, this::refreshTokens);
  }

  private int findPieceIndex(String token, int tileId) {
    for (PlayerView player : players) {
      if (player.token().equals(token)) {
        // Find which piece is at the given tile
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

  @Override
  protected void handleWinnerDeclared(Object data) {
    if (data instanceof Player winner) {
      announceWinner(winner.getName());
    }
  }

  @Override
  protected void handleGameReset() {
    setPlayers(gateway.players(), gateway.boardOverlays());
    showStatusMessage("Roll the dice to start");
    enableRollButton();
  }

  @Override
  protected void handleTurnChanged(Object data) {
    if (data instanceof Player player) {
      String tokenName = player.getToken().name();

      System.out.println("Turn changed to: " + player.getName() + " (" + tokenName + ")");

      // Update all players and their statuses
      for (PlayerView pv : players) {
        // Check if this is the new current player
        boolean isCurrentPlayer = pv.token().equals(tokenName);

        // Update the UI accordingly
        if (isCurrentPlayer) {
          showStatusMessage(pv.name() + "'s turn");
        }

        // Update piece highlights for the current player
        updatePieceHighlights(pv, isCurrentPlayer);
      }
    }
    enableRollButton();
  }

  private void updatePieceHighlights(PlayerView player, boolean isCurrentPlayer) {
    List<ImageView> pieces = tokenImages.get(player.token());
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

  private void refreshTokens() {
    // This would need to fetch updated player positions from the model
    // For now, we'll just refresh what we have
    for (PlayerView player : players) {
      updatePiecePositions(player);
    }
  }

  @Override
  public void showDice(int value) {
    diceService.showDice(diceContainer, new int[] { value });
    lastRoll = value;
  }

  @Override
  public void announceWinner(String name) {
    disableRollButton();
    Dialogs.info("Winner!", "Congratulations, " + name + "! You won the game!");
  }

  public void setPieceSelectedCallback(Consumer<Integer> callback) {
    this.pieceSelectedCallback = callback;
  }

  public void showAlert(String title, String message) {
    Alert alert = new Alert(Alert.AlertType.INFORMATION);
    alert.setTitle(title);
    alert.setHeaderText(null);
    alert.setContentText(message);
    alert.showAndWait();
  }

  public int getLastRoll() {
    return lastRoll;
  }

  @Override
  public void connectToModel(GameGateway gateway) {
    gateway.addObserver(this);
  }

  public void showStatusMessage(String message) {
    PlayerView currentPlayer = getCurrentPlayer();
    playerUIService.updateCurrentPlayerTurnBox(playerTurnBox, currentPlayer, message);
  }

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

  public void setPlayers(List<PlayerView> players) {
    setPlayers(players, new ArrayList<>()); // Empty overlays list
  }

  public void setOverlays(List<OverlayParams> overlays) {
    boardUIService.addOverlays(overlayPane, overlays);
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
