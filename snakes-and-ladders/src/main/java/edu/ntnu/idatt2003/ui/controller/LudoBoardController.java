package edu.ntnu.idatt2003.ui.controller;

import java.util.ArrayList;
import java.util.List;

import edu.games.engine.model.LudoColor;
import edu.ntnu.idatt2003.gateway.GameGateway;
import edu.ntnu.idatt2003.gateway.LudoGateway;
import edu.ntnu.idatt2003.gateway.view.PlayerView;
import edu.ntnu.idatt2003.ui.view.LudoBoardView;

public final class LudoBoardController {

    private final LudoBoardView view;
    private final GameGateway gw;
    
    private int lastRolledValue = 0;
    private int selectedPieceIndex = -1;
    private boolean waitingForPieceSelection = false;

    public LudoBoardController(LudoBoardView view, GameGateway gw) {
        this.view = view;
        this.gw = gw;

        view.getRollButton().setOnAction(e -> rollDice());
        view.getAgainButton().setOnAction(e -> {
            gw.resetGame();
            refreshTokens();
        });
        
        // Set up the callback for piece selection
        view.setPieceSelectedCallback(this::onPieceSelected);

        refreshTokens();
    }

    /* -------- gameplay ---------- */
    
    private void rollDice() {
        view.disableRollButton();
        
        PlayerView current = getCurrentPlayer();
        if (current == null) {
            view.enableRollButton();
            return;
        }
        
        // Roll the dice
        lastRolledValue = gw.rollDice();
        view.showDice(lastRolledValue);
        System.out.println("Rolled: " + lastRolledValue);
        
        // Check player pieces
        boolean hasHomePieces = current.piecePositions().stream().anyMatch(pos -> pos <= 0);
        boolean hasBoardPieces = current.piecePositions().stream().anyMatch(pos -> pos > 0);
        
        // Special case handling for rolling a 6
        if (lastRolledValue == 6) {
            waitingForPieceSelection = true;
            
            if (hasHomePieces) {
                // Can move from home or on board
                view.showStatusMessage(current.name() + " rolled a 6! Select a piece to move (can move from home)");
                return;
            } else if (hasBoardPieces) {
                // Normal board piece movement
                view.showStatusMessage(current.name() + " rolled a 6! Select a piece to move");
                
                // If only one piece on board, auto-select it
                List<Integer> boardPieceIndices = getBoardPieceIndices(current);
                if (boardPieceIndices.size() == 1) {
                    selectedPieceIndex = boardPieceIndices.get(0);
                    processSelectedPiece();
                    return;
                }
                return;
            } else {
                // Should never happen, as player always has either home or board pieces
                view.showStatusMessage(current.name() + " has no valid moves");
                view.enableRollButton();
                return;
            }
        }
        
        // Regular turn handling (not rolling a 6)
        if (hasBoardPieces) {
            // Player has pieces on board - must select one
            waitingForPieceSelection = true;
            view.showStatusMessage(current.name() + " - select a piece to move");
            
            // If player has only one piece on board, auto-select it
            List<Integer> boardPieceIndices = getBoardPieceIndices(current);
            if (boardPieceIndices.size() == 1) {
                selectedPieceIndex = boardPieceIndices.get(0);
                processSelectedPiece();
            }
            return;
        } else {
            // Player has no valid moves (only home pieces and didn't roll a 6)
            System.out.println("No valid moves");
            view.showStatusMessage(current.name() + " has no valid moves");
            
            // Auto-advance turn by selecting any piece index and processing it without rolling again
            if (hasHomePieces) {
                // Select the first home piece for processing (which will skip the turn)
                for (int i = 0; i < current.piecePositions().size(); i++) {
                    if (current.piecePositions().get(i) <= 0) {
                        selectedPieceIndex = i;
                        break;
                    }
                }
                
                // Process the selection (will skip turn since it's not a 6)
                if (gw instanceof LudoGateway ludoGw) {
                    ludoGw.selectPiece(selectedPieceIndex);
                    ludoGw.applyPieceMovement(); // Use apply instead of rolling again
                    selectedPieceIndex = -1;
                    waitingForPieceSelection = false;
                    refreshTokens();
                    view.enableRollButton();
                }
            }
            return;
        }
    }
    
    private void onPieceSelected(int pieceIndex) {
        if (!waitingForPieceSelection) return;
        
        PlayerView current = getCurrentPlayer();
        if (current == null) return;
        
        // Validate selection
        if (pieceIndex < current.piecePositions().size()) {
            int position = current.piecePositions().get(pieceIndex);
            if (position <= 0 && lastRolledValue != 6) {
                view.showAlert("Invalid move", "You need to roll a 6 to move a piece from home.");
                return;
            }
        }
        
        selectedPieceIndex = pieceIndex;
        processSelectedPiece();
    }
    
    private void processSelectedPiece() {
        waitingForPieceSelection = false;
        
        PlayerView current = getCurrentPlayer();
        if (current == null || selectedPieceIndex < 0) {
            view.enableRollButton();
            return;
        }
        
        String playerColor = current.token();
        int startPosition = current.piecePositions().get(selectedPieceIndex);
        boolean movingFromHome = startPosition <= 0;
        
        // Tell the gateway which piece to move
        if (gw instanceof LudoGateway ludoGw) {
            ludoGw.selectPiece(selectedPieceIndex);
            ludoGw.applyPieceMovement();
        }
        
        // Get updated player
        PlayerView updatedPlayer = gw.players().stream()
            .filter(p -> p.token().equals(playerColor))
            .findFirst()
            .orElse(null);

        int endPosition = 0;
        if (updatedPlayer != null && selectedPieceIndex < updatedPlayer.piecePositions().size()) {
            endPosition = updatedPlayer.piecePositions().get(selectedPieceIndex);
        }
        
        // Special handling for home piece moving to start with a 6
        if (movingFromHome && lastRolledValue == 6) {
            // Determine the correct start position for this color
            int startTileId = switch (LudoColor.valueOf(playerColor)) {
                case BLUE -> 1;
                case RED -> 14;
                case GREEN -> 27;
                case YELLOW -> 40;
            };
            
            if (endPosition > 0) {
                // Animate from home to the start position
                List<Integer> path = List.of(0, startTileId);
                
                view.animateMoveAlongPath(
                    playerColor,
                    selectedPieceIndex,
                    path,
                    () -> {
                        refreshTokens();
                        view.enableRollButton();
                    }
                );
                
                // Reset selection
                selectedPieceIndex = -1;
                return;
            }
        }
        
        // Check if there was actual movement for board pieces
        if (startPosition != endPosition) {
            // Always build a path and animate
            List<Integer> path = buildLudoPathBetween(
                startPosition, 
                endPosition, 
                LudoColor.valueOf(playerColor)
            );
            
            System.out.println("Animating piece " + selectedPieceIndex + " along path: " + path);
            
            view.animateMoveAlongPath(
                playerColor,
                selectedPieceIndex,
                path,
                () -> {
                    refreshTokens();
                    if (gw.hasWinner()) {
                        view.announceWinner(current.name());
                    } else {
                        view.enableRollButton();
                    }
                }
            );
        } else {
            refreshTokens();
            view.enableRollButton();
        }
        
        // Reset selection
        selectedPieceIndex = -1;
    }

    private List<Integer> buildLudoPathBetween(int startId, int endId, LudoColor color) {
        List<Integer> path = new ArrayList<>();
        
        // Handle home position or same position
        if (startId <= 0 || startId == endId) {
            path.add(startId);
            if (endId > 0 && startId != endId) {
                path.add(endId);
            }
            return path;
        }
        
        // Get color-specific goal path parameters
        int entryPoint = switch (color) {
            case GREEN -> 27;
            case RED -> 14;
            case BLUE -> 1;
            case YELLOW -> 40;
        };
        
        int goalBaseId = switch (color) {
            case BLUE -> 53;
            case RED -> 59;
            case GREEN -> 65;
            case YELLOW -> 71;
        };
        
        // Check if on main ring
        if (startId <= 52 && endId <= 52) {
            // Calculate clockwise and counterclockwise distances
            int clockwiseSteps;
            if (endId >= startId) {
                clockwiseSteps = endId - startId;
            } else {
                clockwiseSteps = (52 - startId) + endId;
            }
            
            int counterClockwiseSteps;
            if (startId >= endId) {
                counterClockwiseSteps = startId - endId;
            } else {
                counterClockwiseSteps = startId + (52 - endId);
            }
            
            // Choose the shorter path
            if (clockwiseSteps <= counterClockwiseSteps) {
                // Go clockwise
                int pos = startId;
                for (int i = 0; i < clockwiseSteps; i++) {
                    pos = pos % 52 + 1;  // Next position, wrapping from 52 to 1
                    path.add(pos);
                }
            } else {
                // Go counterclockwise
                int pos = startId;
                for (int i = 0; i < counterClockwiseSteps; i++) {
                    pos = (pos == 1) ? 52 : pos - 1;  // Previous position, wrapping from 1 to 52
                    path.add(pos);
                }
            }
            
            return path;
        }
        
        // For goal paths and other cases, just use a simple direct path
        if (startId <= 52 && endId > 52) {
            // Simply add the end position
            path.add(endId);
            return path;
        }
        
        // Moving within goal path
        if (startId > 52 && endId > 52) {
            for (int i = startId + 1; i <= endId; i++) {
                path.add(i);
            }
            return path;
        }
        
        // Fallback - just add end position
        if (endId != startId) {
            path.add(endId);
        }
        
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
        view.setPlayers(gw.players());
        view.setOverlays(gw.boardOverlays());
    }
    
    // Helper methods
    
    private PlayerView getCurrentPlayer() {
        return gw.players().stream()
            .filter(PlayerView::hasTurn)
            .findFirst()
            .orElse(null);
    }
    
    private List<Integer> getBoardPieceIndices(PlayerView player) {
        List<Integer> indices = new ArrayList<>();
        
        for (int i = 0; i < player.piecePositions().size(); i++) {
            if (player.piecePositions().get(i) > 0) {
                indices.add(i);
            }
        }
        
        return indices;
    }
}