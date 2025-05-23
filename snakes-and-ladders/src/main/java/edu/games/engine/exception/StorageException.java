package edu.games.engine.exception;

/**
 * Exception thrown when an error occurs during data storage or retrieval.
 * Typically used for persistence-related failures such as saving or loading game state.
 */
public class StorageException extends GameEngineException {

  /**
   * Creates a new storage exception with a descriptive message.
   *
   * @param message the explanation of the storage error
   */
  public StorageException(String message) {
    super(message);
  }

  /**
   * Creates a new storage exception with a message and underlying cause.
   *
   * @param message the explanation of the storage error
   * @param cause the original exception that triggered this
   */
  public StorageException(String message, Throwable cause) {
    super(message, cause);
  }
}
