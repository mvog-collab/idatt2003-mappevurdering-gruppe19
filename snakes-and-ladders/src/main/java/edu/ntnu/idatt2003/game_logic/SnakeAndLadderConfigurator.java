package edu.ntnu.idatt2003.game_logic;

import java.util.ArrayList;
import java.util.List;

import edu.ntnu.idatt2003.models.Board;
import edu.ntnu.idatt2003.models.Tile;

public class SnakeAndLadderConfigurator {

    private SnakeAndLadderConfigurator() {}

    public static void generateSnakesAndLadders(Board board) {
        if (board.getSize() == 64) {
            generateSnakesAndLadders64Tiles(board);
        } else if (board.getSize() == 90) {
            generateSnakesAndLadders90Tiles(board);
        } else if (board.getSize() == 120) {
            generateSnakesAndLadders120Tiles(board);
        }
    }

    private static void generateSnakesAndLadders90Tiles(Board board) {
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

    private static void generateSnakesAndLadders64Tiles(Board board) {
        List<TileAction> actions = new ArrayList<>();
            
            actions.add(new Snake(48, 16, board));
            actions.add(new Snake(59, 45, board));
            actions.add(new Snake(29, 6, board));

            actions.add(new Ladder(36, 50, board));
            actions.add(new Ladder(22, 40, board));
            actions.add(new Ladder(4, 20, board));

            for (TileAction action : actions) {
            Tile startTile = board.getTile(action.getActionPosition());
            if (startTile != null) {
                startTile.setAction(action);
            }
        }
    }

    private static void generateSnakesAndLadders120Tiles(Board board) {
        List<TileAction> actions = new ArrayList<>();
            
            actions.add(new Snake(118, 84, board));
            actions.add(new Snake(92, 69, board));
            actions.add(new Snake(83, 61, board));
            actions.add(new Snake(77, 56, board));
            actions.add(new Snake(42, 17, board));
            actions.add(new Snake(27, 7, board));

            actions.add(new Ladder(93, 115, board));
            actions.add(new Ladder(66, 86, board));
            actions.add(new Ladder(34, 52, board));
            actions.add(new Ladder(26, 44, board));
            actions.add(new Ladder(20, 60, board));
            actions.add(new Ladder(10, 28, board));


            for (TileAction action : actions) {
            Tile startTile = board.getTile(action.getActionPosition());
            if (startTile != null) {
                startTile.setAction(action);
            }
        }
    }
}
