package edu.ntnu.idatt2003.models;
import java.util.HashSet;
import java.util.Set;

import edu.ntnu.idatt2003.game_logic.TileAction;

public class Tile {

    private final int tileId;
    private Tile nextTile;
    private final Set<Player> playersOnTile;
    private TileAction action;

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

    public TileAction getAction() {
        return action;
    }

    public void setAction(TileAction action) {
        this.action = action;
    }

    public Set<Player> getPlayersOnTile() {
        return new HashSet<>(playersOnTile);
    }

}
