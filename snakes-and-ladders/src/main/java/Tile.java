import java.util.HashSet;
import java.util.Set;

public class Tile {

    private final int tileId;
    private Tile nextTile;
    private final Set<Player> playersOnTile;

    public Tile(int tileId) {
        this.tileId = tileId;
        this.playersOnTile = new HashSet<>();
    }

    public void landPlayer(Player player) {
        playersOnTile.add(player);
        System.out.println(player.getName() + " landed on tile " + tileId);
    }

    public void removePlayerFromTile(Player player) {
        playersOnTile.remove(player);
        System.out.println(player.getName() + " left tile " + tileId);
    }

    public void setNextTile(Tile nextTile) {
        this.nextTile = nextTile;
    }

    public Tile getNextTile() {
        return nextTile;
    }

    public int getTileId() {
        return tileId;
    }
}
