package edu.ntnu.idatt2003.exception;

public class GameInitializationException extends ApplicationException {
    public GameInitializationException(String message) {
        super(message);
    }

    public GameInitializationException(String message, Throwable cause) {
        super(message, cause);
    }
}