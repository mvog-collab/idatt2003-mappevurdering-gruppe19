package edu.games.engine;

import java.time.LocalDate;
import java.util.Objects;

public final class Player {
    private final String name;
    private final Token token;
    private final LocalDate birthday;
    private Tile currentTile;

    public Player(String name, Token token, LocalDate birthday) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Invalid name: cannot be empty");
        }
        this.name = name;
        this.token = Objects.requireNonNull(token);
        this.birthday = Objects.requireNonNull(birthday);
    }

    public String getName() {
        return name;
    }

    public Token getToken() {
        return token;
    }

    public LocalDate getBirtday() {
        return birthday;
    }

    public Tile getCurrentTile() {
        return currentTile;
    }

    void moveTo(Tile tile) {
        this.currentTile = Objects.requireNonNull(tile);
    }

    @Override 
    public boolean equals(Object object) {
        return (object instanceof Player player) && player.name.equals(name) && player.birthday.equals(birthday);
    }

    @Override 
    public int hashCode() { 
        return Objects.hash(name, birthday); 
    }

    @Override 
    public String toString() { 
        return "%s (%s)".formatted(name, token); 
    }
}
