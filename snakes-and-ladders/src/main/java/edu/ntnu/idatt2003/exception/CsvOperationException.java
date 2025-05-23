package edu.ntnu.idatt2003.exception;

/**
 * Thrown to indicate an error occurred during CSV input/output operations,
 * such as reading from or writing to a CSV file.
 */
public class CsvOperationException extends ApplicationException {

    /**
     * Constructs a new CsvOperationException with the specified detail message.
     *
     * @param message the detail message describing the CSV error
     */
    public CsvOperationException(String message) {
        super(message);
    }

    /**
     * Constructs a new CsvOperationException with the specified detail message
     * and cause of the failure.
     *
     * @param message the detail message describing the CSV error
     * @param cause   the underlying cause of this exception
     */
    public CsvOperationException(String message, Throwable cause) {
        super(message, cause);
    }
}
