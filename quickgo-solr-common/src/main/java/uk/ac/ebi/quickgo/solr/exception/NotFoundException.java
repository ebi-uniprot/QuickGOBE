package uk.ac.ebi.quickgo.solr.exception;

/**
 * Exception thrown when an entry is not found in Solr
 * @author cbonill
 *
 */
public class NotFoundException extends Exception{

	private static final long serialVersionUID = 7869574537208074332L;

	public NotFoundException(String message) {
        super(message);
    }

    public NotFoundException(String message, Throwable throwable) {
        super(message, throwable);
    }	
}