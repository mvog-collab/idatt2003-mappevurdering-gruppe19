package edu.ntnu.idatt2003.gateway;

/**
 * Aggregates all responsibilities required by a complete board game gateway,
 * combining setup, player management, state inspection, and gameplay actions.
 * <p>
 * Implementations of this interface provide a unified API for:
 * <ul>
 *   <li>Creating and resetting games ({@link GameSetup}).</li>
 *   <li>Adding, loading, and clearing players ({@link PlayerManagement}).</li>
 *   <li>Querying game state such as current player, dice values, overlays, and winners ({@link GameState}).</li>
 *   <li>Driving gameplay actions like rolling dice and moving pieces ({@link GamePlay}).</li>
 * </ul>
 */
public interface CompleteBoardGame extends GameSetup, PlayerManagement, GameState, GamePlay {
  // No additional methods; combines all game gateway facets into one interface.
}
