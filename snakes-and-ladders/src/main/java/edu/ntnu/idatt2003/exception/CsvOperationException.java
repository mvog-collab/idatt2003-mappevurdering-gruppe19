package edu.ntnu.idatt2003.exception;

public class CsvOperationException extends ApplicationException {
    public CsvOperationException(String message, Throwable cause) {
        super(message, cause);
    }

    public CsvOperationException(String message) {
        super(message);
    }
}