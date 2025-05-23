package edu.games.engine.exception;

/**
 * Exception thrown when validation of game data or rules fails.
 * Typically used to signal invalid input, configuration, or state.
 */
public class ValidationException extends GameEngineException {

  /**
   * Constructs a new validation exception with a specific error message.
   *
   * @param message the detail message explaining the validation failure
   */
  public ValidationException(String message) {
    super(message);
  }

  /**
   * Constructs a new validation exception with a message and underlying cause.
   *
   * @param message the detail message
   * @param cause the original cause of the failure
   */
  public ValidationException(String message, Throwable cause) {
    super(message, cause);
  }
}
