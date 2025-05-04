package edu.games.engine.rule;

import java.util.List;

import edu.games.engine.board.Board;
import edu.games.engine.board.LudoPath;
import edu.games.engine.board.LudoTile;
import edu.games.engine.board.Tile;
import edu.games.engine.model.LudoColor;
import edu.games.engine.model.Player;

public final class LudoRuleEngine implements RuleEngine {

    private final LudoPath path;

    public LudoRuleEngine(LudoPath path) { 
        this.path = path; 
    }

    @Override
    public boolean apply(Board board, Player player, List<Integer> dice) {
        int roll = dice.get(0);
        LudoColor playerColor = LudoColor.valueOf(player.getToken().name());
        
        System.out.println("LudoRuleEngine: " + player.getName() + " (" + playerColor + ") rolled " + roll);
        
        // Case 1: Player is at home (null tile)
        if (player.getCurrentTile() == null) {
            if (roll != 6) {
                System.out.println("Player stays at home (needs 6 to start)");
                return false;
            }
            
            // Player can leave home with a 6
            LudoTile entryTile = ((LudoPath)path).getStartTile(playerColor);
            player.moveTo(entryTile);
            System.out.println("Player moves from home to starting tile " + entryTile.id());
            return true; // Extra turn after rolling a 6
        }
        
        // Case 2: Player is already on the board
        int currentId = player.getCurrentTile().id();
        System.out.println("Current position: tile " + currentId);
        
        // Use the path's next method to calculate the destination tile
        Tile nextTile = path.next(player.getCurrentTile(), roll, playerColor);
        
        // Update player position
        if (nextTile != player.getCurrentTile()) {
            player.moveTo(nextTile);
            System.out.println("Player moved to tile " + nextTile.id());
        } else {
            System.out.println("Player didn't move (already at end or invalid move)");
        }
        
        return roll == 6; // Extra turn if rolled a 6
    }
}