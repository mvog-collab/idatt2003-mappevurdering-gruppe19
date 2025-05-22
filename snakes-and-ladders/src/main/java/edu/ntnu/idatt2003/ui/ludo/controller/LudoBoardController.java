package edu.ntnu.idatt2003.ui.ludo.controller;

import edu.games.engine.model.LudoColor;
import edu.ntnu.idatt2003.gateway.CompleteBoardGame;
import edu.ntnu.idatt2003.gateway.LudoGateway;
import edu.ntnu.idatt2003.gateway.view.PlayerView;
import edu.ntnu.idatt2003.ui.common.controller.AbstractGameController;
import edu.ntnu.idatt2003.ui.ludo.view.LudoBoardView;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.logging.Level;


public final class LudoBoardController extends AbstractGameController<LudoBoardView> {

  private int lastRolledValue = 0;
  private int selectedPieceIndex = -1;
  private boolean waitingForPieceSelection = false;
  private static final int GOAL_LENGTH = 5;
  private static final Logger LOG = Logger.getLogger(LudoBoardController.class.getName());

  public LudoBoardController(LudoBoardView view, CompleteBoardGame gateway) {
    super(view, gateway);

    view.setPieceSelectedCallback(this::onPieceSelected);
    refreshTokens();
  }

  /* -------- gameplay ---------- */

  @Override
  protected void onRollDice() {
    view.disableRollButton();

    PlayerView currentPlayer = getCurrentPlayer();
    if (currentPlayer == null) {
      view.enableRollButton(); // Should not happen if game is active
      return;
    }

    // 1. Roll the dice and update UI
    lastRolledValue = gateway.rollDice();
    view.showDice(lastRolledValue);
    LOG.log(Level.INFO, () -> currentPlayer.playerName() + " rolled: " + lastRolledValue);

    // 2. Determine player's piece status
    LudoColor color = LudoColor.valueOf(currentPlayer.playerToken());
    boolean hasHomePieces = playerHasPiecesAtHome(currentPlayer);
    boolean hasMoveableBoardPieces = playerHasMoveablePiecesOnBoard(currentPlayer, color);

    // 3. Handle different roll scenarios
    if (lastRolledValue == 6) {
      handleRollOfSix(currentPlayer, hasHomePieces, hasMoveableBoardPieces);
    } else {
      handleNormalRoll(currentPlayer, hasMoveableBoardPieces, hasHomePieces);
    }
  }

  private boolean playerHasPiecesAtHome(PlayerView player) {
    return player.piecePositions().stream().anyMatch(pos -> pos <= 0);
  }

  private boolean playerHasMoveablePiecesOnBoard(PlayerView player, LudoColor color) {
    return player.piecePositions().stream()
        .anyMatch(pos -> pos > 0 && !isPieceFinished(pos, color));
  }

  private void handleRollOfSix(
      PlayerView currentPlayer, boolean hasHomePieces, boolean hasMoveableBoardPieces) {
    waitingForPieceSelection = true;
    if (hasHomePieces) {
      view.showStatusMessage(
          currentPlayer.playerName() + " rolled a 6! Select a piece to move (can move from home).");
      // Don't auto-select if they can move from home OR board, offer choice.
    } else if (hasMoveableBoardPieces) {
      view.showStatusMessage(currentPlayer.playerName() + " rolled a 6! Select a piece to move.");
      tryAutoSelectOnlyMovablePiece(currentPlayer); // Auto-select if only one piece on board
    } else {
      // All pieces are finished or some other unexpected state
      view.showStatusMessage(
          currentPlayer.playerName() + " rolled a 6 but has no valid moves (all pieces finished?).");
      view.enableRollButton(); // Allow to re-roll or indicate an issue
    }
  }

  private void handleNormalRoll(
      PlayerView currentPlayer, boolean hasMoveableBoardPieces, boolean hasHomePieces) {
    if (hasMoveableBoardPieces) {
      waitingForPieceSelection = true;
      view.showStatusMessage(currentPlayer.playerName() + " - select a piece to move.");
      tryAutoSelectOnlyMovablePiece(currentPlayer); // Auto-select if only one piece on board
    } else {
      // No pieces on board (all at home or finished) and didn't roll a 6
      handleNoValidMoves(currentPlayer, hasHomePieces);
    }
  }

  private void tryAutoSelectOnlyMovablePiece(PlayerView currentPlayer) {
    LudoColor color = LudoColor.valueOf(currentPlayer.playerToken());
    List<Integer> boardPieceIndices = getBoardPieceIndices(currentPlayer, color); // Pass color
    if (boardPieceIndices.size() == 1) {
      selectedPieceIndex = boardPieceIndices.get(0);
      LOG.log(Level.INFO, () -> "Auto-selecting piece: " + selectedPieceIndex +
          " for " + currentPlayer.playerName());
      processSelectedPiece();
    }
  }

  private void handleNoValidMoves(PlayerView currentPlayer, boolean hasHomePieces) {
    LOG.log(Level.INFO, () -> currentPlayer.playerName() + " has no valid moves with roll " + lastRolledValue);
    view.showStatusMessage(currentPlayer.playerName() + " has no valid moves.");

    if (hasHomePieces && gateway instanceof LudoGateway ludoGw) {
      // Select the first home piece for processing (which will skip the turn in
      // gateway)
      for (int i = 0; i < currentPlayer.piecePositions().size(); i++) {
        if (currentPlayer.piecePositions().get(i) <= 0) {
          selectedPieceIndex = i;
          break;
        }
      }
      ludoGw.selectPiece(selectedPieceIndex);
      ludoGw.applyPieceMovement();
      selectedPieceIndex = -1;
      waitingForPieceSelection = false;
      refreshTokens();
    }
    view.enableRollButton();
  }

  // Helper for getBoardPieceIndices to include color for isPieceFinished check
  private List<Integer> getBoardPieceIndices(PlayerView player, LudoColor color) {
    List<Integer> indices = new ArrayList<>();
    for (int i = 0; i < player.piecePositions().size(); i++) {
      int pos = player.piecePositions().get(i);
      if (pos > 0 && !isPieceFinished(pos, color)) {
        indices.add(i);
      }
    }
    return indices;
  }

  private void onPieceSelected(int pieceIndex) {
    if (!waitingForPieceSelection) {
      LOG.fine("Not waiting for piece selection. Ignoring click.");
      return;
    }

    PlayerView currentPlayer = getCurrentPlayer();
    if (currentPlayer == null) {
      LOG.fine("No current player. Ignoring click.");
      return;
    }

    if (!isValidPieceSelection(currentPlayer, pieceIndex, lastRolledValue)) {
      return; // showAlert is called within isValidPieceSelection
    }

    selectedPieceIndex = pieceIndex;
    processSelectedPiece();
  }

  private boolean isValidPieceSelection(PlayerView player, int pieceIndex, int currentRoll) {
    if (pieceIndex >= player.piecePositions().size() || pieceIndex < 0) {
      view.showAlert("Invalid Selection", "Selected piece index is out of bounds.");
      return false;
    }

    int position = player.piecePositions().get(pieceIndex);
    LudoColor color = LudoColor.valueOf(player.playerToken());

    if (position <= 0 && currentRoll != 6) {
      view.showAlert("Invalid Move", "You need to roll a 6 to move a piece from home.");
      return false;
    }
    if (isPieceFinished(position, color)) {
      view.showAlert("Piece Finished", "That piece has already reached the goal.");
      return false;
    }
    return true;
  }

  // Fix for the processSelectedPiece method in LudoBoardController.java
  private void processSelectedPiece() {
    view.disableRollButton(); // Disable while processing and animating
    waitingForPieceSelection = false;

    PlayerView currentPlayer = getCurrentPlayer();
    if (currentPlayer == null || selectedPieceIndex < 0) {
      view.enableRollButton(); // Should not happen
      return;
    }

    String playerColorToken = currentPlayer.playerToken();
    int initialPosition = currentPlayer.piecePositions().get(selectedPieceIndex);

    // 1. Interact with Gateway to apply move
    if (gateway instanceof LudoGateway ludoGw) {
      ludoGw.selectPiece(selectedPieceIndex);
      ludoGw.applyPieceMovement(); // This updates the game state in the model
    }

    // 2. Get updated state for animation and turn management
    PlayerView playerAfterMove =
        gateway.players().stream()
            .filter(p -> p.playerToken().equals(playerColorToken))
            .findFirst()
            .orElse(null); // This player might not be the "current" player anymore if turn passed

    if (playerAfterMove == null) { // Should not happen
      resetAndEnableRoll();
      return;
    }

    int finalPosition = playerAfterMove.piecePositions().get(selectedPieceIndex);
    PlayerView newCurrentPlayer = getCurrentPlayer(); // Who has the turn NOW
    boolean playerKeepsTurn =
        newCurrentPlayer != null && newCurrentPlayer.playerToken().equals(playerColorToken);

    // 3. Animate based on movement
    boolean movedFromHomeToStart = initialPosition <= 0 && finalPosition > 0 && lastRolledValue == 6;

    if (movedFromHomeToStart) {
      animatePieceFromHome(playerColorToken, selectedPieceIndex, finalPosition, playerKeepsTurn);
    } else if (initialPosition != finalPosition) { // Actual movement on board
      animatePieceOnBoard(
          playerColorToken, selectedPieceIndex, initialPosition, finalPosition, playerKeepsTurn);
    } else {
      handlePostMoveUI(playerColorToken, playerKeepsTurn);
    }

    selectedPieceIndex = -1; // Reset for nextTile selection
  }

  private void animatePieceFromHome(
      String playerToken, int pieceIdx, int endPositionOnBoard, boolean playerKeepsTurn) {
    LudoColor color = LudoColor.valueOf(playerToken);
    List<Integer> path = List.of(0, endPositionOnBoard); // 0 represents home visually

    view.animateMoveAlongPath(
        playerToken, pieceIdx, path, () -> handlePostMoveUI(playerToken, playerKeepsTurn));
  }

  private void animatePieceOnBoard(
      String playerToken, int pieceIdx, int startPos, int endPos, boolean playerKeepsTurn) {
    List<Integer> path = buildLudoPathBetween(startPos, endPos, LudoColor.valueOf(playerToken));
    LOG.log(Level.WARNING, () -> "Animating piece " + pieceIdx + " for " + playerToken + " along path: " + path);

    view.animateMoveAlongPath(
        playerToken, pieceIdx, path, () -> handlePostMoveUI(playerToken, playerKeepsTurn));
  }

  private void handlePostMoveUI(String playerMovedToken, boolean playerKeepsTurn) {
    refreshTokens(); // Update all playerToken positions from model

    if (gateway.hasWinner()) {
      // Find the winner's playerName from the potentially updated player list
      gateway.players().stream()
          .filter(
              p -> p.playerToken().equals(playerMovedToken)) // Assuming winner is the one who just moved
          .findFirst()
          .ifPresent(winner -> view.announceWinner(winner.playerName()));
      // Roll button likely stays disabled or game ends.
    } else {
      PlayerView actualCurrentPlayer = getCurrentPlayer(); // Get the player whose turn it is NOW
      if (actualCurrentPlayer == null) { // Should not happen unless game ended abruptly
        view.disableRollButton();
        return;
      }

      if (playerKeepsTurn) { // The same player who moved still has the turn
        view.enableRollButton();
        view.showStatusMessage(actualCurrentPlayer.playerName() + "'s turn continues - roll again!");
      } else { // Turn has passed to someone else
        view.showStatusMessage(actualCurrentPlayer.playerName() + "'s turn - please roll");
        view.enableRollButton(); // Enable for the new current player
      }
    }
  }

  private void resetAndEnableRoll() {
    refreshTokens();
    view.enableRollButton();
    selectedPieceIndex = -1;
    waitingForPieceSelection = false;
  }

  // TODO: Simplify this
  private List<Integer> buildLudoPathBetween(int startId, int endId, LudoColor color) {
    List<Integer> path = new ArrayList<>();
    path.add(startId);

    if (startId == endId) {
      return path;
    }

    int currentSimulatedId = startId;
    int ownerEntryPointId = getEntryPoint(color);
    int ownerPreEntryPointId = (ownerEntryPointId == 1) ? 52 : ownerEntryPointId - 1;
    int goalBaseForColor = getGoalBaseId(color);
    int goalEndForColor = goalBaseForColor + GOAL_LENGTH - 1;
    for (int step = 0; step < 70 && currentSimulatedId != endId; step++) {
      int nextSimulatedId = -1;

      if (currentSimulatedId == 0) {
        nextSimulatedId = ownerEntryPointId;
        if (endId == ownerEntryPointId) {
        } else if (endId > 0 && endId <= 52 && endId != ownerEntryPointId) {
          LOG.log(Level.WARNING, "Pathing from home to a non-entry point: " + endId + ". Animating to entry first.");
        } else if (endId >= goalBaseForColor && endId <= goalEndForColor) {
          LOG.log(Level.WARNING, "Pathing from home directly into goal: " + endId + ". Animating to entry first.");
        }

      } else if (currentSimulatedId >= goalBaseForColor && currentSimulatedId < goalEndForColor) {
        if (currentSimulatedId < endId && endId <= goalEndForColor) {
          nextSimulatedId = currentSimulatedId + 1;
        } else {
          LOG.log(Level.WARNING, "Pathing logic error: In goal " + currentSimulatedId +
              ", but target " + endId + " is unexpected. Color: " + color);
          break;
        }
      } else if (currentSimulatedId > 0 && currentSimulatedId <= 52) {
        if (currentSimulatedId == ownerPreEntryPointId &&
            (endId == goalBaseForColor || (endId > goalBaseForColor && endId <= goalEndForColor)
                || endId == ownerEntryPointId)) {
          nextSimulatedId = goalBaseForColor;
        } else {
          int physicalNextOnRing = (currentSimulatedId % 52) + 1;
          nextSimulatedId = physicalNextOnRing;
        }
      } else if (currentSimulatedId == goalEndForColor) {
        LOG.fine("Piece " + currentSimulatedId + " is already at the end of its goal path for " + color);
        break;
      } else {
        LOG.log(Level.WARNING, "Pathing logic: Unhandled state. currentSimulatedId=" + currentSimulatedId +
            " endId=" + endId + " color=" + color);
        break;
      }

      if (nextSimulatedId != -1) {
        path.add(nextSimulatedId);
        currentSimulatedId = nextSimulatedId;
      } else {
        LOG.log(Level.SEVERE,
            "Pathing logic: nextSimulatedId remained -1. Breaking. current=" + currentSimulatedId + " target=" + endId);
        break;
      }
    }
    if (currentSimulatedId != endId && (path.isEmpty() || path.get(path.size() - 1) != endId)) {
      LOG.log(Level.WARNING, "Path incomplete or diverged. CurrentSim: " + currentSimulatedId + " Target: " + endId
          + ". Forcing target as last step.");
      if (path.isEmpty() || path.get(path.size() - 1) != endId) {
        if (endId != 0) {
          path.add(endId);
        }
      }
    }
    if (path.size() >= 2 && path.get(path.size() - 1).equals(path.get(path.size() - 2))) {
      path.remove(path.size() - 1);
    }

    LOG.log(Level.INFO, "Generated path for " + color + " from " + startId + " to " + endId + ": " + path);
    return path;
  }

  private int getEntryPoint(LudoColor color) {
    return switch (color) {
      case BLUE -> 1;
      case RED -> 14;
      case GREEN -> 27;
      case YELLOW -> 40;
    };
  }

  private int getGoalBaseId(LudoColor color) {
    return switch (color) {
      case BLUE -> 53;
      case RED -> 59;
      case GREEN -> 65;
      case YELLOW -> 71;
    };
  }

  private void refreshTokens() {
    if (gateway == null)
      return;

    // Get updated player list
    List<PlayerView> updatedPlayers = gateway.players();

    // Set updated player data
    view.setPlayers(updatedPlayers);
    view.setOverlays(gateway.boardOverlays());

    // Update status message for current player
    PlayerView currentPlayer = getCurrentPlayer();
    if (currentPlayer != null) {
      view.showStatusMessage(currentPlayer.playerName() + "'s turn");
    }
  }

  // Helper methods

  private boolean isPieceFinished(int pos, LudoColor color) {
    return pos == getGoalBaseId(color) + GOAL_LENGTH;
  }

  private PlayerView getCurrentPlayer() {
    return gateway.players().stream().filter(PlayerView::hasTurn).findFirst().orElse(null);
  }
}
