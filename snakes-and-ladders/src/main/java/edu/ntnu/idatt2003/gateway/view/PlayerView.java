package edu.ntnu.idatt2003.gateway.view;

import java.time.LocalDate;
import java.util.List;

public record PlayerView(
        String name,
        String token,
        List<Integer> piecePositions, // List of tile IDs for each piece (null for SNL)
        LocalDate birthday,
        boolean hasTurn,
        int activePieceIndex, // -1 if no piece is selected or for SNL
        
        // Single position field for backwards compatibility with SNL
        Integer tileId
) {
    // Constructor for Ludo
    public PlayerView(String name, String token, List<Integer> piecePositions, 
                     LocalDate birthday, boolean hasTurn, int activePieceIndex) {
        this(name, token, piecePositions, birthday, hasTurn, activePieceIndex, 
             // Get first non-zero position for compatibility
             piecePositions != null && !piecePositions.isEmpty() ? 
                 piecePositions.stream().filter(pos -> pos > 0).findFirst().orElse(0) : 0);
    }
    
    // Constructor for Snakes and Ladders
    public PlayerView(String name, String token, int tileId,
                     LocalDate birthday, boolean hasTurn) {
        this(name, token, List.of(tileId), birthday, hasTurn, -1, tileId);
    }
}