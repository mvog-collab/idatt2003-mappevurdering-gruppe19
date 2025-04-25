package edu.games.engine;

import java.util.Random;

public class DiceService {
    private final int dice;
    private final Random random;

    DiceService(int dice) {
        this.dice = dice;
        this.random = new Random();
    }

    DiceService(int dice, Random random) {
        if (dice < 1) throw new IllegalArgumentException("dice < 1");
        this.dice = dice; this.random = random;
      }

      int roll() {
        int sum = 0;
        for (int i = 0; i < dice; i++) {
            sum += random.nextInt(1, 7);
        }
        return sum;
      }
}
