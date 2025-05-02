package edu.games.engine.board;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import edu.games.engine.model.LudoColor;

// ----------------------------------------------------------------------------
// LudoPath – builds a coloured graph of 52 + 4×6 squares
// ----------------------------------------------------------------------------
public final class LudoPath implements MovementPath {

    private final List<LudoRingTile> ring = new ArrayList<>(52);
    private final Map<LudoColor,List<LudoGoalTile>> goals = new EnumMap<>(LudoColor.class);

    public LudoPath() {

        /* 1) build 52‑square ring */
        for (int i = 0; i < 52; i++) ring.add(new LudoRingTile(i));
        for (int i = 0; i < 52; i++) ring.get(i).next(ring.get((i + 1) % 52));

        /* 2) build 4×6 goal lanes */
        for (LudoColor c : LudoColor.values()) {

            List<LudoGoalTile> lane = new ArrayList<>(6);
            for (int j = 0; j < 6; j++)
                lane.add(new LudoGoalTile(52 + c.ordinal()*6 + j));

            for (int j = 0; j < 5; j++)
                lane.get(j).linkNext(lane.get(j + 1));

            goals.put(c, lane);

            int entryIdx = switch (c) { case BLUE->0; case RED->13; case GREEN->26; case PURPLE->39; };
            ring.get(entryIdx).goalEntry(lane.getFirst());
        }
    }

    /* MovementPath -------------------------------------------------- */

    @Override public Tile start() { return null; }

    @Override public Tile next(Tile from,int steps) {
        throw new UnsupportedOperationException("Use next(Tile,int,LudoColor)");
    }

    public Tile next(Tile from,int steps,LudoColor owner) {
        if (from == null) {          // token enters board
            return ring.get(switch(owner){case BLUE->0; case RED->13; case GREEN->26; case PURPLE->39;});
        }
        LudoTile t = (LudoTile) from;
        for (int i = 0; i < steps; i++) t = t.next(owner);
        return t;
    }

    @Override
    public boolean isEnd(Tile tile) {
        return (tile instanceof LudoGoalTile g) && g.next() == null;
    }
}