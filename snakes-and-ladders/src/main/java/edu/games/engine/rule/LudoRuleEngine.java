package edu.games.engine.rule;

import java.util.List;

import edu.games.engine.board.Board;
import edu.games.engine.board.LudoPath;
import edu.games.engine.board.Tile;
import edu.games.engine.model.LudoColor;
import edu.games.engine.model.Player;

// ----------------------------------------------------------------------------
// LudoRuleEngine – minimal SRP implementation
// ----------------------------------------------------------------------------
public final class LudoRuleEngine implements RuleEngine {

    private final LudoPath path;

    public LudoRuleEngine(LudoPath path) { this.path = path; }

    @Override
    public boolean apply(Board board, Player player, List<Integer> dice) {

        int roll   = dice.getFirst();      // single die
        boolean again = roll == 6;

        /* 1) yard rule -------------------------------------------------- */
        if (player.getCurrentTile() == null && roll != 6) return false;

        /* 2) move ------------------------------------------------------- */
        LudoColor col = LudoColor.valueOf(player.getToken().name());
        Tile dest     = path.next(player.getCurrentTile(), roll, col);
        player.moveTo(dest);

        /* 3) capture is handled by DefaultGame.bumpIfOccupied() --------- */
        return again;
    }
}