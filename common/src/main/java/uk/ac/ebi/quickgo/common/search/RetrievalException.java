package uk.ac.ebi.quickgo.common.search;

/**
 * Exception that should be thrown whenever there is an issue retrieving data
 * from a data source.
 */
public class RetrievalException extends RuntimeException {

    public RetrievalException(Throwable cause) {
        super(cause);
    }

    public RetrievalException(String message) {
        super(message);
    }

    public RetrievalException(String message, Throwable cause) {
        super(message, cause);
    }
}
