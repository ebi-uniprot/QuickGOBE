package uk.ac.ebi.quickgo.common.service;

/**
 * Represents a {@link RuntimeException} that is thrown whenever there
 * is a problem during the configuration of the service layer.
 *
 * Created 09/02/16
 * @author Edd
 */
public class ServiceConfigException extends RuntimeException {
    public ServiceConfigException(Throwable cause) {
        super(cause);
    }

    public ServiceConfigException(String message) {
        super(message);
    }

    public ServiceConfigException(String message, Throwable cause) {
        super(message, cause);
    }
}
