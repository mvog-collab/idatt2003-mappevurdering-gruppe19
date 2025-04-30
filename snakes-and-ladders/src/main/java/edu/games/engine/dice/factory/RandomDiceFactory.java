package edu.games.engine.dice.factory;

import edu.games.engine.dice.Dice;
import edu.games.engine.dice.RandomDice;

public class RandomDiceFactory implements DiceFactory {

    private final int dice;

    public RandomDiceFactory() {
        this.dice = 2;
    }

    public RandomDiceFactory(int dice) {
        this.dice = dice;
    }

    @Override
    public Dice create() {
        return new RandomDice(dice);
    }
    
}
