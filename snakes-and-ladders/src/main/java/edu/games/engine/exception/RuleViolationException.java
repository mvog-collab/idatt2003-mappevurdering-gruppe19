package edu.games.engine.exception;

/**
 * Exception thrown when a player or action violates a game rule.
 * Typically used to signal illegal moves or forbidden states.
 */
public class RuleViolationException extends GameEngineException {

  /**
   * Creates a new rule violation with a message.
   *
   * @param message the explanation of the rule violation
   */
  public RuleViolationException(String message) {
    super(message);
  }

  /**
   * Creates a new rule violation with a message and underlying cause.
   *
   * @param message the explanation of the rule violation
   * @param cause the original exception that caused this
   */
  public RuleViolationException(String message, Throwable cause) {
    super(message, cause);
  }
}
