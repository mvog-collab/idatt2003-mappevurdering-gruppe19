package edu.ntnu.idatt2003.game_logic;

import java.util.ArrayList;
import java.util.List;

import edu.ntnu.idatt2003.models.Board;
import edu.ntnu.idatt2003.models.Tile;

public class SnakeAndLadderConfigurator {

    private SnakeAndLadderConfigurator() {}

    public static void generateSnakesAndLadders(Board board) {
            List<TileAction> actions = new ArrayList<>();
            
            actions.add(new Snake(35, 4, board));
            actions.add(new Snake(31, 8, board));
            actions.add(new Snake(83, 67, board));
            actions.add(new Snake(76, 40, board));

            actions.add(new Ladder(28, 46, board));
            actions.add(new Ladder(33, 49, board));
            actions.add(new Ladder(6, 22, board));
            actions.add(new Ladder(38, 74, board));

            for (TileAction action : actions) {
            Tile startTile = board.getTile(action.getActionPosition());
            if (startTile != null) {
                startTile.setAction(action);
            }
        }
    }
}
