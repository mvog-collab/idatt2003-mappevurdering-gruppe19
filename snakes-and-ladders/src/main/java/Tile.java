public class Tile {

    private final int tileId;
    private final Tile nextTile;

    public Tile(int tileId) {
        this.tileId = tileId;
        this.nextTile = new Tile(tileId + 1);
    }

    public void landPlayer(Player player) {

    }

    public void leavePlayer(Player player) {

    }

    public void setNextTile(Tile nextTile) {

    }

    public int getTileId() {
        return tileId;
    }


}
