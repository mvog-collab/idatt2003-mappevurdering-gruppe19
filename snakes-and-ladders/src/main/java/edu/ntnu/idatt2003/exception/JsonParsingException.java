package edu.ntnu.idatt2003.exception;

/**
 * Indicates an error occurred while parsing JSON content,
 * such as invalid syntax or missing fields.
 */
public class JsonParsingException extends ApplicationException {

    /**
     * Constructs a new JsonParsingException with the specified detail message
     * and underlying cause.
     *
     * @param message a description of the JSON parsing error
     * @param cause   the root exception thrown during parsing
     */
    public JsonParsingException(String message, Throwable cause) {
        super(message, cause);
    }
}
