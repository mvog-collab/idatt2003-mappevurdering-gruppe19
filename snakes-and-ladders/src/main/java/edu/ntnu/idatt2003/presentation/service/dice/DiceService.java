package edu.ntnu.idatt2003.presentation.service.dice;

import javafx.scene.layout.Pane;

/**
 * Interface for dice display and management services.
 * <p>
 * Handles dice initialization, display updates, and data parsing
 * for different game types and dice configurations.
 * </p>
 */
public interface DiceService {
  /**
   * Initializes dice display in the specified container.
   *
   * @param container the pane to add dice to
   */
  void initializeDice(Pane container);

  /**
   * Updates dice display with the specified values.
   *
   * @param container the container holding the dice
   * @param values    array of dice values to display
   */
  void showDice(Pane container, int[] values);

  /**
   * Parses dice roll data from the game model.
   *
   * @param diceData the raw dice data to parse
   * @return array of parsed dice values
   */
  int[] parseDiceRoll(Object diceData);
}