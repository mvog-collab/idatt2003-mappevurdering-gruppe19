import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Board {

  private final Map<Integer, Tile> tiles;
  private final int size;

  public Board(int size) {
    this.size = size;
    this.tiles = new HashMap<>();
    generateSnakesAndLadders();
  }

  public void addTile(Tile tile) {
    if (tile == null) {
      throw new IllegalArgumentException("Tile cannot be null.");
    }
    tiles.put(tile.getTileId(), tile);

    if (tile.getTileId() > 0) {
      Tile previousTile = tiles.get(tile.getTileId() - 1);
      if (previousTile != null) {
        previousTile.setNextTile(tile);
      }
    }
  }

  private List<TileAction> generateSnakesAndLadders() {
    List<TileAction> actions = new ArrayList<>();

    if (size > 100) {
    }
    actions.add(new Snake(14, 7, this));
    actions.add(new Snake(48, 26, this));
    actions.add(new Snake(84, 58, this));
    actions.add(new Snake(24, 3, this));
    actions.add(new Snake(45, 15, this));

    actions.add(new Ladder(4, 14, this));
    actions.add(new Ladder(9, 31, this));
    actions.add(new Ladder(20, 38, this));
    actions.add(new Ladder(28, 84, this));
    actions.add(new Ladder(40, 59, this));

    for (TileAction action : actions) {
      Tile startTile = getTile(action.getActionPosition());
      if (startTile != null) {
          startTile.setAction(action);
      }
  }
  return actions;
  }

  public Tile getTile(int tileId) {
    return tiles.get(tileId);
  }
}
