package edu.ntnu.idatt2003.gateway;

/**
 * Defines the core gameplay actions that a client can invoke on a game gateway.
 * <p>
 * Extends {@link GameGateway} to inherit observer registration methods, ensuring
 * that clients can both subscribe to game events and trigger gameplay.
 */
public interface GamePlay extends GameGateway {

  /**
   * Rolls the game’s dice and returns the result.
   * <p>
   * When invoked, this method should perform any necessary game-logic checks
   * (e.g. whether the game is initialized or has a winner) before rolling.
   *
   * @return the sum of the dice roll, or zero if rolling is not permitted
   */
  int rollDice();
}
