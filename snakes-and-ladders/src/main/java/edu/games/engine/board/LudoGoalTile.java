package edu.games.engine.board;

import edu.games.engine.model.LudoColor;

public final class LudoGoalTile implements LudoTile {
    private final int id;
    private LudoTile next;

    public LudoGoalTile(int id) {
        this.id = id;
    }

    @Override 
    public int id() {
        return id;
    }

    void linkNext(LudoTile n) {
        this.next = n;
    }

    LudoTile next() {
        return next;
    }

    @Override
    public LudoTile next(LudoColor ownerIgnored) {
        return next;
    }
}
