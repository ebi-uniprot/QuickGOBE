package uk.ac.ebi.quickgo.repowriter.reader;

/**
 * Instances of this class are thrown to indicate a problem during the reading of a
 * {@link uk.ac.ebi.quickgo.document.QuickGODocument}.
 *
 * Created 11/01/16
 * @author Edd
 */
public class DocumentReaderException extends RuntimeException {
    public DocumentReaderException(String message) {
        super(message);
    }
}
