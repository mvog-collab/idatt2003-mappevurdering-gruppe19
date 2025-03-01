import java.awt.desktop.AboutEvent;

public class Ladder implements TileAction {
  int startTileId;
  int endTileId;
  Board board;

  public Ladder(int startTileId, int endTileId, Board board) {
    this.startTileId = startTileId;
    this.endTileId = endTileId;
    this.board = board;
  }

  @Override
  public void applyAction(Player player) {
    if (startTileId == player.getCurrentTile().getTileId()) {
      player.setCurrentTile(board.getTile(endTileId));
    }
  }

  @Override
  public int getTargetPosition() {
    return endTileId;
  }
}
