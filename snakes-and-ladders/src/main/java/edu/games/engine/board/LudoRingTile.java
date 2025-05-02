package edu.games.engine.board;

import edu.games.engine.model.LudoColor;

public final class LudoRingTile implements LudoTile {
    private final int id;
    private LudoTile next;
    private LudoTile goalEntry;

    public LudoRingTile(int id) {
        this.id = id;
    }

    @Override 
    public int id() {
        return id;
    }

    /* package‑private setters used by LudoPath while wiring the graph */
    public void next(LudoTile next)         {
        this.next = next;
    }

    public void goalEntry(LudoTile goalEntry)    {
        this.goalEntry = goalEntry;
    }

    @Override
    public LudoTile next(LudoColor owner) {
        return goalEntry != null && ownerSquare(owner) ? goalEntry : next;
    }

    /** true if this square is the owner’s entry into goal lane */
    private boolean ownerSquare(LudoColor owner) {
        return switch (owner) {
            case BLUE   -> id == 0;
            case RED    -> id == 13;
            case GREEN  -> id == 26;
            case PURPLE -> id == 39;
        };
    }
}