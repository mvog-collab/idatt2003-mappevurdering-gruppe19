package edu.games.engine.exception;

/**
 * Base exception type for all game engine-related runtime errors.
 * Used to indicate something went wrong internally.
 */
public class GameEngineException extends RuntimeException {

  /**
   * Creates a new exception with a message.
   *
   * @param message the error message
   */
  public GameEngineException(String message) {
    super(message);
  }

  /**
   * Creates a new exception with a message and a cause.
   *
   * @param message the error message
   * @param cause the underlying cause
   */
  public GameEngineException(String message, Throwable cause) {
    super(message, cause);
  }
}
