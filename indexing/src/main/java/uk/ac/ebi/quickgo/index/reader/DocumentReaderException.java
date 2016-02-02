package uk.ac.ebi.quickgo.index.reader;

import uk.ac.ebi.quickgo.common.QuickGODocument;

/**
 * Instances of this class are thrown to indicate a problem during the reading of a
 * {@link QuickGODocument}.
 *
 * Created 11/01/16
 * @author Edd
 */
public class DocumentReaderException extends RuntimeException {
    public DocumentReaderException(String message) {
        super(message);
    }
}
