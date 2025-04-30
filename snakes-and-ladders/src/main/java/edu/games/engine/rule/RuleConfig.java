package edu.games.engine.rule;

public record RuleConfig(ExtraTurnPolicy extraTurn) {

    public enum ExtraTurnPolicy {
        NONE,               // never grant an extra turn
        EVEN_BUT_NOT_12,    // the current behaviour
        ON_SIX              // classic “roll a 6 → roll again”
    }

    /** default = EVEN_BUT_NOT_12 so current games keep working */
    public RuleConfig() {
        this(ExtraTurnPolicy.EVEN_BUT_NOT_12);
    }
}