package edu.games.engine.strategy;

import edu.games.engine.board.LudoBoard;
import edu.games.engine.board.Tile;
import edu.games.engine.impl.DefaultGame;
import edu.games.engine.model.LudoColor;
import edu.games.engine.model.Player;
import edu.games.engine.model.PlayerPiece;

public class LudoGameStrategy implements GameStrategy {
    
    @Override
    public void initializeGame(DefaultGame game) {
        // Nothing special needed for Ludo initialization
    }
    
    @Override
    public boolean processDiceRoll(Player player, int diceValue, DefaultGame game) {
        // Player gets an extra turn if they roll a 6
        return diceValue == 6;
    }
    
    @Override
    public Tile movePiece(Player player, int pieceIndex, int diceValue, DefaultGame game) {
        if (player == null || game == null || !(game.board() instanceof LudoBoard)) {
            return null;
        }
        
        if (pieceIndex < 0 || pieceIndex >= player.getPieces().size()) {
            return null;
        }
        
        PlayerPiece piece = player.getPiece(pieceIndex);
        if (piece == null) return null;
        
        LudoColor color = LudoColor.valueOf(player.getToken().name());
        LudoBoard board = (LudoBoard) game.board();
        
        if (piece.isAtHome()) {
            // Can only leave home with a 6
            if (diceValue != 6) {
                return null;
            }
            
            // Get the starting tile for this color
            return board.getStartTile(color);
        } else {
            // Already on board - move normally
            Tile currentTile = piece.getCurrentTile();
            if (currentTile == null) return null;
            
            return board.move(currentTile, diceValue, color);
        }
    }
    
    @Override
    public boolean checkWinCondition(Player player, DefaultGame game) {
        if (player == null) return false;
        
        return player.getPieces().stream()
            .filter(PlayerPiece::isOnBoard)
            .allMatch(piece -> {
                Tile tile = piece.getCurrentTile();
                return tile != null && tile.id() > 52 && tile.id() < 77;
            }) && 
            player.getPieces().stream().allMatch(PlayerPiece::isOnBoard);
    }
    
    @Override
    public void applySpecialRules(Player player, PlayerPiece piece, Tile destinationTile, DefaultGame game) {
        if (player == null || destinationTile == null || game == null || piece == null) {
            return;
        }
        
        // Skip bumping in goal tiles (ID > 52)
        if (destinationTile.id() > 52) return;
        
        // Bump other players' pieces
        game.players().stream()
            .filter(p -> p != player)
            .forEach(otherPlayer -> {
                otherPlayer.getPieces().stream()
                    .filter(otherPiece -> otherPiece.isOnBoard() && 
                           otherPiece.getCurrentTile().id() == destinationTile.id())
                    .forEach(otherPiece -> {
                        otherPiece.moveTo(null);
                    });
            });
    }
}