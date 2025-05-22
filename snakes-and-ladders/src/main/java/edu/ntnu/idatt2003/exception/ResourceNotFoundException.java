package edu.ntnu.idatt2003.exception;

public class ResourceNotFoundException extends ApplicationException {
    public ResourceNotFoundException(String resourcePath, Throwable cause) {
        super("Resource not found: " + resourcePath, cause);
    }

    public ResourceNotFoundException(String resourcePath) {
        super("Resource not found: " + resourcePath);
    }
}