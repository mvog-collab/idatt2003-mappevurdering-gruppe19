public class Player {

    private final String name;
    private Tile currentTile;

    public Player(String name) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Invalid name input. Name cannot be null or empty.");
        }
        this.name = name;
    }

    public void placeOnTile(Tile tile) {
        if (currentTile != null) {
            currentTile.removePlayerFromTile(this);
        }
        setCurrentTile(tile);
        currentTile.landPlayer(this);
    }

    public void move(int steps) {
        if (steps < 2) {
            throw new IllegalArgumentException("Steps cannot be less than 2.");
        }
        Tile targetTile = currentTile;
        for (int i = 0; i < steps; i++) {
            if (targetTile.getNextTile() == null) {
                placeOnTile(targetTile);
                return;
            }
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

    public void setCurrentTile(Tile tile) {
        if (tile == null) {
            throw new IllegalArgumentException("Tile cannot be null.");
        }
        this.currentTile = tile;
    }
}
