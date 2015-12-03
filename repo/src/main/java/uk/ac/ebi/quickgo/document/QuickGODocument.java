package uk.ac.ebi.quickgo.document;

/**
 * Created 02/12/15
 * @author Edd
 */
public interface QuickGODocument {
    /**
     * Returns a unique name with which one can identify a document. This is not used
     * by the repository layer, but is used during logging to help compute statistics
     * or identify errors.
     *
     * @return
     */
    String getUniqueName();
}
