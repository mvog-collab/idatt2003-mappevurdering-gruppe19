package edu.ntnu.idatt2003.ui.controller;

import java.util.ArrayList;
import java.util.List;

import edu.games.engine.model.LudoColor;
import edu.ntnu.idatt2003.gateway.GameGateway;
import edu.ntnu.idatt2003.gateway.view.PlayerView;
import edu.ntnu.idatt2003.ui.view.LudoBoardView;

public final class LudoBoardController {

    private final LudoBoardView view;
    private final GameGateway   gw;

    public LudoBoardController(LudoBoardView view, GameGateway gw) {
        this.view = view;
        this.gw   = gw;

        view.getRollButton() .setOnAction(e -> playTurn());
        view.getAgainButton().setOnAction(e -> {
            gw.resetGame();
            refreshTokens();
        });

        refreshTokens();
    }

    /* -------- gameplay ---------- */

    private void playTurn() {
        view.disableRollButton();
    
        PlayerView current = gw.players().stream()
                              .filter(PlayerView::hasTurn)
                              .findFirst()
                              .orElseThrow();
    
        // Get the player's current position
        Integer startId = current.tileId();
        String playerColor = current.token();
        System.out.println("Player " + current.name() + " (" + playerColor + ") starting turn at tile " + startId);
        
        // Roll the dice
        int rolled = gw.rollDice();
        
        // Show the die
        view.showDice(rolled);
        System.out.println("Rolled: " + rolled);
        
        // Get the updated player position after game logic
        PlayerView updatedPlayer = gw.players().stream()
                                   .filter(p -> p.token().equals(current.token()))
                                   .findFirst()
                                   .orElseThrow();
        
        Integer endId = updatedPlayer.tileId();
        System.out.println("After game logic, player " + current.name() + " now at tile " + endId);
    
        // Handle special cases
        if ((startId == null || startId <= 0) && rolled == 6 && endId != null && endId > 0) {
            // Player left home with a 6
            System.out.println("Player left home with a 6");
            refreshTokens();
            view.enableRollButton();
            return;
        }
        
        if (startId <= 0 || endId <= 0 || (startId != null && endId != null && startId.equals(endId))) {
            // No movement occurred
            System.out.println("No valid movement occurred");
            refreshTokens();
            view.enableRollButton();
            return;
        }
        
        // Only animate if there was actual movement
        if (startId != null && endId != null && !startId.equals(endId)) {
            // Build the path for animation
            List<Integer> path = buildLudoPathBetween(startId, endId, LudoColor.valueOf(current.token()));
            
            System.out.println("Animating path: " + path);
            
            view.animateMoveAlongPath(
                current.token(),
                path,
                () -> {
                    refreshTokens();
                    if (gw.hasWinner()) view.announceWinner(current.name());
                    else view.enableRollButton();
                }
            );
        } else {
            refreshTokens();
            view.enableRollButton();
        }
    }

    private List<Integer> buildLudoPathBetween(int startId, int endId, LudoColor color) {
        List<Integer> path = new ArrayList<>();
        path.add(startId);
        
        if (startId == endId) {
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
        
        // Case 1: Both start and end are on the main ring
        if (startId <= 52 && endId <= 52) {
            int current = startId;
            while (current != endId) {
                current = current % 52 + 1;
                path.add(current);
                if (path.size() > 52) break;
            }
            return path;
        }
        
        // Case 2: Start on ring, end in goal path
        if (startId <= 52 && endId > 52) {
            // Figure out which goal path we're entering
            // Make sure it's the correct goal path for this color
            if (endId >= goalBaseId && endId < goalBaseId + 6) {
                // First check if we're at the entry point and need to go directly to the goal path
                if (startId == entryPoint) {
                    for (int i = 0; i < endId - goalBaseId + 1; i++) {
                        path.add(goalBaseId + i);
                    }
                    return path;
                }
                
                int current = startId;
                while (current != entryPoint) {
                    current = current % 52 + 1;
                    path.add(current);
                    if (path.size() > 52) break;
                }
                
                for (int i = 0; i < endId - goalBaseId + 1; i++) {
                    path.add(goalBaseId + i);
                }
                return path;
            } else {
                path.add(endId);
                return path;
            }
        }
        
        if (startId > 52 && endId > 52) {
            if (startId >= goalBaseId && startId < goalBaseId + 6 && 
                endId >= goalBaseId && endId < goalBaseId + 6) {
                for (int i = startId + 1; i <= endId; i++) {
                    path.add(i);
                }
                return path;
            } else {
                path.add(endId);
                return path;
            }
        }
        
        if (path.size() == 1 && startId != endId) {
            path.add(endId);
        }
        
        return path;
    }

    private void refreshTokens() {
        view.setPlayers(gw.players());
        view.enableRollButton();
        view.setOverlays(gw.boardOverlays());
    }
}