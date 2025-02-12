public class Player {

    private final String name;
    private Tile currentTile;

    public Player(String name) {
        this.name = name;
    }

    public void placeOnTile(Tile tile) {
        if (currentTile != null) {
            currentTile.removePlayerFromTile(this);
        }
        currentTile = tile;
        currentTile.landPlayer(this);
    }

    public void move(int steps) {
        Tile targetTile = currentTile;
        for (int i = 0; i < steps && targetTile.getNextTile() != null; i++) {
            targetTile = targetTile.getNextTile();
        }
        placeOnTile(targetTile);
    }

    public String getName() {
        return name;
    }

    public Tile getCurrentTile() {
        return currentTile;
    }
}
