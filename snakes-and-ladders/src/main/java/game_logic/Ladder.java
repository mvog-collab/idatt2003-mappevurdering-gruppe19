package game_logic;

import java.awt.desktop.AboutEvent;

import game_logic.TileAction;
import models.Board;
import models.Player;

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
      System.out.println(player.getName() + " moved from " + startTileId + " to " + endTileId);
    }
  }

  @Override
  public int getActionPosition() {
    return startTileId;
  }
}
