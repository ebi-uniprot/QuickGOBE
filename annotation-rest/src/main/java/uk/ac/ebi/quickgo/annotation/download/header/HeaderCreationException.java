package uk.ac.ebi.quickgo.annotation.download.header;

/**
 * Class whose use indicates a problem occurred whilst creating a file download header section.
 *
 * Created 31/01/17
 * @author Edd
 */
class HeaderCreationException extends RuntimeException {
    HeaderCreationException() {
        super();
    }

    HeaderCreationException(String message) {
        super(message);
    }

    HeaderCreationException(String message, Throwable cause) {
        super(message, cause);
    }
}
