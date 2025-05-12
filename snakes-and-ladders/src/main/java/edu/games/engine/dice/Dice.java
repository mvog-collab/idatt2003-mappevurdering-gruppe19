package edu.games.engine.dice;

import java.util.List;

public interface Dice {
  int roll();

  List<Integer> lastValues();
}
