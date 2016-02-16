package uk.ac.ebi.quickgo.rest;

/**
 * Exception that is thrown to indicate a RESTful resource was not found.
 *
 * Created 16/02/16
 * @author Edd
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(Throwable cause) {
        super(cause);
    }

    public ResourceNotFoundException(String message) {
        super(message);
    }

    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
