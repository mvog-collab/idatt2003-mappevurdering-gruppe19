import java.util.HashSet;
import java.util.Set;

public class Tile {

    private final int tileId;
    private Tile nextTile;
    private final Set<Player> playersOnTile;

    public Tile(int tileId) {
        if (tileId < 0) {
            throw new IllegalArgumentException("Tile cannot be null");
        }
        this.tileId = tileId;
        this.playersOnTile = new HashSet<>();
    }
    
    public void landPlayer(Player player) {
        if (player == null) {
            throw new IllegalArgumentException("Player cannot be null");
        }
        playersOnTile.add(player);
        System.out.println(player.getName() + " landed on tile " + tileId);
    }

    public void removePlayerFromTile(Player player) {
        if (player == null) {
            throw new IllegalArgumentException("Player cannot be null.");
        }
        playersOnTile.remove(player);
        System.out.println(player.getName() + " left tile " + tileId);
    }

    public void setNextTile(Tile nextTile) {
        if (nextTile == null) {
            throw new IllegalArgumentException("Next tile cannot be null.");
        }
        this.nextTile = nextTile;
    }

    public Tile getNextTile() {
        return nextTile;
    }

    public int getTileId() {
        return tileId;
    }
}
