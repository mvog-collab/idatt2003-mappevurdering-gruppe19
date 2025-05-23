package edu.ntnu.idatt2003.exception;

/**
 * Thrown when a required resource (e.g., file, image, configuration)
 * cannot be located at the specified path.
 */
public class ResourceNotFoundException extends ApplicationException {

    /**
     * Constructs a new ResourceNotFoundException with a detail message
     * indicating the missing resource path, and the underlying cause.
     *
     * @param resourcePath the path of the resource that was not found
     * @param cause        the root exception causing this error, if any
     */
    public ResourceNotFoundException(String resourcePath, Throwable cause) {
        super("Resource not found: " + resourcePath, cause);
    }

    /**
     * Constructs a new ResourceNotFoundException with a detail message
     * indicating the missing resource path.
     *
     * @param resourcePath the path of the resource that was not found
     */
    public ResourceNotFoundException(String resourcePath) {
        super("Resource not found: " + resourcePath);
    }
}
