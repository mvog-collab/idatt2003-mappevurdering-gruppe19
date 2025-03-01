import java.util.ArrayList;
import java.util.List;

public class SnakeAndLadderConfigurator {

    private SnakeAndLadderConfigurator() {}

    public static void generateSnakesAndLadders(Board board) {
            List<TileAction> actions = new ArrayList<>();

            if (board.getSize() > 100) {
            }
            actions.add(new Snake(14, 7, board));
            actions.add(new Snake(48, 26, board));
            actions.add(new Snake(84, 58, board));
            actions.add(new Snake(24, 3, board));
            actions.add(new Snake(45, 15, board));

            actions.add(new Ladder(4, 14, board));
            actions.add(new Ladder(9, 31, board));
            actions.add(new Ladder(20, 38, board));
            actions.add(new Ladder(28, 84, board));
            actions.add(new Ladder(40, 59, board));

            actions.add(new Snake(15, 7, board));
            actions.add(new Snake(45, 26, board));
            actions.add(new Snake(85, 58, board));
            actions.add(new Snake(25, 3, board));
            actions.add(new Snake(46, 15, board));

            actions.add(new Ladder(13, 14, board));
            actions.add(new Ladder(47, 31, board));
            actions.add(new Ladder(10, 38, board));
            actions.add(new Ladder(21, 84, board));
            actions.add(new Ladder(18, 59, board));

            for (TileAction action : actions) {
            Tile startTile = board.getTile(action.getActionPosition());
            if (startTile != null) {
                startTile.setAction(action);
            }
        }
    }
}
