package uk.ac.ebi.quickgo.common;

/**
 * Created 02/12/15
 * @author Edd
 */
public interface QuickGODocument {
    /**
     * Returns a unique name with which one can identify a document.
     *
     * @return a unique document name
     */
    String getUniqueName();
}
