package edu.ntnu.idatt2003.ui.ludo.controller;

import edu.games.engine.model.LudoColor;
import edu.ntnu.idatt2003.gateway.CompleteBoardGame;
import edu.ntnu.idatt2003.gateway.LudoGateway;
import edu.ntnu.idatt2003.gateway.view.PlayerView;
import edu.ntnu.idatt2003.ui.common.controller.AbstractGameController;
import edu.ntnu.idatt2003.ui.ludo.view.LudoBoardView;
import edu.ntnu.idatt2003.utils.Errors;

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
    LOG.info("LudoBoardController initialized.");
    try {
      view.setPieceSelectedCallback(this::onPieceSelected);
      refreshTokens();
    } catch (Exception e) {
      LOG.log(Level.SEVERE, "Error during LudoBoardController initialization", e);
      Errors.handle("Failed to initialize Ludo board controller.", e);
    }
  }

  /* -------- gameplay ---------- */

  @Override
  protected void onRollDice() {
    LOG.info("Roll dice action initiated.");
    view.disableRollButton();

    PlayerView currentPlayer = getCurrentPlayer();
    if (currentPlayer == null) {
      LOG.warning("onRollDice called but no current player found. Enabling roll button.");
      view.enableRollButton();
      return;
    }
    LOG.info("Current player for dice roll: " + currentPlayer.playerName());

    try {
      lastRolledValue = gateway.rollDice();
      view.showDice(lastRolledValue);
      LOG.log(Level.INFO, () -> currentPlayer.playerName() + " rolled: " + lastRolledValue);

      LudoColor color = LudoColor.valueOf(currentPlayer.playerToken());
      boolean hasHomePieces = playerHasPiecesAtHome(currentPlayer);
      boolean hasMoveableBoardPieces = playerHasMoveablePiecesOnBoard(currentPlayer, color);

      if (lastRolledValue == 6) {
        handleRollOfSix(currentPlayer, hasHomePieces, hasMoveableBoardPieces);
      } else {
        handleNormalRoll(currentPlayer, hasMoveableBoardPieces, hasHomePieces);
      }
    } catch (IllegalArgumentException e) {
      LOG.log(Level.WARNING, "Invalid LudoColor token for player: " + currentPlayer.playerToken(), e);
      view.showAlert("Game Error", "Invalid player color detected. Cannot proceed.");
      view.enableRollButton();
    } catch (Exception e) {
      LOG.log(Level.SEVERE, "Error during dice roll processing for player " + currentPlayer.playerName(), e);
      Errors.handle("An error occurred while processing the dice roll.", e);
      view.enableRollButton(); // Try to allow user to recover or try again
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
    LOG.info(
        currentPlayer.playerName() + " rolled a 6. Has home pieces: " + hasHomePieces + ", Has moveable board pieces: "
            + hasMoveableBoardPieces);
    waitingForPieceSelection = true;
    if (hasHomePieces) {
      view.showStatusMessage(
          currentPlayer.playerName() + " rolled a 6! Select a piece to move (can move from home).");
    } else if (hasMoveableBoardPieces) {
      view.showStatusMessage(currentPlayer.playerName() + " rolled a 6! Select a piece to move.");
      tryAutoSelectOnlyMovablePiece(currentPlayer);
    } else {
      LOG.warning(currentPlayer.playerName()
          + " rolled a 6 but has no valid moves. All pieces might be finished or in an unexpected state.");
      view.showStatusMessage(
          currentPlayer.playerName() + " rolled a 6 but has no valid moves (all pieces finished?).");
      view.enableRollButton();
    }
  }

  private void handleNormalRoll(
      PlayerView currentPlayer, boolean hasMoveableBoardPieces, boolean hasHomePieces) {
    LOG.info(
        currentPlayer.playerName() + " rolled " + lastRolledValue + ". Has moveable board pieces: "
            + hasMoveableBoardPieces);
    if (hasMoveableBoardPieces) {
      waitingForPieceSelection = true;
      view.showStatusMessage(currentPlayer.playerName() + " - select a piece to move.");
      tryAutoSelectOnlyMovablePiece(currentPlayer);
    } else {
      handleNoValidMoves(currentPlayer, hasHomePieces);
    }
  }

  private void tryAutoSelectOnlyMovablePiece(PlayerView currentPlayer) {
    try {
      LudoColor color = LudoColor.valueOf(currentPlayer.playerToken());
      List<Integer> boardPieceIndices = getBoardPieceIndices(currentPlayer, color);
      if (boardPieceIndices.size() == 1) {
        selectedPieceIndex = boardPieceIndices.get(0);
        LOG.log(Level.INFO, () -> "Auto-selecting piece: " + selectedPieceIndex +
            " for " + currentPlayer.playerName());
        processSelectedPiece();
      }
    } catch (IllegalArgumentException e) {
      LOG.log(Level.WARNING, "Invalid LudoColor token for auto-selection: " + currentPlayer.playerToken(), e);
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
    LOG.fine("Piece selected by user: index " + pieceIndex);
    if (!waitingForPieceSelection) {
      LOG.fine("Not waiting for piece selection. Ignoring click on piece " + pieceIndex);
      return;
    }

    PlayerView currentPlayer = getCurrentPlayer();
    if (currentPlayer == null) {
      LOG.warning("Piece selected but no current player. Ignoring click.");
      return;
    }
    LOG.info("Piece " + pieceIndex + " selected by player " + currentPlayer.playerName());

    try {
      if (!isValidPieceSelection(currentPlayer, pieceIndex, lastRolledValue)) {
        LOG.warning("Invalid piece selection: piece " + pieceIndex + " by " + currentPlayer.playerName() + " with roll "
            + lastRolledValue);
        return;
      }
      selectedPieceIndex = pieceIndex;
      processSelectedPiece();
    } catch (Exception e) {
      LOG.log(Level.SEVERE,
          "Error processing piece selection for player " + currentPlayer.playerName() + ", piece " + pieceIndex, e);
      Errors.handle("An error occurred while selecting the piece.", e);
      // Reset state to be safe
      waitingForPieceSelection = false;
      view.enableRollButton();
    }
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

  private void processSelectedPiece() {
    LOG.info("Processing selected piece: index " + selectedPieceIndex);
    view.disableRollButton();
    waitingForPieceSelection = false;

    PlayerView currentPlayer = getCurrentPlayer();
    if (currentPlayer == null || selectedPieceIndex < 0) {
      LOG.warning("processSelectedPiece called with no current player or invalid selectedPieceIndex. Current player: "
          + (currentPlayer != null ? currentPlayer.playerName() : "null") + ", selectedPieceIndex: "
          + selectedPieceIndex);
      view.enableRollButton();
      return;
    }

    String playerColorToken = currentPlayer.playerToken();
    int initialPosition = currentPlayer.piecePositions().get(selectedPieceIndex);
    LOG.info("Player " + currentPlayer.playerName() + " moving piece " + selectedPieceIndex + " from " + initialPosition
        + " with roll " + lastRolledValue);

    try {
      if (gateway instanceof LudoGateway ludoGw) {
        ludoGw.selectPiece(selectedPieceIndex);
        ludoGw.applyPieceMovement();
      } else {
        LOG.severe("Gateway is not an instance of LudoGateway. Cannot apply Ludo-specific piece movement.");
        view.showAlert("Critical Error", "Game logic error. Cannot move piece.");
        resetAndEnableRoll();
        return;
      }

      PlayerView playerAfterMove = gateway.players().stream()
          .filter(p -> p.playerToken().equals(playerColorToken))
          .findFirst()
          .orElse(null);

      if (playerAfterMove == null) {
        LOG.severe("Player " + playerColorToken + " not found after move. This should not happen.");
        resetAndEnableRoll();
        return;
      }

      int finalPosition = playerAfterMove.piecePositions().get(selectedPieceIndex);
      LOG.info("Piece " + selectedPieceIndex + " for " + playerColorToken + " moved to " + finalPosition);
      PlayerView newCurrentPlayer = getCurrentPlayer();
      boolean playerKeepsTurn = newCurrentPlayer != null && newCurrentPlayer.playerToken().equals(playerColorToken);

      boolean movedFromHomeToStart = initialPosition <= 0 && finalPosition > 0 && lastRolledValue == 6;

      if (movedFromHomeToStart) {
        animatePieceFromHome(playerColorToken, selectedPieceIndex, finalPosition, playerKeepsTurn);
      } else if (initialPosition != finalPosition) {
        animatePieceOnBoard(
            playerColorToken, selectedPieceIndex, initialPosition, finalPosition, playerKeepsTurn);
      } else {
        LOG.info("Piece " + selectedPieceIndex + " for " + playerColorToken
            + " did not change position (e.g., blocked or no valid move from gateway).");
        handlePostMoveUI(playerColorToken, playerKeepsTurn);
      }

    } catch (Exception e) {
      LOG.log(Level.SEVERE, "Error during piece movement processing for player " + currentPlayer.playerName(), e);
      Errors.handle("An error occurred while moving the piece.", e);
      // Attempt to recover UI state
      handlePostMoveUI(playerColorToken,
          getCurrentPlayer() != null && getCurrentPlayer().playerToken().equals(playerColorToken)); // Use current state
                                                                                                    // if
      // possible
    } finally {
      selectedPieceIndex = -1;
    }
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
    LOG.fine("Refreshing tokens on the board.");
    if (gateway == null) {
      LOG.warning("Gateway is null, cannot refresh tokens.");
      return;
    }
    try {
      List<PlayerView> updatedPlayers = gateway.players();
      view.setPlayers(updatedPlayers);
      view.setOverlays(gateway.boardOverlays());

      PlayerView currentPlayer = getCurrentPlayer();
      if (currentPlayer != null) {
        view.showStatusMessage(currentPlayer.playerName() + "'s turn");
      } else {
        LOG.info("No current player after token refresh, game might have ended or is in an intermediate state.");
      }
    } catch (Exception e) {
      LOG.log(Level.SEVERE, "Error refreshing tokens", e);
      Errors.handle("An error occurred while updating the game board.", e);
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
