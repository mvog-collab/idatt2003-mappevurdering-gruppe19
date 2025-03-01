public class Snake implements TileAction {

    private final int startTileId;
    private final int endTileId;
    private final Board board;

    public Snake(int startTileId, int endTileId, Board board) {
        this.startTileId = startTileId;
        this.endTileId = endTileId;
        this.board = board;
    }

    @Override
    public void applyAction(Player player) {
        if (startTileId == player.getCurrentTile().getTileId()) {
            Tile tileToLand = board.getTile(endTileId);
            System.out.println(player.getName() + " moved from " + startTileId + " to " + endTileId);
            player.placeOnTile(tileToLand);
        }
    }

    @Override
    public int getActionPosition() {
        return startTileId;
    }
}
