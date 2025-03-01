import java.time.LocalDate;

public class Player {

    private final String name;
    private Tile currentTile;
    private final LocalDate birthday;

    public Player(String name, LocalDate birthday) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Invalid name input. Name cannot be null or empty.");
        }
        if (birthday.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Invalid birthday input. Birthday cannot be in the future.");
        }
        this.birthday = birthday;
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
            System.out.println(targetTile.getTileId() + " is the tile id");
            targetTile = targetTile.getNextTile();
        }
        placeOnTile(targetTile);

        if (hasTileAction(targetTile)) {
            targetTile.getAction().applyAction(this);
        }
    }

    private boolean hasTileAction(Tile tile) {
        return tile.getAction() != null;
    }

    public String getName() {
        return name;
    }

    public LocalDate getBirthday() {
        return birthday;
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
