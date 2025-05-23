package edu.ntnu.idatt2003.exception;

/**
 * A generic runtime exception thrown by the application for unrecoverable errors
 * in the UI layer or business logic that do not fit into more specific exception types.
 */
public class ApplicationException extends RuntimeException {

    /**
     * Constructs a new ApplicationException with the specified detail message.
     *
     * @param message the detail message explaining the reason for the exception
     */
    public ApplicationException(String message) {
        super(message);
    }

    /**
     * Constructs a new ApplicationException with the specified detail message and cause.
     *
     * @param message the detail message explaining the reason for the exception
     * @param cause   the underlying cause of this exception
     */
    public ApplicationException(String message, Throwable cause) {
        super(message, cause);
    }
}
