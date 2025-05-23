package edu.ntnu.idatt2003.presentation.service.dice;

import javafx.scene.layout.Pane;

public interface DiceService {
  void initializeDice(Pane container);

  void showDice(Pane container, int[] values);

  int[] parseDiceRoll(Object diceData);
}
