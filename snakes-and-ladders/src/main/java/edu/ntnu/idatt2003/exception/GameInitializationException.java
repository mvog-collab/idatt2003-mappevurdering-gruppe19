package edu.ntnu.idatt2003.exception;

/**
 * Signals a failure during the initialization of a game session,
 * such as errors setting up board, players, or resources.
 */
public class GameInitializationException extends ApplicationException {

    /**
     * Constructs a new GameInitializationException with the specified detail message.
     *
     * @param message a description of the initialization failure
     */
    public GameInitializationException(String message) {
        super(message);
    }

    /**
     * Constructs a new GameInitializationException with the specified detail message
     * and underlying cause.
     *
     * @param message a description of the initialization failure
     * @param cause   the root exception that triggered this failure
     */
    public GameInitializationException(String message, Throwable cause) {
        super(message, cause);
    }
}
